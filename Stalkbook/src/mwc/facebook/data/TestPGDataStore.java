package mwc.facebook.data;

import java.sql.SQLException;
import java.util.Properties;

public class TestPGDataStore extends PostgresDataStore {

	public TestPGDataStore(String server, String database, Properties props) throws SQLException
	{
		super(server, database, props);
		connection.createStatement().executeUpdate("DELETE FROM location_stalker;");
		connection.createStatement().executeUpdate("DELETE FROM stalker;");
		connection.createStatement().executeUpdate("DELETE FROM location;");
	}
	
}
