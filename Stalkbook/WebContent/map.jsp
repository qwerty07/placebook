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

if (user == null) {
	user = new User(String.valueOf(uid), new Point(-41.26, 174.77));
	db.addUser(user);
}
userHome = user.getHomePoint();
userLocations = db.locationsFor(user);

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Insert title here</title>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAjLUGBU2KYPSE-QiX4gc3nhQaIaxgZFO_HyGm4sK6rU1DjshnHBQZEhZXdK-FSkrkhldgO6eTm6dXRw"></script>
<script src="gmapIO.js" type="text/javascript"></script>
<script src="ajax.js" type="text/javascript"></script>
<script src="StalkCoordinator.js" type="text/javascript"></script>
<style type="text/css">
#map {
	width: 500px; height: 500px
}
</style>

<script type="text/javascript">
	var User = {
		username: "<%= uid %>",
		<% if (userHome != null) { %>
		homeLocation: { x: <%= userHome.x %>, y: <%= userHome.y %> },
		<% } %>
	    locations: [
	    <% if (userLocations != null) {
	    	for (Location location : userLocations) {
	    		Point coords = location.getCoordinates();
	    		out.println("{ name: '" + location.getLocationName() +
	    	    	          "', x: " + coords.x + ", y: " + coords.y + "},");
	    	}
	    } %>
	    ]
	};
</script>

</head>
 <body onload="StalkCoordinator.load()" onunload="GUnload()">
<div id="map"></div>


</body>
</html>