package mwc.facebook.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.postgresql.largeobject.LargeObjectManager;

public class TestPGDataStore extends PostgresDataStore {

	public TestPGDataStore(String server, String database, Properties props) throws SQLException
	{
		super(server, database, props);
		deletePhotos();

		connection.createStatement().executeUpdate("DELETE FROM comment;");
		connection.createStatement().executeUpdate("DELETE FROM location_stalker;");
		connection.createStatement().executeUpdate("DELETE FROM stalker;");
		connection.createStatement().executeUpdate("DELETE FROM location;");
	}

	public TestPGDataStore(String server, String database, String user, String pass) throws SQLException
	{
		super(server, database, user, pass);
		deletePhotos();

		connection.createStatement().executeUpdate("DELETE FROM comment;");
		connection.createStatement().executeUpdate("DELETE FROM location_stalker;");
		connection.createStatement().executeUpdate("DELETE FROM stalker;");
		connection.createStatement().executeUpdate("DELETE FROM location;");
	}
	
	
	private synchronized void deletePhotos() {
		try {
			connection.setAutoCommit(false);
			ResultSet result = connection.createStatement().executeQuery("SELECT * FROM photo;");
			LargeObjectManager manager = ((org.postgresql.PGConnection)connection).getLargeObjectAPI();
			
			while(result.next()) {
				int oid = result.getInt("image");
				System.out.printf("Photo with oid (%d) deleted\n", oid);
				manager.delete(oid);
			}
			connection.createStatement().executeUpdate("DELETE FROM photo;");
			connection.commit();
			connection.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}
}
