package mwc.facebook.data;

import java.sql.SQLException;
import java.util.Properties;

public class TestPGDataStore extends PostgresDataStore {

	public TestPGDataStore(String server, String database, Properties props) throws SQLException
	{
		super(server, database, props);
		connection.createStatement().executeUpdate("DELETE FROM Stalker;");
		connection.createStatement().executeUpdate("DELETE FROM Location;");
	}
	
}
