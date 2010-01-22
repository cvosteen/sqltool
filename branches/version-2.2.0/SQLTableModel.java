/**
 * Changes in v2.1:
 * - Fetches data asynchronously
 */

import javax.swing.table.*;
import java.sql.*;
import java.util.*;
import javax.swing.SwingUtilities;

public class SQLTableModel extends AbstractTableModel {

	Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	Vector<String> columns = new Vector<String>();
	Vector<SQLTableModelListener> listeners = new Vector<SQLTableModelListener>();
	LoadThread loadThread;
	ResultSet rs;

	public SQLTableModel(ResultSet rs) {
		super();
		this.rs = rs;
		loadThread = new LoadThread(rs);
	}

	public ResultSet getResultSet() {
		return rs;
	}

	public void start() {
		loadThread.start();
	}

	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return columns.size();
	}

	public Object getValueAt(int row, int column) {
		return data.get(row).get(column);
	}

	public String getColumnName(int column) {
		return columns.get(column);
	}

	public void addSQLTableModelListener(SQLTableModelListener listener) {
		listeners.add(listener);
	}

	public void removeSQLTableModelListener(SQLTableModelListener listener) {
		listeners.remove(listener);
	}

	private class LoadThread extends Thread {
		private ResultSet rs;
		Vector<Vector<Object>> tempdata = new Vector<Vector<Object>>();

		public LoadThread(ResultSet rs) {
			this.rs = rs;
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
