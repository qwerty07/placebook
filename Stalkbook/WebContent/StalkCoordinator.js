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
		StalkBook.setPositionString(this.homeloc, this.default_zoom);
		
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
		this.username = name;
	},
	
	setHomeLocation: function(location) {
		this.homeloc = location;
	},
	
	/* A new marker has been added to the map */
	addMarker: function(latlng) {
		// if the username is not undefined, then add this 
		// new marker to the database under their name
		if (this.username) {
		}
		
		StalkBook.addMarker(latlng, "My point");
		// alert("Marker added at " + latlng.lat() + ", " + latlng.lng()); // debugging
	}	
};

