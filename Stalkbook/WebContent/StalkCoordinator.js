
var StalkCoordinator = {
	username: "MWC",	// the current user's Facebook name
	homeloc: "Kelburn, Wellington, New Zealand",

	/* Called when webpage first loaded -- sets up the map to the default */
	load: function() {
		// load the map
		StalkBook.load();
		
		StalkBook.addMarkerFunction(StalkCoordinator.addMarker);
		
		// get the user's Facebook name
		this.setUsername("Crispin Anderton");	/** DEFAULT **/

		// if we have a user name		
		if (this.username != undefined) {
			// find the user's home location from the database
			
			// tell the map to show the user's home location
			
			
			// get all the user's markers and add them to the map
		} else {
			// don't have a user name, so set defaults
		}
	},
	
	setUsername: function(name) {
		this.username = name;
	},
	
	/* A new marker has been added to the map */
	addMarker: function(latlng) {
		// if the username is not undefined, then add this 
		// new marker to the database under their name
		if (this.username) {
		}
		
		StalkBook.addMarker(latlng, "My point");
	}	
};

