package mwc.facebook.data;

import java.awt.Point;

public class Location {
	private Point location;
	private String locationName;
	
	public Location(Point location, String locationName){
		this.location = location;
		this.locationName = locationName;
	}
	
	public String getLocationName(){
		return locationName;
	}
}
