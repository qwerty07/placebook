var locationManager;

function LocationManager () {
	this.load = function () {
		this.location = document.getElementById("location");
		this.title = document.createElement("H2");
		this.description = document.createElement("DIV");
		this.users = document.createElement("UL");
		
		this.location.appendChild(this.title);
		this.location.appendChild(this.description);
		this.location.appendChild(this.users);
	};
	this.setLocation = function (point) {
		ajax.retrieveLocation(point, function () { locationManager.callback(); });
	};
	this.callback = function (request) {
		var response = eval('(' + request.responseText + ')');
		this.req = request;
		if (test) {
			test.output(request.responseText);
		}
		this.title.innerHTML = response.location;
		this.description.innerHTML = response.description;
		for (var i = 0; i < response.users.length; i++) {
			var text = document.createTextNode(response.users[i].name);
			var li = document.createElement("LI");
			li.appendChild(text);
			this.users.appendChild(li);
		}
	};
};

hooks.addHook(function () { locationManager = new LocationManager(); });
hooks.addHook(function () { locationManager.load(); });
hooks.addHook(function () { locationManager.callback(
		{ responseText:
			"{ location: 'Location', "+
			"  description: 'description is a long string', "+
			"  users: [ {name: 'Stephen'}, " +
					"{name: 'Andrew'}, " +
					"{name: 'Sam'} ]}"
		}
	);
});