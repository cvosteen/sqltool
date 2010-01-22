/**
 * Changes in v2.1:
 * - New interface to listen for new data and/or SQLExceptions
 *   b/c the SQLTableModel now retrieves its data asynchronously.
 */

import java.sql.SQLException;

public interface SQLTableModelListener {

	public void dataReceived();
	public void dataComplete();
	public void errorOccurred(SQLException e);

}
