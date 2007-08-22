<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
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
Set<Location> userLocations = null;

if (user != null) {
	userHome = user.getHomePoint();
	userLocations = db.locationsFor(user);
}

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Facebook Places</title>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAjLUGBU2KYPSE-QiX4gc3nhQaIaxgZFO_HyGm4sK6rU1DjshnHBQZEhZXdK-FSkrkhldgO6eTm6dXRw"></script>
<script src="gmapIO.js" type="text/javascript"></script>
<script src="ajax.js" type="text/javascript"></script>
<script src="StalkCoordinator.js" type="text/javascript"></script>
<style type="text/css">
#map {
	width: 100%;
	height: 500px;
}
</style>

<script type="text/javascript">
var User = {
	username: "<%= uid %>",
<% if (userHome != null) { %>
	homeLocation: <%= userHome.toJSON() %>,
<% } %>
	locations: [
<% if (userLocations != null) {
		for (Location location : userLocations) {
			out.println(location.toJSON() + ",");
	}
} %>
	]
};

</script>
<%
if (user == null) {
%>
<script type="text/javascript">

function doOnload() {
	
	stalkCoordinator = new StalkCoordinator();
	
	stalkCoordinator.addMarker = function (latlng) {
		var point = {
			x: latlng.lat(),
			y: latlng.lng()
		};
			
		User.homeLocation = point;
	
		async.setDefaultLocation(stalkCoordinator.username, point, function (req) { stalkCoordinator.complete(req) });
	};
	
	stalkCoordinator.load = function () {
		alert("Welcome to Stalkbook, please select your default location.");
			
		// load the map
		StalkBook.load();
		
		// Set the callback function for StalkBook to call when adding
		// a new marker
		StalkBook.setMarkerFunction(stalkCoordinator.addMarker);
		
		// get the user's Facebook name & home location
		this.setUsername(User.username);
	};
	
	stalkCoordinator.complete = function (req) {
		stalkCoordinator = new StalkCoordinator();
		stalkCoordinator.load();
	};
	
	stalkCoordinator.load();
}
</script>
<%
}
else {
%>
<script type="text/javascript">

function doOnload() {
	
	stalkCoordinator = new StalkCoordinator();
	
	stalkCoordinator.load();
}
</script>
<%
}
%>
</head>
<body onload="doOnload()" onunload="GUnload()">
	<div id="map"></div>
</body>


</html>