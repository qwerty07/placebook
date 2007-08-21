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
	private PreparedStatement associateUserAndLocation;
	
	public PostgresDataStore(String server, String database, Properties props) throws SQLException {
		String url = "jdbc:postgresql://" + server + "/" + database;
		connection = DriverManager.getConnection(url, props);
		
		prepareStatements();
	}
	
	private void prepareStatements() throws SQLException {
		
		// Create prepared statements
		insertUser = connection.prepareStatement("INSERT INTO Stalker (fb_username, home_coord_x, home_coord_y) VALUES (?, ?, ?);");
		insertLocation = connection.prepareStatement("INSERT INTO location (coord_x, coord_y, loc_name) VALUES (?, ?, ?)");
		getUserByName = connection.prepareStatement("SELECT fb_username, home_coord_x, home_coord_y FROM Stalker WHERE fb_username = ?;");
		getLocationByPoint = connection.prepareStatement("SELECT loc_name, coord_x, coord_y FROM Location WHERE coord_x = ? AND coord_y = ?;");
		findLocationsInArea = connection.prepareStatement("SELECT loc_name, coord_x, coord_y FROM Location WHERE box(point(coord_x, coord_y),point(coord_x, coord_y)) && ?;");
		associateUserAndLocation = connection.prepareStatement("INSERT INTO stalker_location(fb_username, location_id) VALUES (?, ?);");
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

	public void addUserToLocation(User user, Location location) {
		//associateUserAndLocation.setObject(1, x)
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
			double loc_x = result.getDouble("coord_x");
			double loc_y = result.getDouble("coord_y");
			return new Location(new Point(loc_x, loc_y), locationName);
		} else {
			return null;
		}
	}
}
