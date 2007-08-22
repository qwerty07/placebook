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

	/* Called when webpage first loaded -- sets up the map to the default */
	this.load = function() {
		// load the map
		stalkBook.load();
		
		// Set the callback function for stalkBook to call when adding
		// a new marker
		stalkBook.setMarkerFunction(stalkCoordinator.addMarker);
		
		// get the user's Facebook name & home location
		this.setUsername(user.username);
		this.setHomeLocation(user.homeLocation);

		// tell the map to centre on the user's home location
		alert('x: ' + this.homeloc.x + ', y:' + this.homeloc.y);
		stalkBook.setPositionXY(this.homeloc.x, this.homeloc.y, this.default_zoom);
		
		for (var i = 0; i < user.locations.length; i++) {
			var location = user.locations[i];
			stalkBook.addMarkerByLatLng(location.coordinates.x, location.coordinates.y, location);
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
	
	/* A new marker has been added to the map */
	this.addMarker = function(latlng) {
		// add this new marker to the database under the user name
		var point = {
			x: latlng.lat(),
			y: latlng.lng()
		};
	
		var markerName = prompt("Enter marker text");
		if (markerName == undefined) return;
		if (markerName == "") { 
			markerName = "My marker"; 
		}
		
		var markerDesc = prompt("Enter description");
		if (markerDesc == ""){
			markerDesc = "Desc";
		}

		var loc = {name: markerName, desc: markerDesc};
		
		stalkBook.addMarker(latlng, loc);
		
		async.submitPoint(stalkCoordinator.username, loc, point, function(req){stalkCoordinator.asyncCallback(req)});
		// alert("Marker added at " + latlng.lat() + ", " + latlng.lng()); // debugging
	};
};

var stalkCoordinator;
