//<![CDATA[


var StalkBook = {
  minZoom: 15,
  locality: "christchurch, new zealand",
  geocoder: new GClientGeocoder(),
  //currentLocation: new GPoint(),

  load: function() {
    if (GBrowserIsCompatible()) {
      var map=new GMap2(document.getElementById("map"));
      //map.setCenter(new GLatLng(-40.979898,170.671875), 16);
      StalkBook.geocoder.getLatLng(StalkBook.locality,function(point){
		map.setCenter(point, 12);
      });

      map.enableScrollWheelZoom();

      GEvent.addListener(map, "dblclick", function(marker, point) {
        if (map.getZoom() > StalkBook.minZoom){
          if (marker) {
            map.removeOverlay(marker);
          }
          else {
            map.addOverlay(StalkBook.createMarker(point));
          }
        }
      })
    }
  },
  displayInfo: function (marker) {
	marker.openInfoWindowHtml("Marker #<b> HIHIHI" + "</b>");
  },
  getInfo: function(x, y){
  },
  createMarker: function(point) {
	var marker = new GMarker(point);
	GEvent.addListener(marker, "click", function(){
		StalkBook.displayInfo(marker);
	});
	return marker;
  }
};
//todo:
//search position
//method that takes an object
	//pass the point
//loadmarker
    
    //]]>