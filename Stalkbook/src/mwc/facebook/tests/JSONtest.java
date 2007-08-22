package mwc.facebook.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import mwc.facebook.data.Location;
import mwc.facebook.data.Point;

public class JSONtest extends TestCase {
	public void testPoints() {
		Assert.assertEquals("{x:0.0, y:0.0}", new Point(0,0).toJSON());
		Assert.assertEquals("{x:-1.0, y:0.0}", new Point(-1,0).toJSON());
		Assert.assertEquals("{x:0.0, y:-1.0}", new Point(0,-1).toJSON());		
	}
	
	public void testLocations() {
		Assert.assertEquals("{coordinates: {x:0.0, y:0.0}, name: \"Location\", description: \"Desc\"}",
				new Location(new Point(0,0), "Location", "Desc").toJSON());
		
		Assert.assertEquals("{coordinates: {x:0.0, y:0.0}}",
				new Location(new Point(0,0), null, null).toJSON());
		
		Assert.assertEquals("{coordinates: {x:0.0, y:0.0}, name: \"NameOnly\"}",
				new Location(new Point(0,0), "NameOnly").toJSON());
		
		Assert.assertEquals("{coordinates: {x:0.0, y:0.0}, description: \"DescOnly\"}",
				new Location(new Point(0,0), null, "DescOnly").toJSON());
	}
}
