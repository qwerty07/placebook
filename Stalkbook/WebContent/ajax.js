/** This page contains objects and functions for async communication with
 *  the application's tomcat server.<b> 
 *  
 *  This version of the page provides functionality for adding points to
 *  the map.
 *
 *  XMLHTTPRequest functions from www.quirksmode.org
 */

var async = {
	/** Location of the application server for requests */
    host: "http://facebook.inferface.org.nz/stalkbook/Async",
    
	/** assumes that the callback function takes one boolean argument
	 *  representing the success or failure of the request. */
	submitPoint: function (user, point, callback) {
		//this function simply encodes the point in a form request.
		//we do not handle security or authentication here - TODO
		var data = "user="+user+"&x=" + point.x + "&y=" + point.y;
		//take point, encode in post form, and send to server.
		this.sendRequest(this.host, callback, data);
	},
	sendRequest: function(url,callback,postData) {
		var req = this.createXMLHTTPObject();
		if (!req) return;
		var method = (postData) ? "POST" : "GET";
		req.open(method,url,true);
		req.setRequestHeader('User-Agent','XMLHTTP/1.0');
		if (postData)
			req.setRequestHeader('Content-type','application/x-www-form-urlencoded');
		req.onreadystatechange = function () {
			if (req.readyState != 4) return;
			if (req.status != 200 && req.status != 304) {
				//alert('HTTP error ' + req.status);
				return;
			}
			callback(req);
		}
		if (req.readyState == 4) return;
		req.send(postData);
	},
	XMLHttpFactories: [
		function () {return new XMLHttpRequest()},
		function () {return new ActiveXObject("Msxml2.XMLHTTP")},
		function () {return new ActiveXObject("Msxml3.XMLHTTP")},
		function () {return new ActiveXObject("Microsoft.XMLHTTP")}
	],
	createXMLHTTPObject: function () {
		var xmlhttp = false;
		for (var i=0;i<this.XMLHttpFactories.length;i++) {
			try {
				xmlhttp = this.XMLHttpFactories[i]();
			}
			catch (e) {
				continue;
			}
			break;
		}
		return xmlhttp;
	}
}