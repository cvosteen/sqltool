/**
 * Changes in v2.1:
 * - New class to perform query in a separate thread
 */
import java.io.*;
import java.util.*;
import java.sql.*;

public class QueryThread extends Thread {

	private Connection connection = null;
	private String sql = null;
	private Object result = null;
	private Exception error = null;
	private List<QueryListener> listeners = new Vector<QueryListener>();

	public QueryThread(Connection conn, String sql) {
		connection = conn;
		this.sql = sql;
	}
	
	public void addQueryListener(QueryListener listener) {
		listeners.add(listener);
	}

	public void removeQueryListener(QueryListener listener) {
		listeners.remove(listener);
	}

	private void queryCompleted() {
		for(QueryListener listener : listeners)
			listener.queryCompleted();
	}

	private void queryFailed() {
		for(QueryListener listener : listeners)
			listener.queryFailed();
	}

	public void run() {
		try {
			// Returns a ResultSet for a SELECT query
			// and an Integer (row count) for other queries.
			Statement stmt = connection.createStatement();

			// Try to return the first result set, if any,
			// otherwise return the last update count
			// We determine this as follows:
			// The result of Statement.execute is a boolean, True for a Result Set, False for not.
			// If no result set, check Statement.getUpdateCount:  -1 means no more results, any other number
			// is the update count.
			// Keep calling Statement.getMoreResults, which also returns a boolean like StatementExecute
			boolean isResultSet = stmt.execute(sql);
			int lastUpdateCount = -1;
			int updateCount = -1;

			if(isResultSet) {
				result = stmt.getResultSet();
				queryCompleted();	
				return;
			} else {
				updateCount = stmt.getUpdateCount();
			}

			//  isResultSet && updateCount != -1 ==> ??? Error ???
			//  isResultSet && updateCount == -1 ==> Result Set
			// !isResultSet && updateCount != -1 ==> Update Count
			// !isResultSet && updateCount == -1 ==> No more results

			while(isResultSet || updateCount != -1) {
				isResultSet = stmt.getMoreResults();
				lastUpdateCount = updateCount;
				updateCount = stmt.getUpdateCount();

				if(isResultSet && updateCount == -1) {
					// We have a result set
					result = stmt.getResultSet();
					queryCompleted();
					return;
				}
			}
			
			result = lastUpdateCount;
			queryCompleted();
			return;

		} catch(Exception e) {
			error = e;
			queryFailed();
		}
	}

	public Object getResult() {
		return result;
	}

	public Exception getError() {
		return error;
	}
			
}
