var gmapTest = {

testX: -41.28678,
testY: 174.771634,
testString: "Constable St, Newtown, Wellington, New Zealand",

test: function() {
	

},
testxy: function() {StalkBook.setPositionXY(gmapTest.testX,gmapTest.testY,5);},
teststring: function() {StalkBook.setPositionString(gmapTest.testString,10);},
testaddtag: function() {StalkBook.addMarker(gmapTest.testX,gmapTest.testY,"<img src=\"http://tbn0.google.com/images?q=tbn:3ID-YoLsX678oM:http://mondomonkey.com/MondoMonkeyWhiteB.jpg\"\>");},
teststringWithoutZoom: function() {StalkBook.setPositionString(gmapTest.testString);}



}
