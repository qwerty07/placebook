package mwc.facebook.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.postgresql.geometric.PGbox;
import org.postgresql.geometric.PGpoint;

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
	
	public PostgresDataStore(String server, String database, Properties props) throws SQLException {
		String url = "jdbc:postgresql://" + server + "/" + database;
		connection = DriverManager.getConnection(url, props);
		
		prepareStatements();
	}
	
	private void prepareStatements() throws SQLException {
		
		// Create prepared statements
		insertUser = connection.prepareStatement("INSERT INTO Stalker (fb_username, home_coords) VALUES (?, ?);");
		insertLocation = connection.prepareStatement("INSERT INTO location (coords, loc_name) VALUES (?, ?)");
		getUserByName = connection.prepareStatement("SELECT fb_username, home_coords FROM Stalker WHERE fb_username = ?;");
		getLocationByPoint = connection.prepareStatement("SELECT loc_name, coords FROM Location WHERE coords ~= ?;");
		findLocationsInArea = connection.prepareStatement("SELECT loc_name, coords FROM Location WHERE box(coords,coords) && ?;");
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
			insertLocation.setObject(1, location.getCoordinates());
			insertLocation.setString(2, location.getLocationName());
			insertLocation.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public synchronized void addUser(User user) {
		try {
			insertUser.setString(1, user.getUserName());
			insertUser.setObject(2, user.getHomePoint());
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
			getLocationByPoint.setObject(1, location);
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

	public void addUserToLocation(User user, Location location) {
		// TODO Auto-generated method stub
	}

	public Set<Location> locationsFor(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<User> usersAssociatedWith(Location location) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private User createUserFromResult(ResultSet result) throws SQLException {
		if (result.next()) {
			String username = result.getString("fb_username");
			PGpoint homeCoords = (PGpoint) result.getObject("home_coords");
			if (homeCoords == null) return new User(username, null);
			return new User(username, new Point(homeCoords.x, homeCoords.y));
		} else {
			return null;
		}
	}
	
	private Location createLocationFromResult(ResultSet result) throws SQLException {
		if (result.next()) {
			String locationName = result.getString("loc_name");
			PGpoint locationCoords = (PGpoint) result.getObject("coords");
			return new Location(new Point(locationCoords.x, locationCoords.y), locationName);
		} else {
			return null;
		}
	}
}
