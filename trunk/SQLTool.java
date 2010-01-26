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
			// TODO: Make a more user friendly error message
			// or preferably NO error message if there is no xml file
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
