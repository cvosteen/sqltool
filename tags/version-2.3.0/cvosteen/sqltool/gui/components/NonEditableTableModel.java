/**
 * NonEditableTableModel is a subclass of DefaultTableModel
 * which always returns false for isCellEditable.
 */

package cvosteen.sqltool.gui.components;

import javax.swing.*;
import javax.swing.table.*;

public class NonEditableTableModel extends DefaultTableModel {

	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
	
