/**
 * A single class to handle a collection of databases as well as
 * their persistence.
 * DOCUMENT ME!
 */
package database;

import java.io.*;
import java.util.*;
import java.sql.*;

public class DatabaseManager extends TreeSet<Database> {
	
	private DatabaseXMLEncoder encoder = new DatabaseXMLEncoder();
	private static HashMap<String, DatabaseManager> managers = new HashMap<String, DatabaseManager>();

	public static DatabaseManager instanceForFile(String filename) {
		if(managers.containsKey(filename)) {
			return managers.get(filename);
		} else {
			DatabaseManager manager = new DatabaseManager(filename);
			managers.put(filename, manager);
			return manager;
		}
	}

	private DatabaseManager(String filename) {
		encoder.setFilename(filename);
	}

	public void setFilename(String filename) {
		encoder.setFilename(filename);
	}

	public String getFilename() {
		return encoder.getFilename();
	}

	public void load() throws IOException {
		clear();
		addAll(encoder.readXMLFile());
	}

	public void save() throws IOException {
		encoder.writeXMLFile(this);
	}

	public void close() {
		managers.remove(encoder.getFilename());
	}

}
	
