package gui;

import database.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class DatabaseManagerFrame extends JFrame {

	private DatabaseManager databaseManager;
	private JTabbedPane tabbedPane;
	private PageFormat pageFormat = new PageFormat();

	public DatabaseManagerFrame() {
		super(SQLTool.APP_NAME + " v" + SQLTool.APP_VERSION);

		databaseManager = DatabaseManager.instanceForFile(SQLTool.DATA_FILE);
		
		setMinimumSize(new Dimension(600,400));

		// Don't do anything when we click on the 'X'
		// We want to be able to save settings before closing!!!!
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


		// Add a window close listener to save our settings before quitting
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					shutdown();
				}
			});

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem printItem = new JMenuItem("Print...");
		printItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					printTable();
				}
			});
		fileMenu.add(printItem);
		JMenuItem pageSetupItem = new JMenuItem("Page Setup...");
		pageSetupItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pageSetup();
				}
			});
		fileMenu.add(pageSetupItem);
		fileMenu.addSeparator();
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shutdown();
				}
			});
		fileMenu.add(exitItem);
		JMenu databaseMenu = new JMenu("Database");
		menuBar.add(databaseMenu);
		JMenuItem connectItem = new JMenuItem("Connect...");
		connectItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					connect();
				}
			});
		databaseMenu.add(connectItem);
		JMenuItem disconnectItem = new JMenuItem("Disconnect");
		disconnectItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					disconnect();
				}
			});
		databaseMenu.add(disconnectItem);
		JMenu transactionMenu = new JMenu("Transaction");
		menuBar.add(transactionMenu);
		JMenuItem commitItem = new JMenuItem("Commit");
		commitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					commit();
				}
			});
		transactionMenu.add(commitItem);
		JMenuItem rollbackItem = new JMenuItem("Rollback");
		rollbackItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rollback();
				}
			});
		transactionMenu.add(rollbackItem);
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new AboutDialog(null);
				}
			});
		helpMenu.add(aboutItem);

		setJMenuBar(menuBar);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,1));
		tabbedPane = new JTabbedPane();
		panel.add(tabbedPane);

		getContentPane().add(panel);
		pack();

		// center the window on the screen
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width-getWidth())/2,
			(screenDim.height-getHeight())/2);

		setVisible(true);
	}

	public PageFormat getPageFormat() {
		return pageFormat;
	}

	private void printTable() {
		int index = tabbedPane.getSelectedIndex();
		if(index != -1)	{
			((DatabasePanel) tabbedPane.getComponentAt(index)).printTable();
		}
	}

	private void pageSetup() {
		PrinterJob job = PrinterJob.getPrinterJob();
		pageFormat = job.pageDialog(pageFormat);
	}

	private void connect() {
		DatabaseManagerDialog dialog = new DatabaseManagerDialog(this);
		Database database = dialog.getResponse();
		if(database != null) {
			try {
				JComponent newPanel = new DatabasePanel(this, database);
				tabbedPane.addTab(database.getName(), newPanel);
				tabbedPane.setSelectedComponent(newPanel);
			} catch(SQLException f) {
				f.printStackTrace();
				JOptionPane.showMessageDialog(this,
					f.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			} catch(ClassNotFoundException f) {
				f.printStackTrace();
				JOptionPane.showMessageDialog(this,
					f.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void disconnect() {
		int index = tabbedPane.getSelectedIndex();
		if(index != -1)	{
			((DatabasePanel) tabbedPane.getComponentAt(index)).shutdown();
			tabbedPane.remove(index);
		}
	}

	private void commit() {
		DatabasePanel activePanel = (DatabasePanel) tabbedPane.getSelectedComponent();
		if(activePanel != null)
			activePanel.commit();
	}

	private void rollback() {
		DatabasePanel activePanel = (DatabasePanel) tabbedPane.getSelectedComponent();
		if(activePanel != null)
			activePanel.rollback();
	}

	private void shutdown() {
		for(int i = 0; i < tabbedPane.getTabCount(); i++)
			((DatabasePanel) tabbedPane.getComponentAt(i)).shutdown();
		dispose();
	}

}
