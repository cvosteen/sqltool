import java.io.*;
import java.util.*;
import java.sql.*;

public class ExecuteSqlTask extends Task {

	private Database database = null;
	private String sql = null;

	public ExecuteSqlTask(Database database, String sql) {
		this.database = database;
		this.sql = sql;
	}
	
	public void run() {
		try {
			reportResult(database.executeAdHocSql(sql));
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
