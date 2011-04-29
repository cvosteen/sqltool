/**
 * Interface to be implemented by a concrete
 * ColorScheme.  The color scheme should associate
 * attributes with certain syntax types.
 */

package cvosteen.sqltool.gui.syntax;

import javax.swing.text.AttributeSet;

public interface ColorScheme {
	public AttributeSet attributesForType(String type);
}

