/**
 * Executes a PreparedStatment and returns an Integer (row count)
 * for SQL commands and a ResultSet for SELECT queries.
 *
 * The tricky thing with this is that some statements can
 * return more than one result.  For instance, DECLAREing a variable
 * before a SELECT statement can result in a row count of 1 for the 
 * DECLARE and a ResultSet for the SELECT.
 *
 * If any of the results is a ResultSet, the *first* ResultSet will
 * be returned.  Otherwise, the *last* row count will be returned.
 *
 * Also this "chain of results" must be walked like this because
 * grabbing the next ResultSet will immediately close the current
 * one.
 */
import java.io.*;
import java.util.*;
import java.sql.*;

public class ExecuteSqlTask extends Task {

	private PreparedStatement preparedStatement;

	public ExecuteSqlTask(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}

	public ExecuteSqlTask(Connection connection, String sql) {
		this.preparedStatement = connection.prepareStatement(sql);
	}
	
	public void run() {
		try {
			// Try to return the first result set, if any,
			// otherwise return the last update count
			// We determine this as follows:
			// The result of Statement.execute is a boolean, True for a Result Set, False for not.
			// If no result set, check Statement.getUpdateCount:  -1 means no more results, any other number
			// is the update count.
			// Keep calling Statement.getMoreResults, which also returns a boolean like StatementExecute
			boolean isResultSet = preparedStatement.execute();
			int lastUpdateCount = -1;
			int updateCount = -1;

			// Checking for cancellation
			if(isCancelled())
				return;

			if(isResultSet) {
				reportResult(preparedStatement.getResultSet());
				return;
			} else {
				updateCount = preparedStatement.getUpdateCount();
			}

			//  isResultSet && updateCount != -1 ==> ??? Error ???
			//  isResultSet && updateCount == -1 ==> Result Set
			// !isResultSet && updateCount != -1 ==> Update Count
			// !isResultSet && updateCount == -1 ==> No more results

			while(isResultSet || updateCount != -1) {
				// Checking for cancellation
				if(isCancelled())
					return;

				isResultSet = preparedStatement.getMoreResults();
				lastUpdateCount = updateCount;
				updateCount = preparedStatement.getUpdateCount();

				if(isResultSet && updateCount == -1) {
					// We have a result set
					reportResult(preparedStatement.getResultSet());
					return;
				}
			}
			
			reportResult(lastUpdateCount);
			
		} catch(Exception e) {
			// The Statement may have been cancelled resulting in a SQLException.
			// This should not be reported as an error.
			if(!isCancelled()) {
				reportError(e);
			}
		} finally {
			reportFinished();
		}
	}
}
