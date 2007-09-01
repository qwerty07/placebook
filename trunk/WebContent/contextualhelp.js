
var help = {
	bar: undefined,
	message: "",
	defaultLocation: false,
	zoom: false,
	visible: false,
	setDefaultLocation: function (value) {
		this.defaultLocation = value;
		this.refresh();
	},
	setZoom: function (value) {
		this.zoom = value;
		this.refresh();
	},
	load: function () {
		this.bar = document.getElementById("help");
		var map = document.getElementById("map");
		map.addEventListener("mouseover", function () { help.show() }, false);
		map.addEventListener("mouseout", function () { help.hide() }, false);
	},
	refresh: function () {
		if (this.visible) {
			if (!this.defaultLocation) {
				if (this.zoom) {
					this.bar.innerHTML = "Double click to set your default location";
				}
				else {
					this.bar.innerHTML = "Zoom in to set your default location";
				}
			}
			else {
				if (this.zoom) {
					this.bar.innerHTML = "Double click to create a new location";
				}
				else {
					this.bar.innerHTML = "Zoom in to create a location";
				}
			}
		}
		else {
			this.bar.innerHTML = "";
		}
	},
	show: function () {
		this.visible = true;
		this.refresh();
	},
	hide: function () {
		this.visible = false;
		this.refresh();
	},
}

hooks.addHook(function () { help.load() });


	 