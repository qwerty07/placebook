package mwc.facebook;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.facebook.api.FacebookException;
import com.facebook.api.FacebookRestClient;

import mwc.Stalkbook;
import mwc.facebook.data.CommentContribution;
import mwc.facebook.data.DataStore;
import mwc.facebook.data.Location;
import mwc.facebook.data.PhotoContribution;
import mwc.facebook.data.Point;
import mwc.facebook.data.Rectangle;
import mwc.facebook.data.User;

public class Async extends HttpServlet {

	private static final long serialVersionUID = 1l;
	
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getParameter("action");
		String name = request.getParameter("user");
		String locName = request.getParameter("location");
		String locDesc = request.getParameter("description");
		String sx = request.getParameter("x");
		String sy = request.getParameter("y");
		String sx2 = request.getParameter("x2");
		String sy2 = request.getParameter("y2");
		
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
		else if (action.equals("addUserToLocation") && name != null && sx != null && sy != null) {
			if (addUserToLocation(name, sx, sy)) {
				try { updateProfile(request); } catch (Exception e) {e.printStackTrace();}
				response.getWriter().println("success");
				return;
			}
		
		}
		else if (action.equals("getLocationsByRec") && sx != null && sy != null  && sx2 != null && sy2 != null) {
			if (getLocationsByRec(sx, sy, sx2, sy2, response.getWriter())) {
				return;
			}
		
		}
		
		
		response.getWriter().println("fail");
	}
	
	private boolean setDefaultLocation(String user, String sx, String sy) {
		
		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);

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
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);

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
			store.addUserToLocation(user, location);
			
			System.out.println("added point: " + locName+ ", " + x + ", " + y);

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		
		return false;
	}
	
	
	private boolean addUserToLocation(String name, String sx, String sy) {
		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			User user = store.getUserById(name);
			
			if (user == null) {
				return false;
			}

			Location location = store.getLocationByPoint(point);
			if (location == null) {
				return false;
				//location = new Location(point, locName, locDesc);
				//store.addLocation(location);
			}
			store.addUserToLocation(user, location);
			
			System.out.println("added user to: " + location.getLocationName() + ", " + x + ", " + y);

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		
		return false;
	}
	
	
	
	public void updateProfile(HttpServletRequest request) throws FacebookException, IOException{

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
			
			text += "<a href=\"http://apps.facebook.com/stalkbook/\"><fb:name uid=\"" +username + "\" firstnameonly=true useyou=true possessive=true capitalize=true /> home</a>";
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
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			Location location = store.getLocationByPoint(point);
			if (location == null) {
				System.err.println("request for non-existant location: " + x + ", " + y);
				return false;
			}

			writer.println("{ ");
			writer.println("coordinates: " +location.getCoordinates().toJSON() + ", ");
			writer.println("locationName: \"" + location.getLocationName() + "\", ");
			writer.println("description: \"" + location.getDescription() + "\", ");
			writer.println("users: [ ");
			Set<User> users = store.usersAssociatedWith(location);
			for (User user: users) {
				writer.println(user.toJSON());
			}
			writer.println(" ]");
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
	
	
	private boolean getLocationsByRec(
			String sTopLeftx, String sTopLefty, String sBottomRightx, String sBottomRighty, 
			PrintWriter writer) {

		try {
			float tlx = Float.parseFloat(sTopLeftx);
			float tly = Float.parseFloat(sTopLefty);
			
			float brx = Float.parseFloat(sBottomRightx);
			float bry = Float.parseFloat(sBottomRighty);

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
}
 
