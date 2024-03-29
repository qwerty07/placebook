var firstload = function () {

	stalkCoordinator = new StalkCoordinator();
	
	stalkCoordinator.addMarker = function (latlng) {
		var point = {
			x: latlng.lng(),
			y: latlng.lat()
		};
			
		user.home = point;
	
		async.setDefaultLocation(user.user, point, function (req) { stalkCoordinator.complete(req) });
	};
	
	stalkCoordinator.load = function () {
		alert("Welcome to Stalkbook, please select your default location.");
			
		// load the map
		stalkBook.load();
		
		// Set the callback function for stalkBook to call when adding
		// a new marker
		stalkBook.setMarkerFunction(stalkCoordinator.addMarker);
		
		// get the user's Facebook name & home location
		this.setUsername(user.user);
		
		if (help) {
			help.setDefaultLocation(false);
		}
	};
	
	stalkCoordinator.complete = function (req) {
		stalkCoordinator = new StalkCoordinator();
		stalkCoordinator.load();
	};
	
	stalkCoordinator.load();
}

hooks.addHook(firstload);