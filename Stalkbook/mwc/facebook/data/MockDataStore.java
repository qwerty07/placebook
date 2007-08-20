package mwc.facebook.data;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockDataStore implements DataStore{

	private Map<String, User> userMap = new HashMap<String, User>();
	private Map<String, Location> locationMap = new HashMap<String, Location>();
	
	public Location getLocationByName(String name) {
		return locationMap.get(name);
	}

	public Set<Location> getLocationsWithin(Rectangle area) {
		// TODO Auto-generated method stub
		return new HashSet<Location>();
	}

	public User getUserByName(String name) {
		return userMap.get(name); 
	}

	public void addLocation(Location location) {
		locationMap.put(location.getLocationName(), location);
	}

	public void addUser(User user) {
		userMap.put(user.getUserName(), user);
		
	}
	
}
