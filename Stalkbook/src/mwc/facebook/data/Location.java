package mwc.facebook.data;

import java.util.HashSet;
import java.util.Set;

public class Location {
	private Point coordinates;
	private String locationName;
	private Set<User> stalkers;
	
	public Location(Point coordinates, String locationName){
		this.coordinates = coordinates;
		this.locationName = locationName;
		
		stalkers = new HashSet<User>();
	}
	
	public String getLocationName(){
		return locationName;
	}

	public Point getCoordinates() {
		return coordinates;
	}
	
	public void addStalker(User stalker){
		stalkers.add(stalker);
	}
}
