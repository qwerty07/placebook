/** Object for testing javascript functions
 *  Assumes a div called "output" for writing to
 */

var test = {
	tests: [],
	add: function(t) {
		this.tests.push(t);
	},
	run: function () {
		var result = false;
		for (var i = 0; i < this.tests.length; i++) {
			result = this.tests[i]();
			if (!result) break;
		}
		this.setResult(result);
	},
	setResult: function(result) {
		var out = document.body;
		if (result) {
			out.style.backgroundColor = "#afa;";
		}
		else {
			out.style.backgroundColor = "#faa;";
		}
	},
	output: function (text) {
		var out = document.getElementById("output");
		if (out) out.appendChild(document.createTextNode(text));
	},
	fail: function() {
		setResult(false);
	}
};