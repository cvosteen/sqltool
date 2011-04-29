/**
 * Closes a database connection.
 *
 * This is set up as a separate task because the actual close operation
 * make take some time if a query is hanging up the connection.
 */
package cvosteen.sqltool.tasks;
import cvosteen.sqltool.task.*;
import java.sql.*;

public class CloseConnectionTask extends Task {

	private Connection connection;

	public CloseConnectionTask(Connection connection) {
		this.connection = connection;
	}

	public void run() {
		try {
			connection.close();
		} catch(Exception e) {
			reportError(e);
		} finally {
			reportFinished();
		}
	}
}
