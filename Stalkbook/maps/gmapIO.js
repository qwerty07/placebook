//<![CDATA[

/**StalkBook is an object for interfacing with the google maps API
It allows the position to be set by lat,lng and by String (i.e wellington, nz).
We can set a marker to a given lat,lng.

To use this:
there must be an element with id=map (i.e <div id="map"></div>)
This will be the location of the map.

Things to do:
Be able to change locality (probably from facebook application or db)
Interface with facebook app.
Work with wrapper class.
->Be able to set the string on a marker, with a URL (not completed)

*/
var StalkBook = {
  minZoom: 15,
  locality: "christchurch, new zealand",
  geocoder: new GClientGeocoder(),
  map: undefined,
  //currentLocation: new GPoint(),
  
  load: function() {
    if (GBrowserIsCompatible()) {
      StalkBook.map=new GMap2(document.getElementById("map"));
      //map.setCenter(new GLatLng(-40.979898,170.671875), 16);
      StalkBook.geocoder.getLatLng(StalkBook.locality,function(point){
		StalkBook.map.setCenter(point, 12);
      });

      StalkBook.map.enableScrollWheelZoom();

      GEvent.addListener(StalkBook.map, "click", function(marker, point) {
        if (StalkBook.map.getZoom() > StalkBook.minZoom){
          if (marker) {
            StalkBook.map.removeOverlay(marker);
          }
          else {
            StalkBook.map.addOverlay(StalkBook.createMarker(point));
          }
        }
      })
    }
  },
  displayInfo: function(marker,str) {
	marker.openInfoWindowHtml(str);
  },

  createMarker: function(point,str) {
	var marker = new GMarker(point);
	GEvent.addListener(marker, "click", function(){
		StalkBook.displayInfo(marker,str);
	});
	return marker;
  },
  
  addMarker: function(x, y, str){
  	StalkBook.map.addOverlay(StalkBook.createMarker(new GLatLng(x,y),str));
  },
  
  setPositionXY: function(x, y){
  	StalkBook.map.setCenter(new GLatLng(x,y,true));
  	//StalkBook.map.setCenter(new GLatLng(37.4419, -122.1419), 13);
  },
  
  setPositionString: function(location){
  	StalkBook.geocoder.getLatLng(location,function(point){
		StalkBook.map.setCenter(point);
    });
  },
  
  gpointtopoint: function(latlngpoint) {
  	var point = {
  	  lat: latlngpoint.lat(),
  	  lng: latlngpoint.lng()
    };
    return point;
  }
};
//]]>