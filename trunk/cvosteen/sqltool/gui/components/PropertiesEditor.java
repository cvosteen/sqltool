/**
 * PropertiesEditor is a JScrollPane that contains a JTable.
 * The JTable is editable and allows for addition, deletion, and
 * editing of existing rows via a right-click context menu.
 * 
 */

package cvosteen.sqltool.gui.components;

import cvosteen.sqltool.gui.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class PropertiesEditor extends JPanel {

	PropertiesTableModel model = new PropertiesTableModel();
	JTable table;
	
	public PropertiesEditor() {
		createComponents();
	}

	public PropertiesEditor(Properties properties) {
		model = new PropertiesTableModel(properties);
		createComponents();
	}

	public Properties getProperties() {
		return model.getProperties();
	}

	public void setProperties(Properties properties) {
		model.setProperties(properties);
	}

	private void createComponents() {
		// Set the layout (GridBag)
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		setLayout(gridbag);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,0);

	
		// Set up the Table
		table = new JTable(model);
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		// Set the Table's Context Menu
		JPopupMenu popup = new JPopupMenu();
		JMenuItem delMenuItem = new JMenuItem("Delete...");
		delMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteRow();
				}
			});
		popup.add(delMenuItem);
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

		// Place the table in a JScrollPane to make the JTable scrollable
		JScrollPane tableScroll = new JScrollPane(table);	
		gridbag.setConstraints(tableScroll, c);
		add(tableScroll);
	}
	
	/**
	 * Called when a user requests to delete a row from the
	 * context menu.
	 * A JOptionPane will ask them if they are sure!
	 */
	private void deleteRow() {
		int row = table.getSelectedRow();
		if(row != -1) {
			String name = model.getValueAt(row, 0) + "->" + model.getValueAt(row, 1);
			if(JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + name + "?",
					"Delete Row", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
					== JOptionPane.YES_OPTION) {
				model.removeRow(row);
			}
		}
	}
}

