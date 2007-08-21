var gmapTest = {

testX: -41.28678,
testY: 174.771634,
testString: "Constable St, Newtown, Wellington, New Zealand",

test: function() {
	StalkBook.addMarkerFunction(function(point){
	alert(point);
	});
},
testxy: function() {StalkBook.setPositionXY(gmapTest.testX,gmapTest.testY,5);},
teststring: function() {StalkBook.setPositionString(gmapTest.testString,16);},
testaddtag: function() {StalkBook.addMarkerByLatLng(gmapTest.testX-1,gmapTest.testY-1,"<img src=\"http://tbn0.google.com/images?q=tbn:3ID-YoLsX678oM:http://mondomonkey.com/MondoMonkeyWhiteB.jpg\"\>","Home");},
testaddtag1: function() {StalkBook.addMarkerByLatLng(gmapTest.testX,gmapTest.testY,"<img src=\"http://images.google.co.nz/imgres?imgurl=http://upload.wikimedia.org/wikipedia/commons/2/27/Baby_ginger_monkey.jpg&imgrefurl=http://commons.wikimedia.org/wiki/Image:Baby_ginger_monkey.jpg&h=352&w=408&sz=55&hl=en&start=6&sig2=sfc-HxJld4UsM-TA3w2YHA&tbnid=Fun59PzDyIjYVM:&tbnh=108&tbnw=125&ei=ACXKRub8A4TAgQPz9-itAQ&prev=/images%3Fq%3Dmonkey%26gbv%3D2%26svnum%3D10%26hl%3Den%26sa%3DG\"\>");},
testaddtag2: function() {StalkBook.addMarkerByLatLng(gmapTest.testX+1,gmapTest.testY+1,"<img src=\"http://upload.wikimedia.org/wikipedia/commons/2/27/Baby_ginger_monkey.jpg\"\>","Eat");},
teststringWithoutZoom: function() {StalkBook.setPositionString(gmapTest.testString);}


}
