/**
 * Changes in v2.1:
 * - put data file string in this class from database manager class
 * - put version name in this class from hard coded in DatabaseManagerFrame
 * - Catch potential errors when reading database file, warn user if needed
 *   and proceed as if there was no file
 */

import java.io.IOException;
import javax.swing.*;

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

		// Create the application
		DatabaseManager dbman = null;
		try {
			// If the file is formatted incorrectly, or nonexistent this may fail
			dbman = new DatabaseManager(DATA_FILE);
		} catch(IOException e) {
			// On failure, we'll make a new DatabaseManager
			JOptionPane.showMessageDialog(null,
				e.getMessage(), "Warning",
				JOptionPane.WARNING_MESSAGE);
			try {
				dbman = new DatabaseManager(DATA_FILE, true);
			} catch(IOException f) {
				// This shouldn't happen, the above method is a failsafe
				JOptionPane.showMessageDialog(null,
					f.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		new DatabaseManagerFrame(dbman, APP_NAME, APP_VERSION);
	}
}
