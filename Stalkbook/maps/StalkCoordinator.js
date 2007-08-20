
var StalkCoordinator = {

	load: function() {
		StalkBook.load();
	},
	
	addLocation: function(point) {
		// call this when new location specified on map
		// send point back to map
		gmark = StalkBook.createMarker(point);
		return gmark;
	},
	
	retrieveLocation: function(point) {
	}
};

