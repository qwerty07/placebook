var hooks = {
	onloadhooks: [],
	onload: function () {
		for (var i = 0; i < this.onloadhooks.length; i++) {
			this.onloadhooks[i]();
		}
	},
	addHook: function (hook) {
		this.onloadhooks.push(hook);
	}
};
