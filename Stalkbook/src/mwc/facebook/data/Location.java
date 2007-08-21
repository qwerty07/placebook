package mwc.facebook.data;

public class Location {
	private Point coordinates;
	private String locationName;
	
	public Location(Point coordinates, String locationName){
		this.coordinates = coordinates;
		this.locationName = locationName;
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
	
	public boolean equals(Object other) {
		if (!(other instanceof Location)) return false;
		Location o = (Location) other;
		return coordinates.equals(o.getCoordinates()) && locationName.equals(o.getLocationName());
	}
}	
