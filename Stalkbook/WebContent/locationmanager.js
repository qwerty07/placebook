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
		close.innerHTML="X";
		
		var join = document.createElement("A");
		join.className="joinButton locationButton";
		join.href="javascript:locationManager.join()";
		join.innerHTML="join";
		
		this.location.appendChild(close);
		this.location.appendChild(this.title);
		this.location.appendChild(this.description);
		this.location.appendChild(this.users);
		this.location.appendChild(join);
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

		this.coordinates = response.coordinates;
		this.title.innerHTML = response.locationName;
		
		var descText = response.description;
		var htmlText = "";
		var paragraphs = new Array();
		paragraphs = descText.split("\n");
		for(var i = 0; i < paragraphs.length; i++){
			htmlText = htmlText + "<p>" + paragraphs[i] + "</p>";			
		}
		this.description.innerHTML = htmlText;
		
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
		if (document.getElementById("map")) {
			document.getElementById("map").className="thin";
		}
	}
	this.hide = function () {
		this.location.className="";
		if (document.getElementById("map")) {
			document.getElementById("map").className="";
		}
	}
	this.join = function () {
		async.joinLocation(user.user, this.coordinates, function (req) {});
	}
};

hooks.addHook(function () { locationManager = new LocationManager(); });
hooks.addHook(function () { locationManager.load(); });