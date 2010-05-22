/**
 * A Dialog that allows a user to add/edit/delete a list of
 * Database instances via a DatabaseManager instance.
 * If the user selects one of the Databases and clicks the
 * 'Connect' button, the response from this Dialog will
 * be the Database, instructing the caller to connect
 * to the database.
 */

package cvosteen.sqltool.gui;

import cvosteen.sqltool.database.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class DatabaseManagerDialog extends JDialog implements ResponseGetter<Database> {

	private final DatabaseManager databaseManager;
	private JList list;
	private JPopupMenu popup;
	private Database response = null;

	public DatabaseManagerDialog(Frame owner, DatabaseManager databaseManager) {
		super(owner, "Databases", true);

		this.databaseManager = databaseManager;
		createComponents();
	}
		
	private void createComponents() {
		// We want to hide this object when we click on the 'X'
		// After this dialog has been closed, we will need to retreive the chosen database
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		// Set the layout (GridBag)
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panel.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
	
		// Add the list to take up the whole left side of the dialog
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 3;
		list = new JList();
		list.setListData(databaseManager.toArray());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);

		// Add a popup menu to the list
		popup = new JPopupMenu();
		JMenuItem newMenuItem = new JMenuItem("New...");
		newMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newDatabase();
				}
			});
		JMenuItem editMenuItem = new JMenuItem("Edit...");
		editMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editDatabase();
				}
			});
		JMenuItem delMenuItem = new JMenuItem("Delete...");
		delMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteDatabase();
				}
			});
		popup.add(newMenuItem);
		popup.add(editMenuItem);
		popup.add(delMenuItem);
		list.addMouseListener(new MouseAdapter() {
				// A right click will bring up the context menu
				public void mousePressed(MouseEvent e) {
					if(e.isPopupTrigger())
						popup.show(e.getComponent(), e.getX(), e.getY());
				}

				public void mouseReleased(MouseEvent e) {
					if(e.isPopupTrigger())
						popup.show(e.getComponent(), e.getX(), e.getY());
				}
				// A double click will connect
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						response = (Database) list.getSelectedValue();					
						dispose();
					}	
				}
			});

		JScrollPane scrollPane = new JScrollPane(list);
		gridbag.setConstraints(scrollPane, c);
		panel.add(scrollPane);

		// Two buttons on the upper right side
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		JButton connectButton = new JButton("Connect");
		connectButton.setDefaultCapable(true);
		connectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					response = (Database) list.getSelectedValue();					
					dispose();
				}
			});
		gridbag.setConstraints(connectButton, c);
		panel.add(connectButton);
		JButton cancelButton = new JButton("Cancel");
		// When escape is pressed, activate the Cancel button
		cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "doCancel");
		cancelButton.getActionMap().put("doCancel", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		gridbag.setConstraints(cancelButton, c);
		panel.add(cancelButton);

		getContentPane().add(panel);
		setMinimumSize(new Dimension(350,250));
		pack();
		list.requestFocusInWindow();
		getRootPane().setDefaultButton(connectButton);

		// center the window on the screen
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width-getWidth())/2,
			(screenDim.height-getHeight())/2);

		setVisible(true);
	}
	
	/**
	 * Called when a user requests to create a new database from the
	 * context menu.
	 * A DatabaseDialog is created to capture the user's preferences.
	 */
	private void newDatabase() {
		ResponseGetter<Database> dbDialog = new DatabaseDialog(this);
		Database newDatabase = dbDialog.getResponse();
		if(newDatabase != null) {
			databaseManager.add(newDatabase);
			try {
				databaseManager.save();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(this,
					e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}
		list.setListData(databaseManager.toArray());
	}

	/**
	 * Called when a user requests to edit an existing database from the
	 * context menu.
	 * A DatabaseDialog is created with the Database instance so the
	 * user can edit it.
	 */
	private void editDatabase() {
		Database selection = (Database) list.getSelectedValue();
		if(selection != null) {
			ResponseGetter<Database> dbDialog = new DatabaseDialog(this, selection);
			Database newDatabase = dbDialog.getResponse();
			if(newDatabase != null) {
				selection.setName(newDatabase.getName());
				selection.setDriver(newDatabase.getDriver());
				selection.setConnectionUrl(newDatabase.getConnectionUrl());
				try {
					databaseManager.save();
				} catch(IOException e) {
					JOptionPane.showMessageDialog(this,
						e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		list.setListData(databaseManager.toArray());
	}

	/**
	 * Called when a user requests to delete an existing database from the
	 * context menu.
	 * A JOptionPane will ask them if they are sure!
	 */
	private void deleteDatabase() {
		Database selection = (Database) list.getSelectedValue();
		if(selection != null && JOptionPane.showConfirmDialog(
				list, "Are you sure you want to delete " + selection.getName() + "?",
				"Delete Database", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
				== JOptionPane.YES_OPTION) {
			databaseManager.remove((Database) list.getSelectedValue());
			try {
				databaseManager.save();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(this,
					e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			}
			list.setListData(databaseManager.toArray());
		}
	}

	/**
	 * Called by parent window or other client to get the
	 * user's choice or response from this dialog.
	 */
	public Database getResponse() {
		return response;
	}
}
