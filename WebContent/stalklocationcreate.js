var StalkLocationCreate={
	locName: undefined,
	locCreator: undefined,
	locTags: undefined,
	locDesc: undefined,
	
	
	createLocation: function(){
		var form=document.getElementById("addLocationForm");
		locCreator=form.creator.value;
		x=form.pointX.value;
		y=form.pointY.value;
 
		locName=form.name.value;
		locTags=form.tags.value;
		locDesc=form.description.value;
			
		if(locCreator==""){alert("Creator not defined");return false;}
		if(locName==""){alert("Location name not defined");return false;}
		if(locDesc==""){alert("Location description not defined");return false;}
				
		var point={
			x:x,
			y:y
		};
				
		var info = {
			name: locName,
			desc: locDesc
		};
		
		stalkBook.addMarkerByXY(point.x, point.y, info);
		async.submitPoint(locCreator, info, point, function(req){stalkCoordinator.asyncCallback(req)});

		form.name.value='';
		form.tags.value='';
		form.description.value='';
		return hideForm();			
	}

};