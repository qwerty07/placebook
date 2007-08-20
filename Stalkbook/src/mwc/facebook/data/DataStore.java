package mwc.facebook.data;

import java.util.Set;



public interface DataStore {
	public User getUserByName(String name);
	public Location getLocationByPoint(Point location);
	public Set<Location> getLocationsWithin(Rectangle area);
	
	public void addUser(User user);
	public void addLocation(Location location);
}
