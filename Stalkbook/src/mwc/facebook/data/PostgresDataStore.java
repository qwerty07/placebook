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
	private PreparedStatement getUserByName;
	private PreparedStatement getLocationByPoint;
	private PreparedStatement findLocationsInArea;
	private PreparedStatement associateUserAndLocation;
	private PreparedStatement findUsersByLocation;
	private PreparedStatement findLocationsForUser;
	private PreparedStatement addPhotoToLocation;
	private PreparedStatement getPhotosFromLocation;
	private PreparedStatement findLocationsInCircle;
	
	public PostgresDataStore(String server, String database, Properties props) throws SQLException {
		String url = "jdbc:postgresql://" + server + "/" + database;
		connection = DriverManager.getConnection(url, props);
		
		prepareStatements();
	}
	
	private void prepareStatements() throws SQLException {
		
		// Create prepared statements
		insertUser = connection.prepareStatement("INSERT INTO Stalker (fb_username, home_coord_x, home_coord_y) VALUES (?, ?, ?);");
		insertLocation = connection.prepareStatement("INSERT INTO location (coord_x, coord_y, loc_name, description) VALUES (?, ?, ?, ?);");
		getUserByName = connection.prepareStatement("SELECT fb_username, home_coord_x, home_coord_y FROM Stalker WHERE fb_username = ?;");
		getLocationByPoint = connection.prepareStatement("SELECT loc_name, coord_x, coord_y, description FROM Location WHERE coord_x = ? AND coord_y = ?;");
		findLocationsInArea = connection.prepareStatement("SELECT loc_name, coord_x, coord_y, description FROM Location WHERE box(point(coord_x, coord_y),point(coord_x, coord_y)) && ?;");
		findLocationsInCircle = connection.prepareStatement("SELECT loc_name, coord_x, coord_y, description FROM Location WHERE box(point(coord_x, coord_y),point(coord_x, coord_y)) && box(circle(?,?)) AND (point(coord_x, coord_y) <-> ?) < ?;");
		associateUserAndLocation = connection.prepareStatement("INSERT INTO location_stalker (fb_username, coord_x, coord_y) VALUES (?, ?, ?);");
		findUsersByLocation = connection.prepareStatement("SELECT stalker.fb_username, home_coord_x, home_coord_y FROM stalker NATURAL JOIN location_stalker WHERE coord_x = ? AND coord_y = ?;");
		findLocationsForUser = connection.prepareStatement("SELECT location.coord_x, location.coord_y, location.loc_name, location.description FROM location NATURAL JOIN location_stalker WHERE fb_username = ?;");
		addPhotoToLocation = connection.prepareStatement("INSERT INTO photo(coord_x, coord_y, fb_username, description, image) VALUES (?, ?, ?, ?, ?);");
		getPhotosFromLocation = connection.prepareStatement("SELECT fb_username, description, image, contributed FROM photo WHERE coord_x = ? AND coord_y = ?;");
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
			insertUser.setString(1, user.getUserName());
			insertUser.setDouble(2, user.getHomePoint().x);
			insertUser.setDouble(3, user.getHomePoint().y);
			insertUser.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public synchronized User getUserByName(String name) {
		try
		{
			getUserByName.setString(1, name);
			ResultSet result = getUserByName.executeQuery();
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
	}

	protected Connection getConnection() {
		return connection;
	}

	public synchronized void addUserToLocation(User user, Location location) {
		try
		{
			associateUserAndLocation.setString(1, user.getUserName());
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
			addPhotoToLocation.setString(3, user.getUserName());
			addPhotoToLocation.setString(4, description);
			addPhotoToLocation.setLong(5, oid);
			addPhotoToLocation.execute();
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
			e.printStackTrace();
		}
		return photos;
	}

	public synchronized void addCommentTo(User user, Location location, String comment) {
		// TODO
	}
	
	public synchronized Set<CommentContribution> getCommentsFrom(Location location) {
		// TODO 
		return null;
	}

	public synchronized Set<Location> locationsFor(User user) {
		Set<Location> locations = new HashSet<Location>();
		try
		{
			findLocationsForUser.setString(1, user.getUserName());
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
			String username = result.getString("fb_username");
			double home_x = result.getDouble("home_coord_x");
			boolean xWasNull = result.wasNull();
			double home_y = result.getDouble("home_coord_y");
			boolean yWasNull = result.wasNull();
			if (xWasNull || yWasNull) return new User(username, null);
			return new User(username, new Point(home_x, home_y));
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
			
			String username = result.getString("fb_username");
			String description = result.getString("description");
			Date contributed = result.getDate("contributed");
			long oid = result.getLong("image");
			
			LargeObjectManager manager = ((org.postgresql.PGConnection)connection).getLargeObjectAPI();
			LargeObject l = manager.open(oid);
			byte[] image = l.read(l.size());
			l.close();
			
			PhotoContribution photo = new PhotoContribution(image, description, contributed, username);
			
			connection.commit();
			connection.setAutoCommit(true);
			return photo;
			
		} else {
			return null;
		}
			
	}

	public Set<Location> getLocationsWithin(Point centre, double radius) {
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
