package cz.nos.twitterbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

public class Database {

	private static final Logger logger = Logger.getLogger(Database.class.getName());
	private static final String DB_URI = "jdbc:sqlite:";

	Connection connection = null;
	String dbUrl;

	public Database() throws BackingStoreException, SQLException {

		Config config = new Config();

		dbUrl = config.getDbUrl();

		// create a database connection
		connection = DriverManager.getConnection(DB_URI + dbUrl);

		try (Statement statement = connection.createStatement()) {

			statement.setQueryTimeout(30); // set timeout to 30 sec.

			statement.executeUpdate(
					"create table if not exists tweets " + "(id integer not null primary key autoincrement,"
							+ "tweet_id text not null unique," + "tweet_text text not null)");

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			logger.severe(e.getMessage());
		} finally {
			connection.close();
		}
	}

	public boolean storeTweet(String tweetId, String tweetText) throws SQLException {

		Boolean result = false;
		connection = DriverManager.getConnection(DB_URI + dbUrl);
		try (Statement statement = connection.createStatement()) {

			statement.setQueryTimeout(30); // set timeout to 30 sec.

			try (PreparedStatement stmt = connection.prepareStatement("insert into tweets values(null,?,?)")) {
				stmt.setString(1, tweetId);
				stmt.setString(2, tweetText);

				int i = stmt.executeUpdate();
				logger.info(i + " records inserted");

				result = true;
			} catch (SQLException e) {
				// if the error message is "out of memory",
				// it probably means no database file is found
				logger.severe(e.getMessage());
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			logger.severe(e.getMessage());
		} finally {
			connection.close();
		}

		return result;
	}

	public boolean findTweet(String tweetId) throws SQLException {

		connection = DriverManager.getConnection(DB_URI + dbUrl);

		try (Statement statement = connection.createStatement()) {

			statement.setQueryTimeout(30); // set timeout to 30 sec.

			try (PreparedStatement stmt = connection.prepareStatement("select * from tweets where tweet_id = (?)")) {

				stmt.setString(1, tweetId);

				ResultSet rs = stmt.executeQuery();

				if (!rs.wasNull()) {
					rs.next();
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				// if the error message is "out of memory",
				// it probably means no database file is found
				logger.severe(e.getMessage());
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			logger.severe(e.getMessage());
		} finally {
			connection.close();
		}
		return false;
	}
}
