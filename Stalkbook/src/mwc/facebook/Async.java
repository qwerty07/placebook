package mwc.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.output.ByteArrayOutputStream;

import mwc.Stalkbook;
import mwc.facebook.data.CommentContribution;
import mwc.facebook.data.DataStore;
import mwc.facebook.data.Location;
import mwc.facebook.data.PhotoContribution;
import mwc.facebook.data.Point;
import mwc.facebook.data.Rectangle;
import mwc.facebook.data.User;

import com.facebook.api.FacebookException;
import com.facebook.api.FacebookRestClient;

public class Async extends HttpServlet {

	private static final long serialVersionUID = 1l;
	
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action, name, locName, locDesc, comment,
				sx, sy, sx2, sy2;
		
		if (ServletFileUpload.isMultipartContent(request)) {
			Map<String, String> parameters;
			try {
				parameters = getMultipartParameters(request);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("No parameters available for nultipart async request, aborting");
				return;
			} catch (FileUploadException e) {
				e.printStackTrace();
				System.err.println("No parameters available for nultipart async request, aborting");
				return;
			}
			action = parameters.get("action");
			name = parameters.get("name");
			locName = parameters.get("location");
			locDesc = parameters.get("description");
			comment = parameters.get("comment");
			sx = parameters.get("x");
			sy = parameters.get("y");
			sx2 = parameters.get("x2");
			sy2 = parameters.get("y2");
		} else {
			action = request.getParameter("action");
			name = request.getParameter("user");
			locName = request.getParameter("location");
			locDesc = request.getParameter("description");
		    comment = request.getParameter("comment");
			sx = request.getParameter("x");
			sy = request.getParameter("y");
			sx2 = request.getParameter("x2");
			sy2 = request.getParameter("y2");
		}
		
		if (action.equals("setdefault") && name != null && sx != null && sy != null) {
			if (setDefaultLocation(name, sx, sy)) {
				response.getWriter().println("success");
				return;
			}
		}
		else if (action.equals("addlocation") && name != null && locName != null && locDesc != null
					&& sx != null && sy != null) {
			if (addLocation(name, locName, locDesc, sx, sy)) {
				try { updateProfile(request); } catch (Exception e) {e.printStackTrace();}
				response.getWriter().println("success");
				return;
			}
		}
		else if (action.equals("getlocation") && sx != null && sy != null) {
			if (getLocationData(sx, sy, response.getWriter())) {
				return;
			}
		}
		else if (action.equals("joinlocation") && name != null && sx != null && sy != null) {
			if (joinLocation(name, sx, sy)) {
				try { updateProfile(request); } catch (Exception e) {e.printStackTrace();}
				response.getWriter().println("success");
				return;
			}
		
		}
		else if (action.equals("getLocationsByRec") && sx != null && sy != null  && sx2 != null && sy2 != null) {
			if (getLocationsByRec(sx, sy, sx2, sy2, response.getWriter())) {
				return;
			}
		} else if (action.equals("addphoto")) {
			if (addPhoto(name, sx, sy, locDesc, request)) {
				response.getWriter().println("success");
				return;
			}
		}		
		else if (action.equals("getuserlocrec") && sx != null && sy != null  && sx2 != null && sy2 != null && name !=null) {
			if (getUserLocationsByRec(sx, sy, sx2, sy2,name, response.getWriter())) {
				return;
			}
		
		}
		else if (action.equals("addcomment") && name !=null &&sx != null && sy != null && comment != null) {
			if (addCommentToLocation(name, sx, sy, comment)) {
				return;
			}
		}

		response.getWriter().println("fail");
	}
	
	private boolean setDefaultLocation(String user, String sx, String sy) {
		
		try {
			double x = Double.parseDouble(sx);
			double y = Double.parseDouble(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			User u = store.getUserById(user);
			u.setHomePoint(point);
			store.updateUser(u);
			
			System.out.println("set default location for " + u.getUser() + ": " + x + ", " + y);
			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return false;
	}
	
	private boolean addLocation(String name, String locName, String locDesc, String sx, String sy) {
		
		try {
			double x = Double.parseDouble(sx);
			double y = Double.parseDouble(sy);
			
			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			User user = store.getUserById(name);
			
			if (user == null) {
				return false;
			}

			Location location = store.getLocationByPoint(point);
			if (location == null) {
				location = new Location(point, locName, locDesc);
				store.addLocation(location);
			}
			
			System.out.println("added point: " + locName+ ", " + x + ", " + y);

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		
		return false;
	}
	
	private boolean joinLocation(String u, String sx, String sy) {
		
		try {
			double x = Double.parseDouble(sx);
			double y = Double.parseDouble(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			User user = store.getUserById(u);
			if (user == null) {
				return false;
			}

			Location location = store.getLocationByPoint(point);
			if (location == null) {
				return false;
			}
			
			store.addUserToLocation(user, location);
			
			System.out.println("added user to location: " + location.getLocationName() + ", " + user.getName());

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		
		return false;
	}
	
	private void updateProfile(HttpServletRequest request) throws FacebookException, IOException{

		FacebookRestClient client = Stalkbook.getClient();
		client.setDebug(true);
		// int uid = client.users_getLoggedInUser();

		String username = request.getParameter("user");
		int uid = Integer.valueOf(username);
		
		DataStore db = ObjectManager.instance().store();

		User user = db.getUserById(username); 
		Point userHome = null;
		Set<Location> userLocations = null;
		Set<PhotoContribution> userPhotos = db.getPhotosFrom(user);
		Set<CommentContribution> userComments = db.getCommentsFrom(user);
		String text = "";
		
		if (user != null) {
			userHome = user.getHomePoint();
			userLocations = db.locationsFor(user);
			//fbmlMarkup.append("These are the uber places");
			
			String verbField = "<fb:if-is-own-profile> have <fb:else> has </fb:else></fb:if-is-own-profile>";
			String nameField ="<fb:name uid=\"" + username + "\" firstnameonly=true useyou=true capitalize=true />"; 

			text = nameField + verbField + "posted " + userComments.size() + " comments and " + userPhotos.size() + " photos<br>";
			
			text += "<a href=\"http://apps.facebook.com/stalkbook/?x=" + userHome.x + "&y=" + userHome.y + "\"><fb:name uid=\"" +username + "\" firstnameonly=true useyou=true possessive=true capitalize=true /> home</a>";
			text += " is at (" + userHome.x + ", " + userHome.y + ")<br>";

			text += nameField + "<fb:if-is-own-profile> are <fb:else> is </fb:else></fb:if-is-own-profile>associated with ";
			text += userLocations.size() + " locations<br>";
		} else {
			text = "You have no places moooo!!!!";
		}

		client.profile_setFBML(text, uid);
	}

	private boolean getLocationData(String sx, String sy, PrintWriter writer) {

		try {
			double x = Double.parseDouble(sx);
			double y = Double.parseDouble(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			Location location = store.getLocationByPoint(point);
			if (location == null) {
				System.err.println("request for non-existant location: " + x + ", " + y);
				return false;
			}

			writer.println("{ ");
			writer.println("coordinates: " +location.getCoordinates().toJSON() + ", ");
			writer.println("locationName: \"" + Location.escapeString(location.getLocationName()) + "\", ");
			writer.println("description: \"" + Location.escapeString(location.getDescription()) + "\", ");
			
			writer.println("users: [ ");
			Set<User> users = store.usersAssociatedWith(location);
			for (User user: users) {
				writer.println(user.toJSON() + ", ");
			}
			writer.println(" ],");
			
			writer.println("comments: [ ");
			Set<CommentContribution> comments = store.getCommentsFrom(location);
			for (CommentContribution comment: comments) {
				writer.println(comment.toJSON() + ", ");
				
			}
			writer.println(" ],");
			
			writer.println("photos: [ ");
			Set<PhotoContribution> photos = store.getPhotosFrom(location);
			for (PhotoContribution photo: photos) {
				writer.println(photo.toJSON() + ", ");				
			}
			writer.println(" ],");
			
			writer.println("}");

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}
	
	private boolean addCommentToLocation(String name, String sx, String sy, String comment) {
		
		try {
			double x = Double.parseDouble(sx);
			double y = Double.parseDouble(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			User user = store.getUserById(name);
			
			if (user == null) {
				return false;
			}

			Location location = store.getLocationByPoint(point);
			if (location == null) {
				return false;
			}
			
			store.addCommentTo(user, location, comment);

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		
		return false;
	}
	
	
	private boolean getLocationsByRec(
			String sTopLeftx, String sTopLefty, String sBottomRightx, String sBottomRighty, 
			PrintWriter writer) {

		try {
			double tlx = Double.parseDouble(sTopLeftx);
			double tly = Double.parseDouble(sTopLefty);
			
			double brx = Double.parseDouble(sBottomRightx);
			double bry = Double.parseDouble(sBottomRighty);

			Point tlPoint = new Point(tlx, tly);
			Point brPoint = new Point(brx, bry);
			Rectangle rectangle = new Rectangle(tlPoint, brPoint);
			
			DataStore store = ObjectManager.instance().store();

			Set<Location> locations = store.getLocationsWithin(rectangle);
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("[ ");
			
			for(Location l : locations){
				sb.append(l.toJSON());
				sb.append(",");
			}
			
			sb.append(" ]");
						
			writer.println(sb);

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point");
		}

		return false;
	}
	
	private boolean addPhoto(String p_user, String sx, String sy,
							String description,
							HttpServletRequest request) {
		try {
			if (!ServletFileUpload.isMultipartContent(request)) {
				return false;
			}
			
			Float x = Float.valueOf(sx);
			Float y = Float.valueOf(sy);
			
			DataStore store = ObjectManager.instance().store();

			User user = store.getUserById(p_user);
			Location location = store.getLocationByPoint(new Point(x,y));
			
			ByteArrayOutputStream photoStream = new ByteArrayOutputStream();
			
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				if (!item.isFormField()) {
					// File field
					InputStream formStream = item.openStream();					
					Streams.copy(formStream, photoStream, true);
					store.addPhotoTo(user, location, photoStream.toByteArray(), description);
					break;
				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean getUserLocationsByRec(
			String sTopLeftx, String sTopLefty, String sBottomRightx, String sBottomRighty, String name,
			PrintWriter writer) {

		try {
			double tlx = Double.parseDouble(sTopLeftx);
			double tly = Double.parseDouble(sTopLefty);
			
			double brx = Double.parseDouble(sBottomRightx);
			double bry = Double.parseDouble(sBottomRighty);

			Point tlPoint = new Point(tlx, tly);
			Point brPoint = new Point(brx, bry);
			Rectangle rectangle = new Rectangle(tlPoint, brPoint);
			
			DataStore store = ObjectManager.instance().store();

			User user = store.getUserById(name);
			if(user == null){
				return false;	
			}
			
			Set<Location> locations = store.getLocationsWithin(rectangle);
			Set<Location> userLocations = store.locationsFor(user);
			Set<Location> userRecLoc=new HashSet<Location>();
			for(Location location:locations){
				if(userLocations.contains(location))userRecLoc.add(location);
			}
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("[ ");
			
			for(Location l : userRecLoc){
				sb.append(l.toJSON());
				sb.append(",");
			}
			
			sb.append(" ]");
						
			writer.println(sb);

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point");
		}

		return false;
	}
	
	private Map<String,String> getMultipartParameters(HttpServletRequest request) 
		throws IOException, FileUploadException {

		Map<String, String> parameters = new HashMap<String,String>();;
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter = upload.getItemIterator(request);		
		
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			if (item.isFormField()) {
				// Form field
				InputStream formStream = item.openStream();					
				String field = item.getFieldName();
				
				parameters.put(field, Streams.asString(formStream));
				System.out.printf("%s -> %s\n", field, parameters.get(field));
			}
		}

		return parameters;
		
	}

}
 
