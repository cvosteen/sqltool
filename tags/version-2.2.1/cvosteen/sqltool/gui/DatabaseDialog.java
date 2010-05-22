/**
 * A dialog that allows a user to edit the fields of a Database
 * object.
 */

package cvosteen.sqltool.gui;

import cvosteen.sqltool.database.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DatabaseDialog extends JDialog implements ResponseGetter<Database> {

	// Use some sensible defaults for a new Database
	private static final String DEFAULT_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
	private static final String DEFAULT_URL = "jdbc:odbc:";

	private Database database;
	private final JTextField nameField = new JTextField(20);
	private final JTextField driverField = new JTextField(20);
	private final JTextField urlField = new JTextField(20);

	/**
	 * Creates a DatabaseDialog that will return a new
	 * Database instance.
	 */
	public DatabaseDialog(Dialog owner) {
		super(owner, "Database", true);

		// Create this dialog to create a new Database instance
		database = null;
		createComponents();
		// Use a sensible default for new databases
		driverField.setText(DEFAULT_DRIVER);
		urlField.setText(DEFAULT_URL);
		setVisible(true);
	}

	/**
	 * Creates a DatabaseDialog that will edit an existing
	 * Database instance.
	 */
	public DatabaseDialog(Dialog owner, Database theDatabase) {
		super(owner, "Database", true);

		database = theDatabase;
		createComponents();
		nameField.setText(database.getName());
		driverField.setText(database.getDriver());
		urlField.setText(database.getConnectionUrl());
		setVisible(true);
	}

	private void createComponents() {
		// We want to hide this object when we click on the 'X'
		// That way we can retreive the new database instance if necessary
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		// Set the layout (GridBag)
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panel.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
	
		// Name label and text entry
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		JLabel label = new JLabel("Name:");
		gridbag.setConstraints(label, c);
		panel.add(label);
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		nameField.setMaximumSize(new Dimension(2000, nameField.getPreferredSize().height));
		gridbag.setConstraints(nameField, c);
		panel.add(nameField);

		// Driver label and text entry
		c.weightx = 0.0;
		c.gridwidth = 1;
		label = new JLabel("Driver Class:");
		gridbag.setConstraints(label, c);
		panel.add(label);
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		driverField.setMaximumSize(new Dimension(2000, driverField.getPreferredSize().height));
		gridbag.setConstraints(driverField, c);
		panel.add(driverField);

		// URL label and text entry
		c.weightx = 0.0;
		c.gridwidth = 1;
		label = new JLabel("Connection URL:");
		gridbag.setConstraints(label, c);
		panel.add(label);
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		urlField.setMaximumSize(new Dimension(2000, urlField.getPreferredSize().height));
		gridbag.setConstraints(urlField, c);
		panel.add(urlField);

		// Save and Cancel buttons on bottom right
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHEAST;
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveDatabase();
				}
			});
		gridbag.setConstraints(saveButton, c);
		panel.add(saveButton);
		c.gridx = GridBagConstraints.RELATIVE;
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JButton cancelButton = new JButton("Cancel");
		cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "doCancel");
		cancelButton.getActionMap().put("doCancel", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		gridbag.setConstraints(cancelButton, c);
		panel.add(cancelButton);


		getContentPane().add(panel);
		setMinimumSize(new Dimension(300,200));
		pack();
		nameField.requestFocusInWindow();
		getRootPane().setDefaultButton(saveButton);

		// center the window on the screen
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width-getWidth())/2,
			(screenDim.height-getHeight())/2);

	}
	
	/**
	 * Called when the user presses 'Enter' or clicks 'Save'
	 */
	private void saveDatabase() {
		try {
			database = new Database(nameField.getText(), driverField.getText(), urlField.getText());
			setVisible(false);
		} catch(NullPointerException f) {
			// Databases throw NullPointerException if any of the fields are nulls (e.g. User Error)
			JOptionPane.showMessageDialog(this, f.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called by parent window or other client to get the
	 * user's choice or response from this dialog.
	 */
	public Database getResponse() {
		return database;
	}
}
