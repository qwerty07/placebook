package mwc.facebook;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Set;

import com.facebook.api.FacebookException;
import com.facebook.api.FacebookRestClient;

import mwc.Stalkbook;
import mwc.facebook.data.DataStore;
import mwc.facebook.data.Location;
import mwc.facebook.data.Point;
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
			if (getLocation(sx, sy, response.getWriter())) {
				return;
			}
		}
		
		response.getWriter().println("fail");
	}
	
	private boolean setDefaultLocation(String name, String sx, String sy) {
		
		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			User user = store.getUserByName(name);
			if (user == null) {
				user = new User(name, point);
				store.addUser(user);
			}
			
			System.out.println("set default location for " + name + ": " + x + ", " + y);
			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}
		
		return false;
	}
	
	private boolean addLocation(String name, String locName, String locDesc, String sx, String sy) {
		
		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			User user = store.getUserByName(name);
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

	
	public void updateProfile(HttpServletRequest request) throws FacebookException, IOException{

		FacebookRestClient client = Stalkbook.getClient(request);
		client.setDebug(true);
		// int uid = client.users_getLoggedInUser();

		String username = request.getParameter("user");
		int uid = Integer.valueOf(username);
		
		DataStore db = ObjectManager.instance().store();

		User user = db.getUserByName(username); 
		Point userHome = null;
		Set<Location> userLocations = null;
		StringBuffer fbmlMarkup = new StringBuffer();
		
		if (user != null) {
			userHome = user.getHomePoint();
			userLocations = db.locationsFor(user);
			fbmlMarkup.append("These are the uber places");
		} else {
			fbmlMarkup.append("You have no places moooo!!!!");
		}

		client.profile_setFBML(fbmlMarkup.toString(), uid);
	}

	private boolean getLocation(String sx, String sy, PrintWriter writer) {

		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);

			Point point = new Point(x, y);

			DataStore store = ObjectManager.instance().store();

			Location location = store.getLocationByPoint(point);
			if (location == null) {
				return false;
			}

			writer.println(location.toJSON());

			return true;
		}
		catch (NumberFormatException ex) {
			System.err.println("error parsing point: " + sx + ", " + sy);
		}

		return false;
	}
}
