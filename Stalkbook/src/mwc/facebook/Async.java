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
		doPost(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name = request.getParameter("user");
		String sx = request.getParameter("x");
		String sy = request.getParameter("y");
		
		if (name == null || sx == null || sy == null) {
			response.getWriter().println("fail");
			return;
		}
		
		float x = Float.parseFloat(sx);
		float y = Float.parseFloat(sy);
		
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
		
		//TODO return users associate with point 
		response.getWriter().println("success");
		
		
	}

}
