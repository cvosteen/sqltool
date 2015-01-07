/**
 * A Table Model that holds Properties and as the
 * underlying model for a JTable
 */

package cvosteen.sqltool.gui.components;

import cvosteen.sqltool.gui.*;
import java.awt.*;
import java.awt.print.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;


class PropertiesTableModel implements TableModel {
	private Vector<Property> properties;
	private Vector<TableModelListener> listeners;

	public PropertiesTableModel() {
		listeners = new Vector<TableModelListener>();
		properties = new Vector<Property>();
	}

	public PropertiesTableModel(Properties properties) {
		listeners = new Vector<TableModelListener>();
		setProperties(properties);
	}

	public void setProperties(Properties properties) {
		this.properties = new Vector<Property>();
		for(String key : new TreeSet<String>(properties.stringPropertyNames())) {
			this.properties.add(new Property(key, properties.getProperty(key)));
		}
		notifyListeners(new TableModelEvent(this));
	}

	public Properties getProperties() {
		Properties result = new Properties();
		for(Property property : properties) {
			result.setProperty(property.getKey(), property.getValue());
		}
		return result;
	}

	/**
	 * Always 2 columns, one for key, one for value
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * Both columns are strings
	 */
	public Class<?> getColumnClass(int columnIndex) {
		return "".getClass();
	}

	public String getColumnName(int columnIndex) {
		if(columnIndex == 0)
			return "Key";
		else if(columnIndex == 1)
			return "Value";
		else
			throw new IllegalArgumentException("Invalid Column Number " + columnIndex);
	}

	/**
	 * Returns the number of properties plus 1 for an extra row
	 */
	public int getRowCount() {
		return properties.size() + 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex >= properties.size()) {
			/* This is the blank "new" row */
			return "";
		} else {
			Property property = properties.get(rowIndex);
			if(columnIndex == 0)
				/* This is a key */
				return property.getKey();
			else if(columnIndex == 1)
				/* This is a value */
				return property.getValue();
			else
				return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		/* Is cell editable?  Hell yeah! */
		return true;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(rowIndex >= properties.size()) {
			/* New record */
			if(!aValue.toString().equals("")) {
				if(columnIndex == 0)
					properties.add(new Property(aValue.toString(), ""));
				else if(columnIndex == 1)
					properties.add(new Property("", aValue.toString()));

				notifyListeners(new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
			}
			
		} else {
			/* Edit existing record */
			if(columnIndex == 0)
				properties.setElementAt(new Property(aValue.toString(), properties.get(rowIndex).getValue()), rowIndex);
			else if(columnIndex == 1)
				properties.setElementAt(new Property(properties.get(rowIndex).getKey(), aValue.toString()), rowIndex);

			/* If the entire row is blank, remove it */
			Property property = properties.get(rowIndex);
			if(property.getKey().equals("") && property.getValue().equals(""))
				removeRow(rowIndex);
			else
				notifyListeners(new TableModelEvent(this, rowIndex, rowIndex, columnIndex));
		}
	}

	public void removeRow(int rowIndex) {
		if(rowIndex < properties.size()) {
			properties.remove(rowIndex);
			notifyListeners(new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
		}
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	private void notifyListeners(TableModelEvent e) {
		for(TableModelListener l: listeners) {
			l.tableChanged(e);
		}
	}
}

