package mwc.facebook.tests.data;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import mwc.facebook.data.DataStore;
import mwc.facebook.data.Location;
import mwc.facebook.data.MockDataStore;
import mwc.facebook.data.User;

public class DataStoreTest extends TestCase {

	private DataStore dataStore;
	protected void setUp() throws Exception {
		dataStore = new MockDataStore();
	}

	public void testUserNotFound(){
		Assert.assertNull(dataStore.getUserByName("######"));
	}

	public void testLocationNotFound(){
		Assert.assertNull(dataStore.getLocationByName("######"));
	}

	public void testLocationsWithinZeroArea(){
		Set<Location> temp = dataStore.getLocationsWithin(new Rectangle(0,0));
		Assert.assertNotNull(temp);
		Assert.assertEquals(0, temp.size());
	}

	public void testAddUser(){
		dataStore.addUser(new User("Bob"));
		User user = dataStore.getUserByName("Bob");
		Assert.assertNotNull(user);
		Assert.assertEquals("Bob", user.getUserName());
	}
	
	public void testAddLocation(){
		dataStore.addLocation(new Location(new Point(10,10),"Home"));
		Location location = dataStore.getLocationByName("Home");
		Assert.assertNotNull(location);
		Assert.assertEquals("Home", location.getLocationName());
	}
	
	public void testLocationsWithinArea(){
		Location point1 = new Location(new Point(2,2),"Point 1");
		dataStore.addLocation(point1);
		Location point2 = new Location(new Point(3,5),"Point 2");
		dataStore.addLocation(point2);
		Location point3 = new Location(new Point(6,3),"Point 3");
		dataStore.addLocation(point3);
		
		Set<Location> temp = dataStore.getLocationsWithin(new Rectangle(0,0,9,7));
		Assert.assertNotNull(temp);
		Assert.assertEquals(3, temp.size());
		Assert.assertTrue(temp.contains(point1));
		Assert.assertTrue(temp.contains(point2));
		Assert.assertTrue(temp.contains(point3));
		
		temp = dataStore.getLocationsWithin(new Rectangle(1,1,3,5));
		Assert.assertNotNull(temp);
		Assert.assertEquals(2, temp.size());
		Assert.assertTrue(temp.contains(point1));
		Assert.assertTrue(temp.contains(point2));
		Assert.assertFalse(temp.contains(point3));
		
		temp = dataStore.getLocationsWithin(new Rectangle(3,2,1,1));
		Assert.assertNotNull(temp);
		Assert.assertEquals(0, temp.size());
		
		temp = dataStore.getLocationsWithin(new Rectangle(4,2,4,3));
		Assert.assertNotNull(temp);
		Assert.assertEquals(1, temp.size());
		Assert.assertFalse(temp.contains(point1));
		Assert.assertFalse(temp.contains(point2));
		Assert.assertTrue(temp.contains(point3));
	}
}
