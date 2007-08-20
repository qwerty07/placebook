package mwc.facebook.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
	
	public static void main(String[] args) {
		try {
			PostgresDataStore ds = new PostgresDataStore("localhost", "stalkbook", new Properties());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Connection connection;
	
	public PostgresDataStore(String server, String database, Properties props) throws SQLException {
		String url = "jdbc:postgresql://" + server + "/" + database;
		connection = DriverManager.getConnection(url, props);
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

	public void addUser(User user) {
		// TODO Auto-generated method stub
		
	}

	public Set<Location> getLocationsWithin(Rectangle area) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUserByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Location getLocationByPoint(Point location) {
		// TODO Auto-generated method stub
		return null;
	}
}
