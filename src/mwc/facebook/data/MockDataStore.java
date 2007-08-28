package mwc.facebook.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockDataStore implements DataStore{

	private Map<String, User> userMap = new HashMap<String, User>();
	private Map<Point, Location> locationMap = new HashMap<Point, Location>();
	private Set<Pair<User, Location>> userLocations = new HashSet<Pair<User,Location>>();
	private Map<Location, ArrayList<CommentContribution>> comments = new HashMap<Location, ArrayList<CommentContribution>>();
	private Map<User, ArrayList<CommentContribution>> userComments = new HashMap<User, ArrayList<CommentContribution>>();
	private Map<Location, Set<PhotoContribution>> photos = new HashMap<Location, Set<PhotoContribution>>();
	private Map<User, Set<PhotoContribution>> userPhotos = new HashMap<User, Set<PhotoContribution>>();
	private Map<Integer, PhotoContribution> photoIds = new HashMap<Integer, PhotoContribution>();
	private int photoCount = 0;
	
	public Location getLocationByPoint(Point point) {
		return locationMap.get(point);
	}

	public Set<Location> getLocationsWithin(Rectangle area) {
		Set<Location> locations = new HashSet<Location>();
		for (Location l : locationMap.values()) {
			if (area.contains(l.getCoordinates())) locations.add(l);
		}
		return locations;
	}

	public void addLocation(Location location) {
		locationMap.put(location.getCoordinates(), location);
	}

	public void addUser(User user) {
		userMap.put(user.getUser(), user);
	}

	public void addUserToLocation(User user, Location location) {
		userLocations.add(new Pair<User, Location>(user, location));
	}

	public Set<Location> locationsFor(User user) {
		Set<Location> result = new HashSet<Location>();
		
		for(Pair<User, Location> pear : userLocations) {
			if(pear.a.equals(user))
				result.add(pear.b);
		}
		return result;
	}

	public Set<User> usersAssociatedWith(Location location) {
		Set<User> result = new HashSet<User>();
		
		for(Pair<User, Location> pear : userLocations) {
			if(pear.b.equals(location))
				result.add(pear.a);
		}
		return result;
	}

	public void addCommentTo(User user, Location location, String comment)
	{
		ArrayList<CommentContribution> lList = comments.get(location);
		ArrayList<CommentContribution> uList = userComments.get(user);
		
		if (lList == null) {
			lList = new ArrayList<CommentContribution>();
			comments.put(location, lList);
		}
		if (uList == null) {
			uList = new ArrayList<CommentContribution>();
			userComments.put(user, lList);
		}
		
		CommentContribution c = new CommentContribution(comment, new Date(), user, location);
		
		lList.add(c);
		uList.add(c);
	}

	public int addPhotoTo(User user, Location location, byte[] photo, String description)
	{
		Set<PhotoContribution> lList = photos.get(location);
		Set<PhotoContribution> uList = userPhotos.get(user);
		
		if (lList == null) {
			lList = new HashSet<PhotoContribution>();
			photos.put(location, lList);
		}
		if (uList == null) {
			uList = new HashSet<PhotoContribution>();
			userPhotos.put(user, lList);
		}
		
		int id = photoCount++;
		
		PhotoContribution c = new PhotoContribution(photo, id, description, new Date(), user, location);
		
		lList.add(c);
		uList.add(c);
		photoIds.put(id,c);
		
		return id;
	}

	public ArrayList<CommentContribution> getCommentsFrom(Location location)
	{
		return comments.get(location);
	}

	public Set<PhotoContribution> getPhotosFrom(Location location)
	{
		return photos.get(location);
	}

	public Set<Location> getLocationsWithin(Point centre, double radius) {
		Set<Location> result = new HashSet<Location>();
		
		for(Location l : locationMap.values()) {
			if (l.getCoordinates().distanceTo(centre) <= radius) {
				result.add(l);
			}
		}
		return result;
	}

	public ArrayList<CommentContribution> getCommentsFrom(User user)
	{
		return userComments.get(user);
	}

	public Set<PhotoContribution> getPhotosFrom(User user)
	{
		return userPhotos.get(user);
	}

	public User getUserById(String id)
	{
		System.out.println(id);
		return userMap.get(id);
	}

	public void updateUser(User user)
	{
		userMap.remove(user.getUser());
		userMap.put(user.getUser(), user);
	}

	public PhotoContribution getPhotoById(int id) {
		return photoIds.get(id);
	}
}
