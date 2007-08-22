<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/javascript; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="com.facebook.api.*"
    import="mwc.*"
    import="mwc.facebook.data.*"
    import="mwc.facebook.ObjectManager"
    import="java.util.Set"%>

<%
FacebookRestClient client = Stalkbook.getClient(request);
int uid = client.users_getLoggedInUser();

DataStore db = ObjectManager.instance().store();

User user = db.getUserByName(String.valueOf(uid)); // someone will fix this later
Point userHome = null;
Set<Location> locations = null;

if (user != null) {
	userHome = user.getHomePoint();
	locations = db.getLocationsWithin(new Rectangle(new Point(-90, -180), new Point(90, 180)));
}

%>

var user = {
	username: "<%= uid %>",
<% if (userHome != null) { %>
	homeLocation: <%= userHome.toJSON() %>,
<% } %>
	locations: [
<% if (locations != null) {
		for (Location location : locations) {
			out.println(location.toJSON() + ",");
	}
} %>
	]
};

<% if (user == null) { %>

function doOnload() {

	stalkCoordinator = new StalkCoordinator();
	
	stalkCoordinator.addMarker = function (latlng) {
		var point = {
			x: latlng.lat(),
			y: latlng.lng()
		};
			
		user.homeLocation = point;
	
		async.setDefaultLocation(stalkCoordinator.username, point, function (req) { stalkCoordinator.complete(req) });
	};
	
	stalkCoordinator.load = function () {
		alert("Welcome to Stalkbook, please select your default location.");
			
		// load the map
		stalkBook.load();
		
		// Set the callback function for stalkBook to call when adding
		// a new marker
		stalkBook.setMarkerFunction(stalkCoordinator.addMarker);
		
		// get the user's Facebook name & home location
		this.setUsername(user.username);
	};
	
	stalkCoordinator.complete = function (req) {
		stalkCoordinator = new StalkCoordinator();
		stalkCoordinator.load();
	};
	
	stalkCoordinator.load();
}

<% } else { %>

function doOnload() {
	stalkCoordinator = new StalkCoordinator();
	stalkCoordinator.load();
}

<% } %>