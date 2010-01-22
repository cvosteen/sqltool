/**
 * Changes in v2.1:
 * - Added user friendly message to ClassNotFoundException
 * - Executes queries in another thread (see QueryThread class)
 */
import java.io.*;
import java.util.*;
import java.sql.*;

public class Database implements Serializable, Comparable<Database> {

	private String name;
	private String driver;
	private String connectionUrl;
	private Map<String, String> queries;
	private Statement currentStatement = null;
	transient private Connection connection = null;

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
	
	public List<String> getTables() throws SQLException {
		if(connection == null)
			throw new UnsupportedOperationException("You must be connected to the database first!");
		List<String> tableList = new ArrayList<String>();
		ResultSet tables = connection.getMetaData().getTables(null,null,"%",null);
		while(tables.next()) 
			tableList.add(tables.getString(3));
		tables.close();
		return tableList;
	}

	public List<String> getColumns(String table) throws SQLException {
		if(connection == null)
			throw new UnsupportedOperationException("You must be connected to the database first!");
		List<String> columnList = new ArrayList<String>();
		ResultSet columns = connection.getMetaData().getColumns(null,null,table,null);
		while(columns.next()) 
			columnList.add(columns.getString(4));
		columns.close();
		return columnList;
	}


	public void connect() throws SQLException, ClassNotFoundException {
		if(connection == null) {
			try {
				Class.forName(driver);
			} catch(ClassNotFoundException e) {
				ClassNotFoundException f = new ClassNotFoundException("Invalid database driver class.", e);
				throw(f);
			}
			connection = DriverManager.getConnection(connectionUrl);
		}
	}

	public void disconnect() throws SQLException {
		if(connection == null)
			throw new UnsupportedOperationException("You must be connected to the database first!");
		connection.close();
		connection = null;
	}

	// ****************
	// Query management
	// ****************
	public void saveQuery(String name, String sql) {
		queries.put(name, sql);
	}

	public void deleteQuery(String name) {
		queries.remove(name);
	}

	public String getQuerySql(String name) {
		if(name == null)
			return null;
		return queries.get(name);
	}

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

	public Object executeQuery(String name) throws SQLException {
		return executeAdHocSql(queries.get(name));
	}

	public Object executeAdHocSql(String sql) throws SQLException {
		// Returns a ResultSet for a SELECT query
		// and an Integer (row count) for other queries.
		currentStatement = connection.createStatement();

		// Try to return the first result set, if any,
		// otherwise return the last update count
		// We determine this as follows:
		// The result of Statement.execute is a boolean, True for a Result Set, False for not.
		// If no result set, check Statement.getUpdateCount:  -1 means no more results, any other number
		// is the update count.
		// Keep calling Statement.getMoreResults, which also returns a boolean like StatementExecute
		boolean isResultSet = currentStatement.execute(sql);
		int lastUpdateCount = -1;
		int updateCount = -1;

		if(isResultSet) {
			return currentStatement.getResultSet();
		} else {
			updateCount = currentStatement.getUpdateCount();
		}

		//  isResultSet && updateCount != -1 ==> ??? Error ???
		//  isResultSet && updateCount == -1 ==> Result Set
		// !isResultSet && updateCount != -1 ==> Update Count
		// !isResultSet && updateCount == -1 ==> No more results

		while(isResultSet || updateCount != -1) {
			isResultSet = currentStatement.getMoreResults();
			lastUpdateCount = updateCount;
			updateCount = currentStatement.getUpdateCount();

			if(isResultSet && updateCount == -1) {
				// We have a result set
				return currentStatement.getResultSet();
			}
		}
		
		return lastUpdateCount;
	}
			
	public QueryThread executeThreadedSql(String sql) {
		return new QueryThread(connection, sql);
	}

	public void cancelQuery() throws SQLException {
		currentStatement.cancel();
	}

	public void commit() throws SQLException {
		if(connection == null)
			throw new UnsupportedOperationException("You must be connected to the database first!");
		connection.commit();
	}

	public void rollback() throws SQLException {
		if(connection == null)
			throw new UnsupportedOperationException("You must be connected to the database first!");
		connection.rollback();
	}

	// Implement methods so this can be put into collections, etc.
	public boolean equals(Object obj) {
		if(!(obj instanceof Database))
			return false;
		if(((Database) obj).getName().equals(name))
			return true;
		return false;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public int compareTo(Database other) {
		return name.toLowerCase().compareTo(other.getName().toLowerCase());
	}

	public String toString() {
		return this.name;
	}
}
	
