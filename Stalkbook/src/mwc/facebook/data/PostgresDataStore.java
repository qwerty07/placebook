package mwc.facebook.data;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.postgresql.ds.common.PGObjectFactory;
import org.postgresql.geometric.PGbox;
import org.postgresql.geometric.PGpoint;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import com.sun.org.apache.bcel.internal.util.ByteSequence;

public class PostgresDataStore implements DataStore {
	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private PreparedStatement insertUser;
	private PreparedStatement insertLocation;
	
	protected Connection connection;
	private PreparedStatement getUserById;
	private PreparedStatement getLocationByPoint;
	private PreparedStatement findLocationsInArea;
	private PreparedStatement associateUserAndLocation;
	private PreparedStatement findUsersByLocation;
	private PreparedStatement findLocationsForUser;
	private PreparedStatement addPhotoToLocation;
	private PreparedStatement getPhotosFromLocation;
	private PreparedStatement findLocationsInCircle;
	private PreparedStatement addCommentToLocation;
	private PreparedStatement getCommentsFromLocation;
	private PreparedStatement getPhotosForUser;
	private PreparedStatement getCommentsForUser;
	private PreparedStatement updateUser;
	
	public PostgresDataStore(String server, String database, Properties props) throws SQLException {
		String url = "jdbc:postgresql://" + server + "/" + database;
		connection = DriverManager.getConnection(url, props);
		
		prepareStatements();
	}
	
	private void prepareStatements() throws SQLException {
		
		// Create prepared statements
		insertUser = connection.prepareStatement("INSERT INTO Stalker (fb_id, fb_name, home_coord_x, home_coord_y) VALUES (?, ?, ?, ?);");
		updateUser = connection.prepareStatement("UPDATE stalker SET fb_id = ?, fb_name = ?, home_coord_x = ?, home_coord_y = ? WHERE fb_id = ?;");
		insertLocation = connection.prepareStatement("INSERT INTO location (coord_x, coord_y, loc_name, description) VALUES (?, ?, ?, ?);");
		getUserById = connection.prepareStatement("SELECT fb_id, fb_name, home_coord_x, home_coord_y FROM Stalker WHERE fb_id = ?;");
		getLocationByPoint = connection.prepareStatement("SELECT loc_name, coord_x, coord_y, description FROM Location WHERE coord_x = ? AND coord_y = ?;");
		findLocationsInArea = connection.prepareStatement("SELECT loc_name, coord_x, coord_y, description FROM Location WHERE box(point(coord_x, coord_y),point(coord_x, coord_y)) && ?;");
		findLocationsInCircle = connection.prepareStatement("SELECT loc_name, coord_x, coord_y, description FROM Location WHERE box(point(coord_x, coord_y),point(coord_x, coord_y)) && box(circle(?,?)) AND (point(coord_x, coord_y) <-> ?) < ?;");
		associateUserAndLocation = connection.prepareStatement("INSERT INTO location_stalker (stalker_fb_id, coord_x, coord_y) VALUES (?, ?, ?);");
		findUsersByLocation = connection.prepareStatement("SELECT stalker.fb_id, stalker.fb_name, home_coord_x, home_coord_y FROM stalker NATURAL JOIN location_stalker WHERE coord_x = ? AND coord_y = ?;");
		findLocationsForUser = connection.prepareStatement("SELECT location.coord_x, location.coord_y, location.loc_name, location.description FROM location NATURAL JOIN location_stalker WHERE stalker_fb_id = ?;");
		addPhotoToLocation = connection.prepareStatement("INSERT INTO photo(coord_x, coord_y, stalker_fb_id, description, image) VALUES (?, ?, ?, ?, ?);");
		getPhotosFromLocation = connection.prepareStatement("SELECT coord_x, coord_y, stalker_fb_id, description, image, contributed FROM photo WHERE coord_x = ? AND coord_y = ?;");
		getPhotosForUser = connection.prepareStatement("SELECT stalker_fb_id, coord_x, coord_y, description, image, contributed FROM photo WHERE stalker_fb_id = ?;");
		addCommentToLocation = connection.prepareStatement("INSERT INTO comment(coord_x, coord_y, stalker_fb_id, comment) VALUES (?, ?, ?, ?);");
		getCommentsFromLocation = connection.prepareStatement("SELECT coord_x, coord_y, stalker_fb_id, comment, contributed FROM comment WHERE coord_x = ? AND coord_y = ?;");
		getCommentsForUser = connection.prepareStatement("SELECT coord_x, coord_y, stalker_fb_id, comment, contributed FROM comment WHERE stalker_fb_id = ?;");
	}
	
	public PostgresDataStore(String server, String database, String username, String password) throws SQLException {
		this(server, database, makeUserAndPasswordProperties(username, password));
	}
	
	private static Properties makeUserAndPasswordProperties(String username, String password) {
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		return props;
	}

	public synchronized void addLocation(Location location) {
		try {
			insertLocation.setDouble(1, location.getCoordinates().x);
			insertLocation.setDouble(2, location.getCoordinates().y);
			insertLocation.setString(3, location.getLocationName());
			insertLocation.setString(4, location.getDescription());
			insertLocation.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public synchronized void addUser(User user) {
		try {
			insertUser.setString(1, user.getUser());
			insertUser.setString(2, user.getName());
			insertUser.setDouble(3, user.getHomePoint().x);
			insertUser.setDouble(4, user.getHomePoint().y);
			insertUser.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}
	
	public synchronized void updateUser(User user) {
		try {
			updateUser.setString(1, user.getUser());
			updateUser.setString(2, user.getName());
			updateUser.setDouble(3, user.getHomePoint().x);
			updateUser.setDouble(4, user.getHomePoint().y);
			updateUser.setString(5, user.getUser());
			updateUser.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public synchronized User getUserById(String id) {
		try
		{
			getUserById.setString(1, id);
			ResultSet result = getUserById.executeQuery();
			return createUserFromResult(result);
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		return null;
	}

	public synchronized Location getLocationByPoint(Point location) {
		try
		{
			getLocationByPoint.setDouble(1, location.x);
			getLocationByPoint.setDouble(2, location.y);
			ResultSet result = getLocationByPoint.executeQuery();
			return createLocationFromResult(result);
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		return null;
	}

	public synchronized Set<Location> getLocationsWithin(Rectangle area) {
		Set<Location> locations = new HashSet<Location>();
		try {
			findLocationsInArea.setObject(1, new PGbox(area.getTopLeft(), area.getBottomRight()));
			ResultSet result = findLocationsInArea.executeQuery();
			Location loc = createLocationFromResult(result);
			while (loc != null) {
				locations.add(loc);
				loc = createLocationFromResult(result);
			}
		} catch (SQLException e) {
			handleError(e);
		}
		return locations;
	}
	
	private void handleError(SQLException e) {
		e.printStackTrace();
		Assert.fail();
	}

	protected Connection getConnection() {
		return connection;
	}

	public synchronized void addUserToLocation(User user, Location location) {
		try
		{
			associateUserAndLocation.setString(1, user.getUser());
			associateUserAndLocation.setDouble(2, location.getCoordinates().x);
			associateUserAndLocation.setDouble(3, location.getCoordinates().y);
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		try
		{
			associateUserAndLocation.executeUpdate();
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 23505) {
				// TODO: update instead?
			} else {
				handleError(e);
			}
		}
	}
	
	public synchronized void addPhotoTo(User user, Location location, byte[] photo, String description) {
		try
		{
			connection.setAutoCommit(false);
			LargeObjectManager manager = ((org.postgresql.PGConnection)connection).getLargeObjectAPI();
			long oid =manager.createLO();
			System.out.printf("Large object created with oid (%d)\n", oid);
			LargeObject image = manager.open(oid);
			image.write(photo);
			image.close();
			
			addPhotoToLocation.setDouble(1, location.getCoordinates().x);
			addPhotoToLocation.setDouble(2, location.getCoordinates().y);
			addPhotoToLocation.setString(3, user.getUser());
			addPhotoToLocation.setString(4, description);
			addPhotoToLocation.setLong(5, oid);
			addPhotoToLocation.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			handleError(e);
		}
	}
	
	public synchronized Set<PhotoContribution> getPhotosFrom(Location location) {
		Set<PhotoContribution> photos = new HashSet<PhotoContribution>();
		
		try
		{
			getPhotosFromLocation.setDouble(1, location.getCoordinates().x);
			getPhotosFromLocation.setDouble(2, location.getCoordinates().y);
			ResultSet result = getPhotosFromLocation.executeQuery();
			
			PhotoContribution p = createPhotoFromResult(result);
			while(p != null) {
				photos.add(p);
				p = createPhotoFromResult(result);
			}
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		return photos;
	}
	
	public synchronized Set<PhotoContribution> getPhotosFrom(User user)
	{
		Set<PhotoContribution> photos = new HashSet<PhotoContribution>();
		
		try
		{
			getPhotosForUser.setString(1, user.getUser());
			ResultSet result = getPhotosForUser.executeQuery();
			
			PhotoContribution p = createPhotoFromResult(result);
			while(p != null) {
				photos.add(p);
				p = createPhotoFromResult(result);
			}
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		return photos;
	}

	public synchronized void addCommentTo(User user, Location location, String comment) {
		try
		{
			addCommentToLocation.setDouble(1, location.getCoordinates().x);
			addCommentToLocation.setDouble(2, location.getCoordinates().y);
			addCommentToLocation.setString(3, user.getUser());
			addCommentToLocation.setString(4, comment);
			addCommentToLocation.executeUpdate();
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		
	}
	
	public synchronized Set<CommentContribution> getCommentsFrom(Location location) {
		Set<CommentContribution> comments = new HashSet<CommentContribution>();
		try
		{
			getCommentsFromLocation.setDouble(1, location.getCoordinates().x);
			getCommentsFromLocation.setDouble(2, location.getCoordinates().y);
			ResultSet result = getCommentsFromLocation.executeQuery();
			
			CommentContribution comment = createCommentFromResult(result);
			while(comment != null) {
				comments.add(comment);
				comment = createCommentFromResult(result);
			}
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		return comments;
	}
	
	public synchronized Set<CommentContribution> getCommentsFrom(User user)
	{
		Set<CommentContribution> comments = new HashSet<CommentContribution>();
		try {
			getCommentsForUser.setString(1, user.getUser());
			ResultSet result = getCommentsFromLocation.executeQuery();
			
			CommentContribution comment = createCommentFromResult(result);
			while(comment != null) {
				comments.add(comment);
				comment = createCommentFromResult(result);
			}
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		
		return comments;
	}

	public synchronized Set<Location> locationsFor(User user) {
		Set<Location> locations = new HashSet<Location>();
		try
		{
			findLocationsForUser.setString(1, user.getUser());
			ResultSet result = findLocationsForUser.executeQuery();
			
			Location l = createLocationFromResult(result);
			while(l != null) {
				locations.add(l);
				l = createLocationFromResult(result);
			}
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		
		return locations;
	}

	public synchronized Set<User> usersAssociatedWith(Location location) {
		Set<User> users = new HashSet<User>();
		try
		{
			findUsersByLocation.setDouble(1, location.getCoordinates().x);
			findUsersByLocation.setDouble(2, location.getCoordinates().y);
			ResultSet result = findUsersByLocation.executeQuery();
			User u = createUserFromResult(result);
			while (u != null) {
				users.add(u);
				u = createUserFromResult(result);
			}
		}
		catch (SQLException e)
		{
			handleError(e);
		}
		return users;
	}
	
	private User createUserFromResult(ResultSet result) throws SQLException {
		if (result.next()) {
			String id = result.getString("fb_id");
			String username = result.getString("fb_name");
			double home_x = result.getDouble("home_coord_x");
			boolean xWasNull = result.wasNull();
			double home_y = result.getDouble("home_coord_y");
			boolean yWasNull = result.wasNull();
			if (xWasNull || yWasNull) return new User(id, username, null);
			return new User(id, username, new Point(home_x, home_y));
		} else {
			return null;
		}
	}
	
	private Location createLocationFromResult(ResultSet result) throws SQLException {
		if (result.next()) {
			String locationName = result.getString("loc_name");
			String description = result.getString("description");
			double loc_x = result.getDouble("coord_x");
			double loc_y = result.getDouble("coord_y");
			return new Location(new Point(loc_x, loc_y), locationName, description);
		} else {
			return null;
		}
	}
	
	private PhotoContribution createPhotoFromResult(ResultSet result) throws SQLException {
		if(result.next()) {
			// fb_username, description, image, contributed 
			connection.setAutoCommit(false);
			
			User username = getUserById(result.getString("stalker_fb_id"));
			double x =result.getDouble("coord_x");
			double y =result.getDouble("coord_y");
			Location location = getLocationByPoint(new Point(x,y));
			String description = result.getString("description");
			Date contributed = result.getDate("contributed");
			
			long oid = result.getLong("image");
			
			LargeObjectManager manager = ((org.postgresql.PGConnection)connection).getLargeObjectAPI();
			LargeObject l = manager.open(oid);
			byte[] image = l.read(l.size());
			l.close();
			
			PhotoContribution photo = new PhotoContribution(image, description, contributed, username, location);
			
			connection.commit();
			connection.setAutoCommit(true);
			return photo;
			
		} else {
			return null;
		}
	}
	
	private CommentContribution createCommentFromResult(ResultSet result) throws SQLException
	{
		if(result.next()) {
			String comment = result.getString("comment");
			User username = getUserById(result.getString("stalker_fb_id"));
			double x =result.getDouble("coord_x");
			double y =result.getDouble("coord_y");
			Location location = getLocationByPoint(new Point(x,y));
			Date contributed = result.getDate("contributed");
			
			return new CommentContribution(comment, contributed, username, location);
		} else {
			return null;
		}
	}

	public synchronized Set<Location> getLocationsWithin(Point centre, double radius) {
		Set<Location> locations = new HashSet<Location>();
		try {
			findLocationsInCircle.setObject(1, centre);
			findLocationsInCircle.setDouble(2, radius);
			findLocationsInCircle.setObject(3, centre);
			findLocationsInCircle.setDouble(4, radius);
			ResultSet result = findLocationsInCircle.executeQuery();
			Location loc = createLocationFromResult(result);
			while (loc != null) {
				locations.add(loc);
				loc = createLocationFromResult(result);
			}
		} catch (SQLException e) {
			handleError(e);
		}
		return locations;
	}


}
