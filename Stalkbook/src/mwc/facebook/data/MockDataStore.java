package mwc.facebook.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockDataStore implements DataStore{

	private Map<String, User> userMap = new HashMap<String, User>();
	private Map<Point, Location> locationMap = new HashMap<Point, Location>();
	private Set<Pair<User, Location>> userLocations = new HashSet<Pair<User,Location>>(); 
	
	public Location getLocationByPoint(Point point) {
		return locationMap.get(point);
	}

	public Set<Location> getLocationsWithin(Rectangle area) {
		Set<Location> locations = new HashSet<Location>();
		for (Location l : locationMap.values()) {
			if (area.contains(l.getCoordinates())) locations.add(l);
		}
		return locations;
	}

	public User getUserByName(String name) {
		return userMap.get(name); 
	}

	public void addLocation(Location location) {
		locationMap.put(location.getCoordinates(), location);
	}

	public void addUser(User user) {
		userMap.put(user.getUserName(), user);
	}

	public void addUserToLocation(User user, Location location) {
		userLocations.add(new Pair<User, Location>(user, location));
	}

	public Set<Location> locationsFor(User user) {
		Set<Location> result = new HashSet<Location>();
		
		for(Pair<User, Location> pear : userLocations) {
			if(pear.a.equals(user))
				result.add(pear.b);
		}
		return result;
	}

	public Set<User> usersAssociatedWith(Location location) {
		Set<User> result = new HashSet<User>();
		
		for(Pair<User, Location> pear : userLocations) {
			if(pear.b.equals(location))
				result.add(pear.a);
		}
		return result;
	}

	public Set<Location> getLocationsWithin(Point centre, double radius) {
		Set<Location> result = new HashSet<Location>();
		
		for(Location l : locationMap.values()) {
			if (l.getCoordinates().distanceTo(centre) <= radius) {
				result.add(l);
			}
		}
		return result;
	}
}
