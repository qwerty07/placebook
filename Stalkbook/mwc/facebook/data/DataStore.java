package mwc.facebook.data;

import java.awt.Rectangle;


public interface DataStore {
	public User getUserByName(String name);
	public Location getLocationByName(String name);
	public Location getLocationsWithin(Rectangle area);
}
