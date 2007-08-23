/**
 *  StalkCoordinator
 *
 * Handles the communication between the Tomcat webserver
 * and the Google Maps interface object (reg gmapIO.js)
*/

function StalkCoordinator() {
	this.username = undefined;	// the current user's Facebook name
	this.homeloc = undefined;	// user's home location -- where the map will centre when loaded
	this.default_zoom = 15;	// default zoom level
	
	this.viewOfConcern = undefined;

	/* Called when webpage first loaded -- sets up the map to the default */
	this.load = function() {
		
		// get the user's Facebook name & home location
		this.setUsername(user.username);
		this.setHomeLocation(view.location || user.homeLocation);
		
		// load the map
		stalkBook.load();
		
		// Set the callback function for stalkBook to call when adding
		// a new marker
		stalkBook.setMarkerFunction(stalkCoordinator.addMarker);
		//stalkBook.moveFunction(stalkCoordinator.moveView);

		for (var i = 0; i < user.locations.length; i++) {
			var location = user.locations[i];
			stalkBook.addMarkerByXY(location.coordinates.x, location.coordinates.y, location);
		}
	};
	
	this.processMarkers = function(req) {
		var markers = eval(req.responseText);
		if(markers)stalkCoordinator.addMarkers(markers);
		else{
			alert(req.responseText);
		}
			
	};
	
	this.addMarkers = function(markers) {
		stalkBook.clearMarkers();
		for (var i = 0; i < markers.length; i++) {
			var	location = markers[i];
			stalkBook.addMarkerByXY(location.coordinates.x, location.coordinates.y, location);
		}
	};
	
	this.setUsername = function(name) {
		stalkCoordinator.username = name;
	};
	
	this.setHomeLocation = function(location) {
		stalkCoordinator.homeloc = location;
	};
	
	this.asyncCallback = function(req) {
		// alert(req.readystate + ", " + req.status);
	};
	
	/*
	Creates the view of concern for a given window(GLatLngBounds)
	*/
/*	this.getViewOfConcern = function(window){
	  var size=window.toSpan();
	  var windowTopLeft=new GLatLng(window.getNorthEast().lat-size.lat,window.getNorthWest().lng-size.lng);
	  var windowBottomRight=new GLatLng(window.getNorthEast().lat+size.lat,window.getNorthEast().lng+size.lng);
	  var derived=new GLatLngBounds(windowTopLeft-,windowBottomRight);
 
	};*/
	
	/* A new marker has been added to the map */
	this.addMarker = function(latlng) {
		// add this new marker to the database under the user name
		var point = {
			x: latlng.lng(),
			y: latlng.lat()
		};
	
		var form=document.getElementById("addLocationForm");
		form.creator.value=stalkCoordinator.username; 
		form.pointX.value=point.x;	
		form.pointY.value=point.y;
		form.name.value = '';
		form.description.value = '';
		form.tags.value = '';
		document.getElementById("blocker").style.display = 'block';

		form.name.focus();
		
		// alert("Marker added at " + latlng.lat() + ", " + latlng.lng()); // debugging
	};
	
	this.moveView = function(){
		var window=stalkBook.getViewWindow();
		var pt1=window.getNorthEast();
		var pt2=window.getSouthWest();
		async.retrieveLocationsByRec(pt1.lat(),pt1.lng(),pt2.lat(),pt2.lng(),stalkCoordinator.processMarkers);
	}
};

function hideForm(){
	var form=document.getElementById("blocker");		
	form.style.display = 'none';
	return false;	
}

var stalkCoordinator;
