//<![CDATA[

/**stalkBook is an object for interfacing with the google maps API
It allows the position to be set by lat,lng and by String (i.e wellington, nz).
We can set a marker to a given lat,lng.

To use this:
there must be an element with id=map (i.e <div id="map"></div>)
This will be the location of the map.
*/
var stalkBook = {
  minZoom: 15,
  locality: "Wellington, New Zealand",
  geocoder: new GClientGeocoder(),
  map: undefined,
  markerFunc: undefined,
  typeImages: new Array(5),
  markers: [],
  
  /*
  Loads up the map requires map element. Define interfaces here.
  */
  load: function() {
    if (GBrowserIsCompatible()) {
      stalkBook.map=new GMap2(document.getElementById("map"));
      stalkBook.map.addControl(new GLargeMapControl());
	  stalkBook.map.addControl(new GMapTypeControl());
	  
	  stalkBook.typeImages[0]= new GIcon();
	  stalkBook.typeImages[0].image = "./images/map/home.png";
      stalkBook.typeImages[0].shadow = "http://labs.google.com/ridefinder/images/mm_20_shadow.png";
	  stalkBook.typeImages[0].iconSize = new GSize(25, 25);
	  stalkBook.typeImages[0].shadowSize = new GSize(22, 20);
  	  stalkBook.typeImages[0].iconAnchor = new GPoint(6, 20);
	  stalkBook.typeImages[0].infoWindowAnchor = new GPoint(5, 1);
	  stalkBook.typeImages[0].type="Home";
	 
	  stalkBook.typeImages[1]= new GIcon();
	  stalkBook.typeImages[1].image = "./images/map/eat.png";
      stalkBook.typeImages[1].shadow = "http://labs.google.com/ridefinder/images/mm_20_shadow.png";
	  stalkBook.typeImages[1].iconSize = new GSize(25, 25);
	  stalkBook.typeImages[1].shadowSize = new GSize(22, 20);
  	  stalkBook.typeImages[1].iconAnchor = new GPoint(6, 20);
	  stalkBook.typeImages[1].infoWindowAnchor = new GPoint(5, 1);
	  stalkBook.typeImages[1].type="Eat";	  
      
      if (stalkCoordinator && stalkCoordinator.homeloc) {
      	stalkBook.setPositionXY(stalkCoordinator.homeloc.x, stalkCoordinator.homeloc.y, stalkCoordinator.default_zoom);
      }
      else {
      	stalkBook.geocoder.getLatLng(stalkBook.locality,function(point){
			stalkBook.map.setCenter(point, 12);
      	});
      }

      stalkBook.map.enableScrollWheelZoom();

      GEvent.addListener(stalkBook.map, "dblclick", function(marker, point) {
        if (stalkBook.map.getZoom() > stalkBook.minZoom){
            if(stalkBook.markerFunc)stalkBook.markerFunc(point);            
        }
      })
      GEvent.addListener(stalkBook.map, "moveend", function() {       
        if(stalkBook.moveFunc)stalkBook.moveFunc();                    
      })
    }
  },


/*
Add marker at a given point defined by latitude(lat) and longitude(lng) also takes an 
optional html formated string(str) that will be displayed when the marker is clicked, 
as well as an optional type which changes the marker image to what is associated to the 
given type.
*/
  
  addMarkerByXY: function(x, y, info, type){
    var point= new GLatLng(y, x);
	stalkBook.addMarker(point,info,type);
  },
  
/*
Add marker at a given point defined by a GLatLng(point) also takes an 
optional html formated string(str) that will be displayed when the 
marker is clicked, as well as an optional type which changes the 
marker image to what is associated to the given type.
*/
  addMarker: function(latlng, info,type){
    var marker = new GMarker(latlng);
    if(type){
	  	for (i=0;i<stalkBook.typeImages.length;i++){	
	  		if(stalkBook.typeImages[i].type==type){
  				marker = new GMarker(latlng, stalkBook.typeImages[i]);
  				break;
  			}
  		}
  	}
    if(info.name){
		GEvent.addListener(marker, "click", function(){
			marker.openInfoWindowHtml(
				"<dl>" +
				"<dt>Name:</dt><dd>" + info.name + "</dd>" +
				"<dt>Description</dt><dd>" + info.desc + "</dd>" +
				"</dl>" +
				"<a href='javascript:void(0)' onclick='locationManager.setLocation({x:"+latlng.lng()+", y:"+latlng.lat()+"})'>[view]</a>"
			);
		});
	}
	this.markers[this.markers.length] = marker;
  	stalkBook.map.addOverlay(marker);
  },
  
  clearMarkers: function() {
  	for(var i=0; i<markers.length; i++)
  		stalkBook.map.removeOverlay(markers[i]);
  	this.markers = [];
  },
    
/*
Centers position to a given point, defined by latitude(lat) and longitude(lng). 
This has an optional zoom parameter. This can be between 1-17 possibly more 
for areas with more detail.
*/  
  setPositionXY: function(x, y, zoom){
    if(!zoom){zoom=stalkBook.map.getZoom();}
    var point=new GLatLng(y, x, true);
  	stalkBook.map.setCenter(point, zoom);
  },
  
 /*
 Centers position to a place defined by a string. This has an optional zoom 
 parameter. This can be between 1-17 possibly more for areas with more detail.
 */
  setPositionString: function(location, zoom, type){
    if(!zoom){zoom=stalkBook.map.getZoom();}
  	stalkBook.geocoder.getLatLng(location,function(point){
		stalkBook.map.setCenter(point,zoom);
    });
  },
  
  
 /*
 Converts a GLatLng to a point object.
 */ 
  gPointtoPoint: function(latlngpoint) {
  	var point = {
  	  y: latlngpoint.lat(),
  	  x: latlngpoint.lng()
    };
    return point;
  },
  
  
  /*
  Function that takes a callback function to be executed when a the map is 
  double clicked. i.e. When a marker needs added.
  */
  setMarkerFunction: function(func){
  	stalkBook.markerFunc=func;
  },
  /*
  Function that takes a callback function to be executed when a the map is clicked. 
  i.e. When the view is moved.
  */
  moveFunction: function(func){
  	stalkBook.moveFunc=func;
  },
  
  
  /*
  Gets the current view window of the map
  */
  getViewWindow: function(){
  	return stalkBook.map.getBounds();
  }
  
};
//]]>