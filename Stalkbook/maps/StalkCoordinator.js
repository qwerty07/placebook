
var StalkCoordinator = {

	load: function() {
		var pt = GPoint(200, 200);

		StalkBook.load();
		StalkBook.createMarker(pt);
	},
	
	addLocation: function(point) {
		// call this when new location specified on map
		// send point back to map
		StalkBook.createMarker(point);
	},
	
	retrieveLocation: function(point) {
	}
};

