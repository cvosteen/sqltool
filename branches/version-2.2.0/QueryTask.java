import java.io.*;
import java.util.*;
import java.sql.*;

public class QueryTask extends Task implements TaskListener {

	private PreparedStatement preparedStatement;

	public QueryTask(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}

	public QueryTask(Connection connection, String sql) {
		this.preparedStatement = connection.prepareStatement(sql);
	}

	public void run() {
		try {
			ExecuteSqlTask t = new ExecuteSqlTask(database, sql);
			t.addTaskListener(this);
			t.start();

			// While this is running the user may request cancellation.
			// Some databases support cancelling of queries from another
			// thread.
			while(!t.isFinished() && !t.isCancelled()) {
				// Every 0.1 second, check for cancellation
				sleep(100);
				if(isCancelled()) {
					try {
						database.cancelQuery();
					} catch(SQLFeatureNotSupportedException e) {
						// Don't propagate this exception and don't finish the
						// task.  The query cannot be cancelled and the user
						// just has to wait.
					}
				}
			}

		} catch(Exception e) {
			// Only if there is an error above are we finished here.
			// Otherwise we wait for a result or error from the subtask
			reportError(e);
			reportFinished();
		}
	}

	// These two are ignored
	public void taskFinished() { }
	public void taskStatus(Object obj) { }

	public void taskResult(Object obj) {
		// Here we get the result from the query
		try {
			if(obj instanceof Integer) {
				// Okay, number of rows modified
				reportResult(obj);
			} else {
				// Result set - let's start retrieving it
				ResultSet rs = (ResultSet) obj;

				// First get the column names
				ResultSetMetaData rsmeta = rs.getMetaData();
				int colCount = rsmeta.getColumnCount();
				Vector<String> columns = new Vector<String>();
				for(int col = 1; col <= colCount; col++)
					columns.add(rsmeta.getColumnName(col));
				// First status report is the columns
				reportStatus(columns);

				if(isCancelled()) {
					return;
				}
				
				// Now we are going to retrieve rows
				// and report the results via status()
				// once per second
				long time = System.currentTimeMillis();
				Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
				while(rs.next()) {
					// Read in a row and add it to our Vector
					Vector<Object> row = new Vector<Object>();
					for(int col = 1; col <= colCount; col++)
						row.add(rs.getObject(col));
					rows.add(row);

					// After 1 second, send all rows to listeners
					if(System.currentTimeMillis() - time >= 1000) {
						reportStatus(rows);
						rows = new Vector<Vector<Object>>();
						time = System.currentTimeMillis();

						if(isCancelled()) {
							return;
						}
					}
				}
			}
		} catch(Exception e) {
			reportError(e);
		} finally {
			reportFinished();
		}
       	}

	public void taskError(Exception e) {
		// The query had an error
		// We shall propagate it to our listeners
		reportError(e);
	}

}
