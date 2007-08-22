package mwc.facebook.data;

import mwc.facebook.JSONable;

public class Location implements JSONable {
	private Point coordinates;
	private String locationName;
	private String description;
	
	public Location(Point coordinates, String locationName){
		this(coordinates, locationName, null);
	}
	
	public Location(Point coordinates, String locationName, String description){
		this.coordinates = coordinates;
		this.locationName = locationName;
		this.description = description;
	}
	
	public String getLocationName(){
		return locationName;
	}

	public Point getCoordinates() {
		return coordinates;
	}
	
	public int hashCode() {
		return coordinates.hashCode();
	}
	
	public String getDescription(){
		return description;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Location)) return false;
		Location o = (Location) other;
		return coordinates.equals(o.getCoordinates());
	}

	public String toJSON() {
		StringBuffer sb = new StringBuffer();
		sb.append("{coordinates: " + coordinates.toJSON());
		if (locationName != null) {
			sb.append(", name: \"" + this.locationName + "\"");
		}
		if (description != null) {
			sb.append(", desc: \"" + this.description + "\"");
		}
		sb.append("}");
		return sb.toString();
	}
}	
