package mwc.facebook.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class PostgresDataStore implements DataStore {
	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private PreparedStatement insertUser;
	
	private Connection connection;
	
	public PostgresDataStore(String server, String database, Properties props, boolean testing) throws SQLException {
		String url = "jdbc:postgresql://" + server + "/" + database;
		connection = DriverManager.getConnection(url, props);
		
		prepareStatements();
	}
	
	public PostgresDataStore(String server, String database, Properties props) throws SQLException {
		this(server, database, props, false);
	}
	
	private void prepareStatements() throws SQLException {
		
		// Create prepared statements
		insertUser = connection.prepareStatement("INSERT INTO Stalker (fb_username) VALUES (?);");
		
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

	public void addLocation(Location location) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void addUser(User user) {
		try {
			insertUser.setString(1, user.getUserName());
			insertUser.executeUpdate();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public Set<Location> getLocationsWithin(Rectangle area) {
		// TODO Auto-generated method stub
		return new HashSet<Location>();
	}

	public User getUserByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Location getLocationByPoint(Point location) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void handleError(SQLException e) {
		e.printStackTrace();
	}

	protected Connection getConnection() {
		return connection;
	}
}
