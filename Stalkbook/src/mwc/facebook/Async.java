package mwc.facebook;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mwc.facebook.data.DataStore;
import mwc.facebook.data.Location;
import mwc.facebook.data.Point;
import mwc.facebook.data.User;

public class Async extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name = (String)request.getAttribute("user");
		float x = Float.parseFloat((String)request.getAttribute("x"));
		float y = Float.parseFloat((String)request.getAttribute("y"));
		
		Point point = new Point(x, y);
		
		DataStore store = ObjectManager.instance().store();
		
		User user = store.getUserByName(name);
		if (user == null) {
			user = new User(name, point);
			store.addUser(user);
		}
		
		Location location = store.getLocationByPoint(point);
		if (location == null) {
			location = new Location(point, "location");
		}
		store.addUserToLocation(user, location);
		
		response.getWriter().println("success");
	}

}
