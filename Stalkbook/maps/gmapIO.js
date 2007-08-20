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
  
  /*
  Loads up the map requires map element. Define interfaces here.
  */
  load: function() {
    if (GBrowserIsCompatible()) {
      StalkBook.map=new GMap2(document.getElementById("map"));
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


/*
Add marker at a given point defined by latitude(lat) and longitude(lng) also takes an html 
formated string(str) that will be displayed when the marker is clicked.
*/
  addMarker: function(lat, lng, str){
    var point= new GLatLng(lat, lng);
    var marker = new GMarker(point);
	GEvent.addListener(marker, "click", function(){
		marker.openInfoWindowHtml(str);
	});
  	StalkBook.map.addOverlay(marker);
  },

/*
Centers position to a given point, defined by latitude(lat) and longitude(lng). 
This has an optional zoom parameter. This can be between 1-17 possibly more 
for areas with more detail.
*/  
  setPositionXY: function(lat, lng, zoom){
  if(!zoom)zoom=StalkBook.map.getZoom();
  	StalkBook.map.setCenter(new GLatLng(lat,lng,true), zoom);
  },
  
 /*
 Centers position to a place defined by a string. This has an optional zoom 
 parameter. This can be between 1-17 possibly more for areas with more detail.
 */
  setPositionString: function(location, zoom){
    if(!zoom)zoom=StalkBook.map.getZoom();
  	StalkBook.geocoder.getLatLng(location,function(point){
		StalkBook.map.setCenter(point,zoom);
    });
  },
  
  
 /*
 Converts a GLatLng to a point object.
 */ 
  gpointtopoint: function(latlngpoint) {
  	var point = {
  	  lat: latlngpoint.lat(),
  	  lng: latlngpoint.lng()
    };
    return point;
  }
  
  
};
//]]>