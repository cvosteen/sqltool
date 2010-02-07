package gui.syntax;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.text.*;

public class StandardColorScheme implements ColorScheme {

	private Hashtable<String, AttributeSet> types = new Hashtable<String, AttributeSet>();

	public StandardColorScheme() {
		SimpleAttributeSet as = new SimpleAttributeSet();
		StyleConstants.setBold(as, true);
		StyleConstants.setForeground(as, new Color(0.5f, 0.25f, 0.0f)); // Brown
		types.put("statement", as);

		as = new SimpleAttributeSet();
		StyleConstants.setBold(as, true);
		StyleConstants.setForeground(as, new Color(0.0f, 0.5f, 0.5f)); // Dark Cyan
		types.put("operator", as);

		as = new SimpleAttributeSet();
		StyleConstants.setBold(as, true);
		StyleConstants.setForeground(as, new Color(0.5f, 0.5f, 0.0f)); // Dark Yellow
		types.put("function", as);

		as = new SimpleAttributeSet();
		StyleConstants.setBold(as, true);
		StyleConstants.setForeground(as, new Color(0.25f, 0.0f, 0.5f)); // Indigo
		types.put("keyword", as);

		as = new SimpleAttributeSet();
		StyleConstants.setBold(as, true);
		StyleConstants.setForeground(as, new Color(0.0f, 0.5f, 0.0f)); // Green
		types.put("type", as);

		as = new SimpleAttributeSet();
		StyleConstants.setForeground(as, new Color(0.5f, 0.0f, 1.0f)); // Light Indigo
		types.put("variable", as);

		as = new SimpleAttributeSet();
		StyleConstants.setForeground(as, new Color(0.0f, 0.0f, 1.0f)); // Blue
		types.put("comment", as);

		as = new SimpleAttributeSet();
		StyleConstants.setForeground(as, new Color(1.0f, 0.0f, 1.0f)); // Magenta
		types.put("number", as);
		
		as = new SimpleAttributeSet();
		StyleConstants.setForeground(as, new Color(1.0f, 0.0f, 0.0f)); // Bright Red
		types.put("string", as);

		as = new SimpleAttributeSet();
		StyleConstants.setBold(as, true);
		StyleConstants.setForeground(as, new Color(0.0f, 0.0f, 1.0f));
		StyleConstants.setBackground(as, new Color(1.0f, 1.0f, 0.0f));
		types.put("todo", as);

		as = new SimpleAttributeSet();
		StyleConstants.setBold(as, true);
		StyleConstants.setForeground(as, new Color(1.0f, 1.0f, 1.0f));
		StyleConstants.setBackground(as, new Color(1.0f, 0.0f, 0.0f));
		types.put("error", as);
	}

	public AttributeSet attributesForType(String type) {
		return types.get(type);
	}

}

