import java.io.*;
import java.util.*;
import java.sql.*;

public class QueryTask extends Task {

	private Database database = null;
	private String sql = null;

	public QueryTask(Database database, String sql) {
		this.database = database;
		this.sql = sql;
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
}
