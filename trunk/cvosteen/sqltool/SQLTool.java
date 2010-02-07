package cvosteen.sqltool;

import cvosteen.sqltool.database.*;
import cvosteen.sqltool.gui.*;
import cvosteen.sqltool.memory.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

public class SQLTool {

	public static final String DATA_FILE = "database.xml";
	public static final String APP_NAME = "SQLTool";
	public static final String APP_VERSION = "2.2";

	public static void main(String[] args) {
		// Use native L&F if possible
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Set memory monitor
		LowMemoryMonitor monitor = LowMemoryMonitor.getInstance();
		monitor.setMemoryThreshold(10000000);
		
		// Create the application
		DatabaseManager databaseManager = new DatabaseManager(DATA_FILE);

		try {
			// If the file is formatted incorrectly, or nonexistent this may fail
			databaseManager.load();

		} catch(FileNotFoundException e) {
			// If there is no file, it is likely this is the first time the program
			// is being run.  In this case, ignore the error.

		} catch(IOException e) {
			// Some unknown kind of error occurred.  Alert the user, then
			// proceed with an empty Database List
			// TODO: Allow the user to cancel out?
			JOptionPane.showMessageDialog(null,
				e.getMessage(), "Warning",
				JOptionPane.WARNING_MESSAGE);
		}

		new DatabaseManagerFrame(databaseManager, APP_NAME, APP_VERSION);
	}
}
