package tasks;
import java.sql.*;
import java.util.*;
import task.*;

public class ResultSetTask extends Task {

	private ResultSet resultSet;

	public ResultSetTask(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public void run() {
		try {
			// Pull column names from ResultSet
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			int colCount = resultSetMetaData.getColumnCount();
			Vector<String> columns = new Vector<String>();
			for(int col = 1; col <= colCount; col++)
				columns.add(resultSetMetaData.getColumnName(col));

			// Report that the columns have been retrieved
			reportStatus(columns);

			// Check for cancellation
			if(isCancelled())
				return;


			// Start pulling rows from ResultSet
			long time = System.currentTimeMillis();
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while(resultSet.next()) {
				Vector<Object> row = new Vector<Object>();
				for(int col = 1; col <= colCount; col++)
					row.add(resultSet.getObject(col));
				data.add(row);

				// Once per second report all rows so far
				if(System.currentTimeMillis() - time >= 1000) {
					// Check for cancellation
					if(isCancelled())
						return;

					reportStatus(data);
					time = System.currentTimeMillis();
					data = new Vector<Vector<Object>>();
				}
			}
		} catch(final SQLException e) {
			reportError(e);
		} finally {
			reportFinished();
		}
	}
}
