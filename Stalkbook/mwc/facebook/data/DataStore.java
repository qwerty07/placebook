package mwc.facebook.data;

import java.awt.Rectangle;
import java.util.Set;


public interface DataStore {
	public User getUserByName(String name);
	public Location getLocationByName(String name);
	public Set<Location> getLocationsWithin(Rectangle area);
}
