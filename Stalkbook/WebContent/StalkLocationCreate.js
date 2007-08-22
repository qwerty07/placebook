var StalkLocationCreate={
	locName: undefined,
	locCreator: undefined,
	locTags: undefined,
	locDesc: undefined,
	
	
	openForm: function(){}
	submitForm: function(){}
	cancelForm: function(){}
	
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
				

		stalkBook.addMarkerByLatLng(point.x, point.y, {name: locName, desc: locDesc});
		//async.submitPoint(locCreator, locName, point, function(req){StalkCoordinator.asyncCallback(req)});

		form.name.value='';
		form.tags.value='';
		form.description.value='';
		
		return hideForm();			
	}

};