var locationManager;

function LocationManager () {	
	this.load = function () {
		this.location = document.getElementById("location");
		this.title = document.createElement("H4");
		this.description = document.createElement("DIV");
		this.users = document.createElement("UL");
		this.comments = document.createElement("UL");
		this.photos = document.createElement("UL");
		this.joinButton = document.createElement("A");
		
		var close = document.createElement("A");
		close.className="closeButton";
		close.href="javascript:locationManager.hide()";
		close.innerHTML="<span>[close]</span>";
		
		this.joinButton.className="join locationButton";
		this.joinButton.href="javascript:locationManager.join()";
		this.joinButton.innerHTML = "Join this Location";
		
		var content = document.createElement("DIV");
		content.className = "content";
		var inner = document.createElement("DIV");
		inner.className = "inner";
		
		var ddiv = document.createElement("DIV");
		ddiv.className = "item first";
		ddiv.appendChild(document.createElement("H5"));
		ddiv.firstChild.textContent="Description";
		ddiv.appendChild(this.description);
		ddiv.appendChild(this.joinButton);
		
		var udiv = document.createElement("DIV");
		udiv.className = "item users";
		udiv.appendChild(document.createElement("H5"));
		udiv.firstChild.textContent="Locals";
		udiv.appendChild(this.users);
		
		var addComment = document.createElement("A");
		addComment.className = "addComment locationButton";
		addComment.href = "javascript:locationManager.addComment()";
		addComment.innerHTML = "Add a Comment";
		
		var cdiv = document.createElement("DIV");
		cdiv.className = "item comments";
		cdiv.appendChild(document.createElement("H5"));
		cdiv.firstChild.textContent="Comments";
		cdiv.appendChild(this.comments);
		cdiv.appendChild(addComment);
		
		var addPhoto = document.createElement("A");
		addPhoto.className = "addPhoto locationButton";
		addPhoto.href = "javascript:locationManager.addPhoto()";
		addPhoto.innerHTML = "Add a Photo";
		
		var pdiv = document.createElement("DIV");
		pdiv.className = "item photos";
		pdiv.appendChild(document.createElement("H5"));
		pdiv.firstChild.textContent = "Photos";
		pdiv.appendChild(this.photos);
		pdiv.appendChild(addPhoto);
		
		this.location.appendChild(close);
		this.location.appendChild(this.title);
		this.location.appendChild(content);
		content.appendChild(ddiv);
		content.appendChild(udiv);
		content.appendChild(cdiv);
		content.appendChild(pdiv);
		
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
		this.users.innerHTML="";
		this.comments.innerHTML = "";
		this.photos.innerHTML = "";
		
		var descText = response.description;
		var htmlText = "";
		var paragraphs = new Array();
		paragraphs = descText.split("\n");
		for(var i = 0; i < paragraphs.length; i++){
			htmlText = htmlText + "<p>" + paragraphs[i] + "</p>";			
		}
		this.description.innerHTML = htmlText;
		
		this.joinButton.style.display="block";
		
		if (response.users && response.users.length > 0) {
			for(var i = 0; i < response.users.length; i++) {
				this.joinUser(response.users[i]);
			}
			this.users.lastChild.className = "last";	
		}
		
		if (response.comments && response.comments.length > 0) {
			for (var i = 0; i < response.comments.length; i++) {
				this.createComment(response.comments[i]);
			}
		}
		
		if (response.photos && response.photos.length > 0) {
			for (var i = 0; i < response.photos.length; i++) {
				var container = document.createElement("DL");
				var user = document.createElement("DT");
				user.innerHTML = response.photos[i].user.name;
				var date = document.createElement("DD");
				date.innerHTML = response.photos[i].date;
				var description = document.createElement;
				description.innerHTML = response.photos[i].description; 
			}
		}
		
		this.show();
	};
	this.createComment = function (comment) {
		var container = document.createElement("DL");
		var user = document.createElement("DT");
		user.innerHTML=comment.user.name;
		var text = document.createElement("DD");
		text.innerHTML=comment.text;
		var li = document.createElement("LI");
		container.appendChild(user);
		container.appendChild(text);
		li.appendChild(container);
		this.comments.appendChild(li);
	};
	
	this.joinUser = function(u) {

		if (u.user == user.user) {
			this.joinButton.style.display="none";
		}
		
		var text = document.createTextNode(u.name);
		var li = document.createElement("LI");
		li.appendChild(text);
		li.user = u.user;
		this.users.insertBefore(li, this.users.firstChild);

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
		this.joinUser(user);
	}
	this.addComment = function() {
		// add a comment to this location
		var comment = prompt("Enter your comment");
		if (comment != null) {
			async.addComment(user.user, this.coordinates, comment, function(req) {});
			this.createComment({user: user, text: comment});
		}
	}
	
	this.addPhoto = function() {
		// add a photo to this location
		var filename = prompt("Enter photo filename");
		async.addPhoto(user.user, this.coordinates, filename, function(req) {});
	}
};

hooks.addHook(function () { locationManager = new LocationManager(); });
hooks.addHook(function () { locationManager.load(); });
