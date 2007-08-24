package mwc.facebook.data;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Set;

public interface DataStore {
	public User getUserById(String id);
	public Location getLocationByPoint(Point location);
	public Set<Location> getLocationsWithin(Rectangle area);
	
	/**
	 * Gets all locations within a certain distance of a point.
	 * @param centre The centre point of the circle
	 * @param radius The maximum distance from the centre
	 * @return All locations within the circle described
	 */
	public Set<Location> getLocationsWithin(Point centre, double radius);
	
	public void addUser(User user);
	public void updateUser(User user);
	public void addLocation(Location location);
	
	public void addUserToLocation(User user, Location location);
	public Set<Location> locationsFor(User user);
	public Set<User> usersAssociatedWith(Location location);
	
	public int addPhotoTo(User user, Location location, byte[] photo, String description);
	public Set<PhotoContribution> getPhotosFrom(Location location);
	public PhotoContribution getPhotoById(int id);
	public Set<PhotoContribution> getPhotosFrom(User user);
	public void addCommentTo(User user, Location location, String comment);
	public ArrayList<CommentContribution> getCommentsFrom(Location location);
	public ArrayList<CommentContribution> getCommentsFrom(User user);
}
