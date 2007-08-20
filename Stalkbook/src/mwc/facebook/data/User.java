package mwc.facebook.data;

import java.util.HashSet;
import java.util.Set;

public class User {
	private String userName;
	private Point home;
	private Set<Location> locations;
	
	public User(String username, Point home){
		this.userName = username;
		this.home = home;
		
		this.locations = new HashSet<Location>();
	}
	
	public String getUserName(){
		return userName;
	}
	
	public Point getHomePoint() {
		return home;
	}
	
	public void addLocation(Location location) {
		locations.add(location);
	}
	
	public void removeLocation(Location location) {
		locations.remove(location);
	}
	
	/**
	 * Return list of locations this user is associated with. This
	 * list should not be modified, as the actual backing list is
	 * returned.
	 * 
	 * @return list of locations that the user is associated with
	 */
	public Set<Location> getLocations() {
		return locations;
	}
}
