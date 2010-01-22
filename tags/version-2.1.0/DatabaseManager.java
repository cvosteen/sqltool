/**
 * Changes in v2.1:
 * - Moved data file string to main DBTool class
 * - Saved database info as XML instead of serialized objects
 */
import java.io.*;
import java.util.*;

public class DatabaseManager {

	private String dataFile;
	private List<Database> databases = new ArrayList<Database>();

	public DatabaseManager(String dataFile) throws IOException {
		initialize(dataFile, false);
	}

	public DatabaseManager(String dataFile, boolean newFile) throws IOException {
		initialize(dataFile, newFile);
	}

	private void initialize(String dataFile, boolean newFile) throws IOException {
		this.dataFile = dataFile;

		if(newFile)
			return;

		DatabaseXML dx = new DatabaseXML(dataFile);
		databases = dx.readXMLFile();
	}

	public void save() throws IOException {
		DatabaseXML dx = new DatabaseXML(dataFile);
		dx.writeXMLFile(databases);
	}

	public void saveDatabase(Database database) {
		databases.add(database);
	}

	public void deleteDatabase(Database database) {
		databases.remove(database);
	}

	public List<Database> getAllDatabases() {
		List<Database> copy = new ArrayList<Database>(databases);
		Collections.sort(copy);
		return copy;
	}
}
	
