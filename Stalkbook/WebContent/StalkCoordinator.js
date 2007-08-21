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
		StalkBook.load();
		
		// Set the callback function for StalkBook to call when adding
		// a new marker
		StalkBook.addMarkerFunction(StalkCoordinator.addMarker);
		
		// get the user's Facebook name & home location
		this.setUsername(User.username);
		this.setHomeLocation(User.homeLocation);

		// tell the map to centre on the user's home location
		StalkBook.setPositionXY(this.homeloc.x, this.homeloc.y, this.default_zoom);

		for (var i = 0; i < User.locations.length; i++) {
			var location = User.locations[i];
			StalkBook.addMarkerByLatLng(location.x, location.y, location.name);
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
		// alert(req.readystate + ", " + req.status);
	},
	
	/* A new marker has been added to the map */
	addMarker: function(latlng) {
		// add this new marker to the database under the user name
		var point = {
			x: latlng.lat(),
			y: latlng.lng()
		};
				
		var markerText = prompt("Name of location");
		if(markerText){		
			if (markerText == "") { 
				markerText = "My marker"; 
			}
				
			StalkBook.addMarker(latlng, markerText);
			
			async.submitPoint(StalkCoordinator.username, markerText, point, function(req){StalkCoordinator.asyncCallback(req)});
			// alert("Marker added at " + latlng.lat() + ", " + latlng.lng()); // debugging
		}
	}
};

