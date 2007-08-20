var gmapTest = {

testX: -41.28678,
testY: 174.771634,
testString: "Constable St, Newtown, Wellington, New Zealand",

test: function() {
	

},
//testxy
//teststring
//testaddtag

testxy: function() {StalkBook.setPositionXY(gmapTest.testX,gmapTest.testY);},
teststring: function() {StalkBook.setPositionString(gmapTest.testString);},
testaddtag: function() {StalkBook.addMarker(gmapTest.testX,gmapTest.testY);}



}
