var StalkLocationCreate={
	locName: undefined,
	locCreator: undefined,
	locTags: undefined,
	locDesc: undefined,
	
	
	createLocation: function(){
		var form=document.getElementById("addLocationForm");
		locCreator=form.creator.value;
		lat=form.pointLat.value;
		lng=form.pointLng.value;
 
		locName=form.name.value;
		locTags=form.tags.value;
		locDesc=form.description.value;
			
		if(locCreator==""){alert("Creator not defined");return false;}
		if(locName==""){alert("Location name not defined");return false;}
		if(locDesc==""){alert("Location description not defined");return false;}
				
		var point={
			x:lat,
			y:lng
		};
				
		var info = {
			name: locName,
			desc: locDesc
		};
		
		stalkBook.addMarkerByLatLng(point.x, point.y, info);
		async.submitPoint(locCreator, info, point, function(req){stalkCoordinator.asyncCallback(req)});

		form.name.value='';
		form.tags.value='';
		form.description.value='';
		return hideForm();			
	}

};