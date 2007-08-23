var locationManager;

function LocationManager () {
	this.load = function () {
		this.location = document.getElementById("location");
		this.title = document.createElement("H2");
		this.description = document.createElement("DIV");
		this.users = document.createElement("UL");
		
		var close = document.createElement("A");
		close.className="closeButton";
		close.href="javascript:locationManager.hide()";
		close.innerHTML="close";
		
		this.location.appendChild(close);
		this.location.appendChild(this.title);
		this.location.appendChild(this.description);
		this.location.appendChild(this.users);
	};
	this.setLocation = function (point) {
		async.retrieveLocation(point, function (req) { locationManager.callback(req); });
	};
	this.callback = function (request) {
		var response = eval('(' + request.responseText + ')');
		this.req = request;
		if (window.test) {
			test.output(request.responseText);
		}
		this.title.innerHTML = response.name;
		this.description.innerHTML = response.desc;
		if (response.users) {
			for (var i = 0; i < response.users.length; i++) {
				var text = document.createTextNode(response.users[i].name);
				var li = document.createElement("LI");
				li.appendChild(text);
				this.users.appendChild(li);
			}
		}
		this.show();
	};
	this.show = function () {
		this.location.className="show";
		document.getElementById("map").className="thin";
	}
	this.hide = function () {
		this.location.className="";
		document.getElementById("map").className="";
	}
};

hooks.addHook(function () { locationManager = new LocationManager(); });
hooks.addHook(function () { locationManager.load(); });