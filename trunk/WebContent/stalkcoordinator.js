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
	this.markers = []; //markers on the map
	
	this.viewOfConcern = undefined;

	/* Called when webpage first loaded -- sets up the map to the default */
	this.load = function() {
		
		// get the user's Facebook name & home location
		this.setUsername(user.user);
		this.setHomeLocation(view.location || user.home);
		
		if (help) {
			help.setDefaultLocation(true);
		}
		
		// load the map
		stalkBook.load();
		
		// Set the callback function for stalkBook to call when adding
		// a new marker
		stalkBook.setMarkerFunction(stalkCoordinator.addMarker);
		stalkBook.moveFunction(stalkCoordinator.moveView);
		
		this.moveView();
	};
	
	this.processMarkers = function(req) {
		var markers = eval(req.responseText);
		if(markers)stalkCoordinator.addMarkers(markers);
		else{
			alert(req.responseText);
		}
			
	};
	
	this.addMarkers = function(markers) {
		//stalkBook.clearMarkers(); --don't do this anymore
		
		//sort markers
		markers = this.sort(markers);
		var result = [];
		
		var i = 0;
		var j = 0;
		while (i < markers.length && j < this.markers.length) {
			if (this.compare(markers[i], this.markers[j]) < 0) {
				result.push(markers[i]);
				var	location = markers[i];
				stalkBook.addMarkerByXY(location.coordinates.x, location.coordinates.y, location);
				i++;
			}
			else if (this.compare(markers[i], this.markers[j]) > 0) {
				result.push(this.markers[j]);
				j++;
			}
			else {
				result.push(this.markers[j]);
				i++;
				j++;
			}
		}
		while (i < markers.length) {
			result.push(markers[i]);
			var	location = markers[i];
			stalkBook.addMarkerByXY(location.coordinates.x, location.coordinates.y, location);
			i++;
		}
		while (j < this.markers.length) {
			result.push(this.markers[j]);
			j++;
		}
		
		this.markers = result;
	};
	
	this.sort = function(list) {
		if (list.length <= 1) {
			return list;
		}
		else {
			var l1 = list.splice(0,list.length/2);
			var l2 = list;
			var result = [];
			l1 = this.sort(l1);
			l2 = this.sort(l2);
			var i = 0;
			var j = 0;
			while (i < l1.length && j < l2.length) {
				if (this.compare(l1[i], l2[j]) < 0) {
					result.push(l1[i]);
					i++;
				}
				else if (this.compare(l1[i], l2[j]) > 0) {
					result.push(l2[j]);
					j++;
				}
				else {
					result.push(l1[i]);
					result.push(l2[j]);
					i++;
					j++;
				}
			}
			while (i < l1.length) {
				result.push(l1[i]);
				i++;
			}
			while (j < l2.length) {
				result.push(l2[j]);
				j++;
			}
			return result;
		}
	};
	
	this.compare = function (a, b) {
		if (a.coordinates.x == b.coordinates.x) {
			return a.coordinates.y - b.coordinates.y;
		}
		else return a.coordinates.x - b.coordinates.x;
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
			x: latlng.lng(),
			y: latlng.lat()
		};
	
		// Hide the location pane, if its visible
		locationManager.hide();
	
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
		if (stalkBook.getWindowZoom < 10){
			async.getUserLocationsByRec(pt1.lng(),pt1.lat(),pt2.lng(),pt2.lat(),stalkCoordinator.processMarkers);	
		} else {
			async.retrieveLocationsByRec(pt1.lng(),pt1.lat(),pt2.lng(),pt2.lat(),stalkCoordinator.processMarkers);
		}
	}
};

function hideForm(){
	var form=document.getElementById("blocker");		
	form.style.display = 'none';
	return false;	
}

var stalkCoordinator;
