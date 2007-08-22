/**
 *  StalkCoordinator
 *
 * Handles the communication between the Tomcat webserver
 * and the Google Maps interface object (reg gmapIO.js)
*/

var StalkCoordinator = {
	username: undefined,	// the current user's Facebook name
	homeloc: undefined,	// user's home location -- where the map will centre when loaded
	default_zoom: 15,	// default zoom level

	/* Called when webpage first loaded -- sets up the map to the default */
	load: function() {
		// load the map
		stalkBook.load();
		
		// Set the callback function for StalkBook to call when adding
		// a new marker
		stalkBook.setMarkerFunction(StalkCoordinator.addMarker);
		
		// get the user's Facebook name & home location
		this.setUsername(User.username);
		this.setHomeLocation(User.homeLocation);

		// tell the map to centre on the user's home location
		stalkBook.setPositionXY(this.homeloc.x, this.homeloc.y, this.default_zoom);

		for (var i = 0; i < User.locations.length; i++) {
			var location = User.locations[i];
			stalkBook.addMarkerByLatLng(location.x, location.y, location.name);
		}
	
		// if we have a user name		
		if (this.username != "MWC") {
			// alert("Welcome, " + this.username);  // debugging
			
			// get all the user's markers and add them to the map
		} else {
			// don't have a user name, so use defaults
			// alert("Using default location settings");  // debugging
			
			// create a new user on the database
		}
	},
	
	setUsername: function(name) {
		StalkCoordinator.username = name;
	},
	
	setHomeLocation: function(location) {
		StalkCoordinator.homeloc = location;
	},
	
	asyncCallback: function(req) {
		alert("Success");
	
		//alert(req.readystate + ", " + req.status);
	},
	
	/* A new marker has been added to the map */
	addMarker: function(latlng) {
		// add this new marker to the database under the user name
		var point = {
			x: latlng.lat(),
			y: latlng.lng()
		};
	
	/*
		var markerName = prompt("Enter marker text");
		if (markerName == undefined) return;
		if (markerName == "") { 
			markerName = "My marker"; 
		}
		
		var markerDesc = prompt("Enter description");
		if (markerDesc == ""){
			markerDesc = "Desc";
		}*/
		
		
		//alert("Opening Add Location form");
		var form=document.getElementById("addLocationForm");
		form.creator.value=StalkCoordinator.username; 
		form.pointLat.value=point.x;	
		form.pointLng.value=point.y;
		document.getElementById("addLocationContainer").style.display='block';
		
		//async.submitPoint(StalkCoordinator.username, markerName, point, function(req){StalkCoordinator.asyncCallback(req)});
		// alert("Marker added at " + latlng.lat() + ", " + latlng.lng()); // debugging
	}
};

function hideForm(){
	var form=document.getElementById("addLocationContainer");		
	form.style.display = 'none';
	return false;	
}

