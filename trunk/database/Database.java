/**
 * The Database object serves as a glorified struct.
 * It merely holds database connection information, like
 * the connection url and the Driver.  It also holds
 * a collection of queries that are to be used with
 * the database.
 */
package database;

import java.io.*;
import java.util.*;
import java.sql.*;

public class Database implements Serializable, Comparable<Database> {

	private String name;
	private String driver;
	private String connectionUrl;
	private Map<String, String> queries;
	private Connection connection = null;

	public Database(String name, String driver, String connectionUrl) {
		setName(name);
		setDriver(driver);
		setConnectionUrl(connectionUrl);
		queries = new Hashtable<String, String>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if(name == null || name.length() == 0)
			throw new NullPointerException("You must specify a name!");
		this.name = name;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		if(driver == null)
			throw new NullPointerException("You must specify a driver!");
		this.driver = driver;
	}

	public String getConnectionUrl() {
		return this.connectionUrl;
	}

	public void setConnectionUrl(String connectionUrl) {
		if(connectionUrl == null)
			throw new NullPointerException("You must specify a connection URL!");
		this.connectionUrl = connectionUrl;
	}
	
	/**
	 * Returns a list of tables in the from the database connection.
	 */
	public static List<String> getTables(Connection connection) throws SQLException {
		List<String> tableList = new ArrayList<String>();
		ResultSet tables = connection.getMetaData().getTables(null,null,"%",null);
		while(tables.next()) 
			tableList.add(tables.getString(3));
		tables.close();
		return tableList;
	}

	/**
	 * Returns a list of columns in the specified table.
	 */
	public static List<String> getColumns(Connection connection, String table) throws SQLException {
		List<String> columnList = new ArrayList<String>();
		ResultSet columns = connection.getMetaData().getColumns(null,null,table,null);
		while(columns.next()) 
			columnList.add(columns.getString(4));
		columns.close();
		return columnList;
	}

	/**
	 * Connects to the database.
	 * Attempts to load the driver and connect to this instance's
	 * url.
	 */
	public Connection connect() throws SQLException, ClassNotFoundException {
		try {
			Class.forName(driver);
		} catch(ClassNotFoundException e) {
			ClassNotFoundException f = new ClassNotFoundException("Invalid database driver class.", e);
			throw(f);
		}
		connection = DriverManager.getConnection(connectionUrl);

		// TODO: Make this user configurable?
		connection.setAutoCommit(false);

		return connection;
	}

	/**
	 * Saves the specified SQL statement as the specified query name
	 * to this Database instance.
	 */
	public void saveQuery(String name, String sql) {
		queries.put(name, sql);
	}

	/**
	 * Removes the specified query from this instance.
	 */
	public void deleteQuery(String name) {
		queries.remove(name);
	}

	/**
	 * Returns the SQL for the specified query
	 */
	public String getQuerySql(String name) {
		if(name == null)
			return null;
		return queries.get(name);
	}

	/**
	 * Return a List of all of the query names
	 */
	public List<String> getAllQueries() {
		List<String> list = new ArrayList<String>(queries.keySet());
		Collections.sort(list, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return o1.toLowerCase().compareTo(o2.toLowerCase());
				}
				public boolean equals(Object obj) {
					return false;
				}
			});
		return list;
	}


	/**
	 * Allows comparison between instances for use in collections.
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof Database))
			return false;
		if(((Database) obj).getName().equals(name))
			return true;
		return false;
	}

	/**
	 * Allows comparison between instances for use in collections.
	 */
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Allows Database object to be sorted.
	 */
	public int compareTo(Database other) {
		return name.toLowerCase().compareTo(other.getName().toLowerCase());
	}

	/**
	 * Allows this object to print a human-friendly name when needed.
	 */
	public String toString() {
		return this.name;
	}
}
	
