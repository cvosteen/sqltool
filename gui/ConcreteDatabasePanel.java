package gui;

import database.*;
import task.*;
import tasks.*;
import gui.components.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

public class ConcreteDatabasePanel extends JSplitPane implements DatabasePanel {

	private DatabasePanelParent parent;
	private Database database;
	private Connection connection;
	private JTree tree;
	private JComboBox queryCombo;
	private JTextArea sqlField;
	protected JTable table;
	private JPopupMenu popup;
	protected JButton runButton;
	protected JButton saveButton;
	protected JLabel queryStatusLabel;

	public ConcreteDatabasePanel(DatabasePanelParent parent, Database database) throws SQLException, ClassNotFoundException {
		super(HORIZONTAL_SPLIT);

		// This one CAN be null
		this.parent = parent;

		if(database == null)
			throw new NullPointerException("Cannot open database, none specified!");
		this.database = database;
		
		connection = database.connect();

		createComponents();
	}
	
	private void createComponents() {
		// Put the tree on the left side of the divider.
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(database.getName());
		dbNode.add(new DefaultMutableTreeNode("Placeholder"));
		rootNode.add(dbNode);
		tree = new JTree(rootNode);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {
				public void treeWillCollapse(TreeExpansionEvent event) {}
				public void treeWillExpand(TreeExpansionEvent event) {
					if(event.getPath().getPathCount() == 2)
						expandDatabaseTree((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
					else if(event.getPath().getPathCount() == 3)
						expandTableTree((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
				}
			});
		JScrollPane treeScroll = new JScrollPane(tree);
		setLeftComponent(treeScroll);
		setDividerLocation(0.25);
		setResizeWeight(0.25);

		// Split right side into top and bottom panels
		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		JSplitPane panel = new JSplitPane(VERTICAL_SPLIT);
		panel.setTopComponent(topPanel);
		panel.setBottomComponent(bottomPanel);

		// Layout the top panel
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		topPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);

		// Query ComboBox
		c.weighty = 0.0;
		c.gridheight = 1;
		JLabel label = new JLabel("Query:");
		gridbag.setConstraints(label, c);
		topPanel.add(label);

		c.weightx = 0.5;
		queryCombo = new JComboBox(database.getAllQueries().toArray());
		queryCombo.setSelectedIndex(-1);
		queryCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO: Ask about unsaved changes?
					sqlField.setText(database.getQuerySql((String) queryCombo.getSelectedItem()));
					sqlField.setCaretPosition(0);
					saveButton.setEnabled(false);
				}
			});
		gridbag.setConstraints(queryCombo, c);
		topPanel.add(queryCombo);

		// New query button
		c.weightx = 0.0;
		JButton newButton = new JButton("New");
		newButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newQuery("");
					}
			});
		gridbag.setConstraints(newButton, c);
		topPanel.add(newButton);
		
		// Delete query button
		c.weightx = 0.1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						deleteQuery();
					}
			});
		gridbag.setConstraints(deleteButton, c);
		topPanel.add(deleteButton);

		// Run query button
		c.gridx = 6;
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		runButton = new JButton("Run Query");
		runButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						runQuery();
					}
			});
		gridbag.setConstraints(runButton, c);
		topPanel.add(runButton);

		// SQL Editor
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		label = new JLabel("SQL:");
		gridbag.setConstraints(label, c);
		topPanel.add(label);
		
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 4;
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		sqlField = new JTextArea(6, 1);
		sqlField.setText(database.getQuerySql((String) queryCombo.getSelectedItem()));
		sqlField.getDocument().addDocumentListener(new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						saveButton.setEnabled(true);
					}

					public void insertUpdate(DocumentEvent e) {
						saveButton.setEnabled(true);
					}

					public void removeUpdate(DocumentEvent e) {
						saveButton.setEnabled(true);
					}
			});
		JScrollPane sqlScroll = new JScrollPane(sqlField);
		sqlScroll.setMinimumSize(new Dimension(sqlScroll.getPreferredSize()));
		gridbag.setConstraints(sqlScroll, c);
		topPanel.add(sqlScroll);

		// Save query button
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		saveButton = new JButton("Save Query");
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveQuery();
					}
			});
		gridbag.setConstraints(saveButton, c);
		topPanel.add(saveButton);

		// Status label
		c.gridx = 5;
		c.gridy = 2;
		c.anchor = GridBagConstraints.SOUTHWEST;
		queryStatusLabel = new JLabel("Ready");
		gridbag.setConstraints(queryStatusLabel, c);
		topPanel.add(queryStatusLabel);


		// Lay out the bottom panel
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		bottomPanel.setLayout(gridbag);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,0);

		// Results table
		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		popup = new JPopupMenu();
		JMenuItem printMenuItem = new JMenuItem("Print...");
		printMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					printTable();
				}
			});
		popup.add(printMenuItem);
		table.addMouseListener(new MouseAdapter() {
				// A right click will bring up the context menu
				public void mousePressed(MouseEvent e) {
					if(e.isPopupTrigger())
						popup.show(e.getComponent(), e.getX(), e.getY());
				}

				public void mouseReleased(MouseEvent e) {
					if(e.isPopupTrigger())
						popup.show(e.getComponent(), e.getX(), e.getY());
				}
			});
		table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK), "doPrint");
		table.getActionMap().put("doPrint", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					printTable();
				}
			});
		JScrollPane tableScroll = new JScrollPane(table);	
		gridbag.setConstraints(tableScroll, c);
		bottomPanel.add(tableScroll);

		setRightComponent(panel);
	}

	private void newQuery(String sql) {
		boolean okay = true;
		String newQuery = JOptionPane.showInputDialog(this,
				"Enter Query Name:", "New Query", JOptionPane.PLAIN_MESSAGE);
		// If its already existing, ask before replacing it
		if(database.getQuerySql(newQuery) != null)
			okay = JOptionPane.showConfirmDialog(this,
				newQuery + " already exists.  Overwrite it?", "New Query", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE) ==	JOptionPane.YES_OPTION;

		if(newQuery != null && okay) {
				database.saveQuery(newQuery, sql);
				queryCombo.setModel(new DefaultComboBoxModel(database.getAllQueries().toArray()));
				sqlField.setText(sql);
				queryCombo.setSelectedItem(newQuery);
				parent.saveRequested(this);
				saveButton.setEnabled(false);
		}
	}

	private void deleteQuery() {
		String delQuery = (String) queryCombo.getSelectedItem();
		if(delQuery != null && JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " +
				       	delQuery + "?", "Delete Query", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) ==	JOptionPane.YES_OPTION) {
			database.deleteQuery(delQuery);
			parent.saveRequested(this);
			queryCombo.setModel(new DefaultComboBoxModel(database.getAllQueries().toArray()));
			sqlField.setText(database.getQuerySql((String) queryCombo.getSelectedItem()));
			saveButton.setEnabled(false);
		}
	}

	private void runQuery() {
		runButton.setEnabled(false);
		queryStatusLabel.setText("Working...");
		try {
			Task queryTask = new QueryTask(connection, sqlField.getText());
			queryTask.addTaskListener(new QueryTaskListener());
			queryTask.start();
		} catch(SQLException e) {
			queryStatusLabel.setText("Error");
			JOptionPane.showMessageDialog(this,
				e.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
			runButton.setEnabled(true);
		}
	}

	private void printTable() {
		parent.printRequested(this);
	}

	protected void adjustTableColumns(JTable theTable) {
		TableModel model = theTable.getModel();
		TableCellRenderer headerRenderer =
				theTable.getTableHeader().getDefaultRenderer();

		for (int col = 0; col < theTable.getColumnModel().getColumnCount(); col++) {
			TableColumn column = theTable.getColumnModel().getColumn(col);

			// Find the header size
			Component comp = headerRenderer.getTableCellRendererComponent(
					null, column.getHeaderValue(),
					false, false, 0, 0);
			int headerWidth = comp.getPreferredSize().width;

			// Find the longest cell
			int sampleSize = model.getRowCount();
			String longString = "";
			for(int row = 0; row < sampleSize; row++) {
				Object sampleObject = model.getValueAt(row, col);
				String sampleString = null;
				if(sampleObject != null)
					sampleString = sampleObject.toString();
				if(sampleString != null && sampleString.length() > longString.length())
					longString = sampleString;
			}
				
			// Find the cell size
			comp = theTable.getDefaultRenderer(String.class).
					getTableCellRendererComponent(
					theTable, longString,
					false, false, 0, col);
			int cellWidth = comp.getPreferredSize().width;

			column.setPreferredWidth(Math.max(headerWidth, cellWidth) + 10);
		}
	}

	private void saveQuery() {
		String saveQuery = (String) queryCombo.getSelectedItem();
		if(saveQuery == null) {
			// must prompt for name if there are no queries selected
			newQuery(sqlField.getText());
		} else {
			database.saveQuery(saveQuery, sqlField.getText());
			parent.saveRequested(this);
			saveButton.setEnabled(false);
		}
	}

	private void expandDatabaseTree(DefaultMutableTreeNode dbNode) {
		try {
			dbNode.removeAllChildren();
			java.util.List<String> tables = database.getTables(connection);
			for(String table : tables) {
				DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
				tableNode.add(new DefaultMutableTreeNode("Placeholder"));
				dbNode.add(tableNode);
			}
		} catch(SQLException e) {
			JOptionPane.showMessageDialog(this,
				e.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	private void expandTableTree(DefaultMutableTreeNode tableNode) {
		try {
			tableNode.removeAllChildren();
			java.util.List<String> columns = database.getColumns(connection, (String)tableNode.getUserObject());
			for(String column : columns) {
				DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode(column);
				tableNode.add(columnNode);
			}
		} catch(SQLException e) {
			JOptionPane.showMessageDialog(this,
				e.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}


	public Printable getPrintableComponent() {
		return new JTablePrintable(table);
	}

	public void shutdown() {
		// TODO: Ask to save any unsaved changed to text field!
		try {
			connection.close();
		} catch(Exception f) {
			// Ignore all exceptions
			// Nothing much we can do here anyway
		}
	}

	public void commit() {
		try {
			connection.commit();
		} catch(SQLException f) {
			JOptionPane.showMessageDialog(this,
				f.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		} catch(UnsupportedOperationException f) {
			JOptionPane.showMessageDialog(this,
				f.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	public void rollback() {
		try {
			connection.rollback();
		} catch(SQLException f) {
			JOptionPane.showMessageDialog(this,
				f.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		} catch(UnsupportedOperationException f) {
			JOptionPane.showMessageDialog(this,
				f.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	private class QueryTaskListener implements TaskListener {
		public void taskFinished() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						adjustTableColumns(table);
						runButton.setEnabled(true);
					}
				});
			} catch(Exception f) { }
		}

		public void taskStatus(final Object obj) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						if(obj instanceof Vector && ((Vector) obj).size() > 0) {
							Vector vector = (Vector) obj;
							if(vector.get(0) instanceof String) {
								// We have column names
								for(Object column : vector) {
									model.addColumn(column);
								}
							} else {
								// We have rows of data
								for(Object row : vector) {
									if(row instanceof Vector) {
										model.addRow((Vector) row);
									}
								}
							}
						}

					}
				});
			} catch(Exception f) { }
		}

		public void taskResult(final Object obj) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						queryStatusLabel.setText("Ready");
						JOptionPane.showMessageDialog(null,
							"" + obj + " rows updated.", "Update Executed",
							JOptionPane.INFORMATION_MESSAGE);
					}
				});
			} catch(Exception f) { }
		}

		public void taskError(final Exception e) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						queryStatusLabel.setText("Error");
						JOptionPane.showMessageDialog(null,
							e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
					}
				});
			} catch(Exception f) { }
		}
	}
}

