package gui;

import database.*;
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

public class DatabasePanel extends JSplitPane {

	protected DatabaseManagerFrame databaseManagerFrame;
	private DatabaseManager databaseManager;
	private Set<Database> databases;
	private Database database;
	private JTree tree;
	private JComboBox queryCombo;
	private JTextArea sqlField;
	protected JTable table;
	private JPopupMenu popup;
	protected JButton runButton;
	protected JButton saveButton;
	protected JLabel queryStatusLabel;

	public DatabasePanel(DatabaseManagerFrame dmf, Set<Database> databases, Database theDatabase) throws SQLException, ClassNotFoundException {
		super(HORIZONTAL_SPLIT);

		databaseManagerFrame = dmf;
		this.databases = databases;

		if(theDatabase == null)
			throw new NullPointerException("Cannot open database, none specified!");
		database = theDatabase;
		
		database.connect();

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
		setDividerLocation(0.3);
		setResizeWeight(0.3);

		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		JSplitPane panel = new JSplitPane(VERTICAL_SPLIT);
		panel.setTopComponent(topPanel);
		panel.setBottomComponent(bottomPanel);

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		topPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);

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

		c.weightx = 0.0;
		JButton newButton = new JButton("New");
		newButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newQuery("");
					}
			});
		gridbag.setConstraints(newButton, c);
		topPanel.add(newButton);
		
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

		c.gridx = 5;
		c.gridy = 2;
		c.anchor = GridBagConstraints.SOUTHWEST;
		queryStatusLabel = new JLabel("Ready");
		gridbag.setConstraints(queryStatusLabel, c);
		topPanel.add(queryStatusLabel);


		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		bottomPanel.setLayout(gridbag);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,0);

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
				try {
					database.saveQuery(newQuery, sql);
					queryCombo.setModel(new DefaultComboBoxModel(database.getAllQueries().toArray()));
					sqlField.setText(sql);
					queryCombo.setSelectedItem(newQuery);
					databaseManager.save();
					saveButton.setEnabled(false);
				} catch(IOException e) {
					JOptionPane.showMessageDialog(this,
						e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				}

		}
	}

	private void deleteQuery() {
		String delQuery = (String) queryCombo.getSelectedItem();
		if(delQuery != null && JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " +
				       	delQuery + "?", "Delete Query", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) ==	JOptionPane.YES_OPTION) {
			try {
				database.deleteQuery(delQuery);
				databaseManager.save();
				queryCombo.setModel(new DefaultComboBoxModel(database.getAllQueries().toArray()));
				sqlField.setText(database.getQuerySql((String) queryCombo.getSelectedItem()));
				saveButton.setEnabled(false);
			} catch(IOException e) {
				JOptionPane.showMessageDialog(this,
					e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void runQuery() {
		runButton.setEnabled(false);
		queryStatusLabel.setText("Working...");
		QueryThread qt = database.executeThreadedSql(sqlField.getText());
		qt.addQueryListener(new DatabasePanelQueryListener(qt, this));
		qt.start();
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
			try {
				database.saveQuery(saveQuery, sqlField.getText());
				databaseManager.save();
				saveButton.setEnabled(false);
			} catch(IOException e) {
				JOptionPane.showMessageDialog(this,
					e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void printTable() {
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pf = databaseManagerFrame.getPageFormat();
		job.setPrintable(new JTablePrintable(table), pf);
		boolean ok = job.printDialog();
		if(ok) {
			try {
				job.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(this,
					e.getMessage(), "Print Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void commit() {
		try {
			database.commit();
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
			database.rollback();
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

	public void shutdown() {
		// TODO: Ask to save any unsaved changed to text field!
		try {
			database.disconnect();
		} catch(Exception f) {
			// Ignore all exceptions
			// Nothing much we can do here anyway
		}
	}

	public void expandDatabaseTree(DefaultMutableTreeNode dbNode) {
		try {
			dbNode.removeAllChildren();
			java.util.List<String> tables = database.getTables();
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

	public void expandTableTree(DefaultMutableTreeNode tableNode) {
		try {
			tableNode.removeAllChildren();
			java.util.List<String> columns = database.getColumns((String)tableNode.getUserObject());
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
}




class DatabasePanelQueryListener implements QueryListener {

	QueryThread qt;
	DatabasePanel parent;

	public DatabasePanelQueryListener(QueryThread qt, DatabasePanel parent) {
		this.qt = qt;
		this.parent = parent;
	}

	// These methods are called in a non Event Handling Thread
	public void queryFailed() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(
							parent.databaseManagerFrame,
							qt.getError().getMessage(), 
							"Error",
							JOptionPane.ERROR_MESSAGE);
						parent.queryStatusLabel.setText("Error");
						parent.runButton.setEnabled(true);
					}
				});
		} catch(Exception f) { }
	}

	public void queryCompleted() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						parent.queryStatusLabel.setText("0 records");
						final Object result = qt.getResult();
						if(result instanceof ResultSet) {
							SQLTableModel tableModel = new SQLTableModel((ResultSet)result);
							parent.table.setModel(tableModel);
							parent.adjustTableColumns(parent.table);
							tableModel.addSQLTableModelListener(
								new DatabasePanelSQLTableModelListener(parent, tableModel));
							tableModel.start();
						} else if(result instanceof Integer) {
							JOptionPane.showMessageDialog(parent.databaseManagerFrame,
								"" + result + " rows updated.", "Update Executed",
								JOptionPane.INFORMATION_MESSAGE);
							parent.queryStatusLabel.setText("Ready");
							parent.runButton.setEnabled(true);
						}
					}
				});
		} catch(Exception f) { }
	}
}

class DatabasePanelSQLTableModelListener implements SQLTableModelListener {

	private DatabasePanel parent;
	private SQLTableModel tableModel;

	public DatabasePanelSQLTableModelListener(DatabasePanel parent, SQLTableModel tableModel) {
		this.parent = parent;
		this.tableModel = tableModel;
	}

	public void dataReceived() {
		int recs = tableModel.getRowCount();
		if(recs == 1)
			parent.queryStatusLabel.setText("" + recs + " record");
		else
			parent.queryStatusLabel.setText("" + recs + " records");
	}

	public void dataComplete() {
		int recs = tableModel.getRowCount();
		if(recs == 1)
			parent.queryStatusLabel.setText("" + recs + " record");
		else
			parent.queryStatusLabel.setText("" + recs + " records");
		tableModel.fireTableStructureChanged();
		parent.adjustTableColumns(parent.table);
		try { tableModel.getResultSet().close(); } catch(SQLException f) { }
		parent.runButton.setEnabled(true);
	}

	public void errorOccurred(SQLException e) {
		JOptionPane.showMessageDialog(parent.databaseManagerFrame,
			e.getMessage(), "Error",
			JOptionPane.ERROR_MESSAGE);
		parent.adjustTableColumns(parent.table);
		try { tableModel.getResultSet().close(); } catch(SQLException f) { }
		if(parent.queryStatusLabel.getText() == "Working...") {
			parent.queryStatusLabel.setText("Error");
		} else {
			int recs = tableModel.getRowCount();
			if(recs == 1)
				parent.queryStatusLabel.setText("" + recs + " record");
			else
				parent.queryStatusLabel.setText("" + recs + " records");
		}
		parent.runButton.setEnabled(true);
	}
}
