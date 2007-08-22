var hooks = {
	onloadhooks: [],
	onload: function () {
		for (var i = 0; i < onloadhooks.length; i++) {
			onloadhooks[i]();
		}
	},
	addHook: function (hook) {
		onloadhooks[] = hook;
	}
};
