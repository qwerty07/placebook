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
		
		// get the user's Facebook name & home location
		//this.setUsername(user.username);
		//this.setHomeLocation(user.homeLocation);
		
		// load the map
		stalkBook.load();
		
		// Set the callback function for stalkBook to call when adding
		// a new marker
		stalkBook.setMarkerFunction(stalkCoordinator.addMarker);

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
	
		var form=document.getElementById("addLocationForm");
		form.creator.value=StalkCoordinator.username; 
		form.pointLat.value=point.x;	
		form.pointLng.value=point.y;
		document.getElementById("blocker").style.display = 'block';
		// document.getElementById("addLocationContainer").style.display='block';
		form.name.focus();
		
		// alert("Marker added at " + latlng.lat() + ", " + latlng.lng()); // debugging
	};
};

function hideForm(){
	var form=document.getElementById("blocker");		
	form.style.display = 'none';
	return false;	
}

var stalkCoordinator;
