/**
 * PropertiesEditor is a JScrollPane that contains a JTable.
 * The JTable is editable and allows for addition, deletion, and
 * editing of existing rows via a right-click context menu.
 * 
 */

package cvosteen.sqltool.gui.components;

/**
 * Represents a single Key-Value property
 */
public class Property {
	private String key;
	private String value;

	public Property(String key, String value) {
		setKey(key);
		setValue(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

