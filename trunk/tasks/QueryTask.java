/**
 * Parent task of ExecuteSqlTask and ResultSetTask.
 * This task is needed both as a facade and also to
 * cancel the ExecuteSqlTask query from another thread.
 */

package tasks;
import task.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class QueryTask extends Task {

	private PreparedStatement preparedStatement;

	public QueryTask(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}

	public QueryTask(Connection connection, String sql) throws SQLException {
		this.preparedStatement = connection.prepareStatement(sql);
	}

	public void run() {
		try {
			ExecuteSqlTask t = new ExecuteSqlTask(preparedStatement);
			t.addTaskListener(new ExecuteSqlTaskListener());
			t.start();

			// While this is running the user may request cancellation.
			// Some databases support cancelling of queries from another
			// thread.
			while(!t.isFinished() && !t.isCancelled()) {
				// Every 0.1 second, check for cancellation
				sleep(100);
				if(isCancelled()) {
					try {
						t.cancel();
						preparedStatement.cancel();
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


	/**
	 * The TaskListener for the ExecuteSqlTask.
	 */
	class ExecuteSqlTaskListener implements TaskListener {

		/**
		 * ExecuteSqlTask does not report its status.
		 */
		public void taskStatus(Object obj) { }

		/**
		 * This should be ignored.
		 * The ExecuteSqlTask will either report a result or an error.
		 * Even in the case that it was cancelled, a cancelled Statement
		 * throws an SQL exception, resulting in a reported error.
		 *
		 * Just because the ExecuteSqlTask has finished does not necessarily
		 * mean that this task is also finished.
		 */
		public void taskFinished() { }

		/**
		 * The result should either be an Integer or a ResultSet.
		 * If it is an Integer the result should be reported.  If
		 * it is a ResultSet, the ResultSet task should be run on it
		 * and its results returned to our observers.
		 */
		public void taskResult(Object obj) {
			if(obj instanceof ResultSet) {
				Task t = new ResultSetTask((ResultSet) obj);
				t.addTaskListener(new ResultSetTaskListener());
				t.start();
				
				// While this is running the user may request cancellation.
				while(!t.isFinished() && !t.isCancelled()) {
					// Every 0.1 second, check for cancellation
					try {
						sleep(100);
					} catch(InterruptedException e) { }

					if(isCancelled()) {
						t.cancel();
					}
				}
			} else {
				reportResult(obj);
				reportFinished();
			}

		}

		/**
		 * The query had an error or was cancelled.
		 * This error should just be reported. This also means
		 * that this task has finished.
		 */
		public void taskError(Exception e) {
			reportError(e);
			reportFinished();
		}
	}

	/**
	 * The TaskListener for the ResultSetTask.
	 */
	class ResultSetTaskListener implements TaskListener {

		/**
		 * The task raised an exception.
		 * This error should just be reported.
		 */
		public void taskError(Exception e) { 
			reportError(e);
		}

		/**
		 * The ResultSetTask has finished.
		 * This means the QueryTask is definitely finished.
		 */
		public void taskFinished() {
			reportFinished();
		}

		/**
		 * The ResultSetTask returns no result.
		 * Instead it reports status as it retrieves
		 * table rows.  Just ignore this.
		 */
		public void taskResult(Object obj) { }

		/**
		 * The ResultSet should return first
		 * the columns, and then the rows.
		 * Report these results to observers of
		 * this task.
		 */
		public void taskStatus(Object obj) {
			reportStatus(obj);
		}
	}
}

