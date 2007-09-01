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
		text = this.out(text);
		if (out) out.appendChild(document.createTextNode(text + "\n"));
	},
	out: function (text) {
		var out = document.getElementById("output");
		if (text.isArray) {
			var t = "[ ";
			for (var i = 0; i < text.length; i++) {
				t += this.out(text[i]) + ", ";
			}
			t+="]";
			return t;
		}
		else if (text.coordinates) {
			return "{x: " + text.coordinates.x + ",y: " + text.coordinates.y + "}";
		}
		else {
			return text;
		}
	},
	fail: function() {
		setResult(false);
	}
};

Array.prototype.isArray = function(){return true;}

async.sendRequest= function(url,callback,postData) {
	test.output("post: " + postData);
	callback(postData);
}