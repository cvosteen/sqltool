/**
 * A single class to handle a collection of databases as well as
 * their persistence.
 * DOCUMENT ME!
 */
package database;

import java.io.*;
import java.util.*;
import java.sql.*;

public class RestrictedDatabaseManager {
	
	private DatabaseManager databaseManager;

	public RestrictedDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public String getFilename() {
		return databaseManager.getFilename();
	}

	public void save() throws IOException {
		databaseManager.save();
	}

}
	
