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
String uid = String.valueOf(client.users_getLoggedInUser());

DataStore db = ObjectManager.instance().store();

String viewLocationX = request.getParameter("x");
String viewLocationY = request.getParameter("y");

try {
	Float.valueOf(viewLocationX);
	Float.valueOf(viewLocationY);
} catch (Exception e) {
	viewLocationX = null;
	viewLocationY = null;
}

//TODO error handling for non-existent users
//Useful to pretend that they exist for testing
User user = db.getUserById(uid);
Point userHome = null;

{
	Set<CharSequence> fields = new TreeSet<CharSequence>();
	fields.add("name");
	fields.add("pic_small");
	Collection<Integer> ids = new Vector<Integer>();
	ids.add(Integer.parseInt(uid));
	Document document = Stalkbook.getClient(request).users_getInfo(ids, fields);
	NodeList list = document.getElementsByTagName("name");
	String name = list.item(0).getTextContent();
	list = document.getElementsByTagName("pic_small");
	String pic = list.item(0).getTextContent();
	
	if (user == null) {
		user = new User(uid, name, pic, new Point(0,0));
		db.addUser(user);
	}
	else if (!name.equals(user.getName()) || !pic.equals(user.getPic())) {
		user = new User(uid, name, pic, new Point(0,0));
		db.updateUser(user);
	}
}

userHome = user.getHomePoint();

if (userHome.x == 0 && userHome.y == 0) {
	userHome = null;
}

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.w3c.dom.Document"%>
<%@page import="org.w3c.dom.NodeList"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.TreeSet"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Facebook Places</title>

<script src="onload.js"></script>

<%
String path = request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();
System.out.println(path);
String key = "ABQIAAAAjLUGBU2KYPSE-QiX4gc3nhQaIaxgZFO_HyGm4sK6rU1DjshnHBQZEhZXdK-FSkrkhldgO6eTm6dXRw"; //facebook.interface.org.nz/stalkbook/
if (path.equals("localhost:8080/placebook/map.jsp")) {
	key = "ABQIAAAAGVkkPzriDJzr0as5paOJcBTe7Eifd_iXjBDgwTzHgEAyw0l-yRSwY9tPLh5FJ3SvC0a3ctsADcxt1A";
}
else if (path.equals("facebook.interface.org.nz:8080/placebook/map.jsp")) {
	key = "ABQIAAAAGVkkPzriDJzr0as5paOJcBSer5cQCzSoMRgPT_f-szwyoknspxTtWg-aP7mG6y39p9SouMiN1sPy-Q";
}
%>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=<%= key %>"></script>
<script src="stalkbook.js" type="text/javascript"></script>
<script src="ajax.js" type="text/javascript"></script>
<script src="stalkcoordinator.js" type="text/javascript"></script>
<script src="stalklocationcreate.js" type="text/javascript"></script>
<script src="locationmanager.js" type="text/javascript"></script>
<link rel="stylesheet" href="style.css" type="text/css" media="screen" />

<script type="text/javascript">
var user = <%= user.toJSON() %>;

var view = {
<% if (viewLocationX != null && viewLocationY != null) { %>
	location: {x: <%= viewLocationX %>, y: <%= viewLocationY %>}
<% } %>	
};

</script>
<% if (userHome == null) { %>
<script type="text/javascript" src="firstload.js"></script>
<% } else { %>
<script type="text/javascript">
hooks.addHook(
	function () {
		stalkCoordinator = new StalkCoordinator();
		stalkCoordinator.load();
	}
);
</script>
<% } %>
</head>

<body onload="hooks.onload()" onunload="GUnload()">
	<div id="map"></div>
	
<div style="display: none;" id="blocker">
	<div id="addLocationContainer">	
		<h2>Add Location</h2>
		<div id="formWrap">
		<form action="" id="addLocationForm">
			<input id="creator" name="creator" type="hidden"/>
			<input id="pointX" name="pointX" type="hidden"/>
			<input id="pointY" name="pointY" type="hidden""/>
			
			<label for="name">Name</label><input type="text" name="name" id="name"/><br/>
			<label for="description">Description</label><textarea rows="6" cols="40" name="description" id="description"></textarea><br/>
			<label for="tags">Tags</label><input type="text" name="tags" id="tags"/><br/>		
			
			<input class="buttons" type="submit" onclick="StalkLocationCreate.createLocation(); return false" value="Save"/>
			<input class="buttons" type="button" onclick="hideForm()" value="Cancel"/>	
		</form>		
		<br />
		<br />
		</div>
	</div>
</div>

<div id="location"></div>
</body>

</html>