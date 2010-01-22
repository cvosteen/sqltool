import java.io.*;
import java.util.*;
import java.sql.*;

public class ResultSetTask extends Task {

	private ResultSet rs;
	Vector<Vector<Object>> tempdata = new Vector<Vector<Object>>();

	public ResultSetTask(ResultSet rs) {
		this.rs = rs;

	}
	
	public void run() {
		try {
			result(database.executeAdHocSql(sql));
		} catch(Exception e) {
			error(e);
		} finally {
			finished();
		}
	}

	public void run() {
		try {
			ResultSetMetaData rsmeta = rs.getMetaData();
			int colCount = rsmeta.getColumnCount();
			Vector<String> columns2 = new Vector<String>();
			for(int col = 1; col <= colCount; col++)
				columns2.add(rsmeta.getColumnName(col));
			columns = columns2;

			long time = System.currentTimeMillis();
			while(rs.next()) {
				Vector<Object> row = new Vector<Object>();
				for(int col = 1; col <= colCount; col++)
					row.add(rs.getObject(col));
				data.add(row);

				if(System.currentTimeMillis() - time >= 1000) {
					Vector<Vector<Object>> data2 = new Vector<Vector<Object>>();
					data2.addAll(data);
					data2.addAll(tempdata);
					data = data2;
					tempdata.clear();

					try {
						SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									fireTableStructureChanged();
									for(SQLTableModelListener listener : listeners) {
										listener.dataReceived();
									}
								}
							});
					} catch(Exception e) { }

					time = System.currentTimeMillis();
				}
			}
		} catch(final SQLException e) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							for(SQLTableModelListener listener : listeners) {
								listener.errorOccurred(e);
							}
						}
					});
			} catch(Exception f) { }
		}

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						for(SQLTableModelListener listener : listeners) {
							listener.dataComplete();
						}
					}
				});
		} catch(Exception f) { }
	}
}

}
