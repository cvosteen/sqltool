package database.tasks;
import task.*;
import java.sql.*;

public class ResultSetTask extends Task {

	private ResultSet resultSet;

	public ResultSetTask(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public void run() {
		try {
		} catch(Exception e) {
			reportError(e);
		} finally {
			reportFinished();
		}
	}
}
