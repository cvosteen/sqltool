import database.*;
import gui.*;
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

		// Create the application
		DatabaseManager databaseManager = DatabaseManager.instanceForFile(DATA_FILE);

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

		new DatabaseManagerFrame();
	}
}
