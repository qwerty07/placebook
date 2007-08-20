package mwc.facebook.tests.data;

import java.awt.Rectangle;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import mwc.facebook.data.DataStore;
import mwc.facebook.data.Location;
import mwc.facebook.data.MockDataStore;

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
}
