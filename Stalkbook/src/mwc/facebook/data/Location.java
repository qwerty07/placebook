package mwc.facebook.data;

public class Location {
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
}	
