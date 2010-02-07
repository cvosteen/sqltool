package cvosteen.sqltool.gui.syntax;

import java.awt.Font;
import javax.swing.text.*;
import java.util.*;
import java.util.regex.*;

public class SyntaxHighlightedDocument extends DefaultStyledDocument {

	private Syntax syntax = null;
	private ColorScheme colorScheme = null;
	private SimpleAttributeSet baseAttributeSet = new SimpleAttributeSet();

	public SyntaxHighlightedDocument() {
		StyleConstants.setFontFamily(baseAttributeSet, "Courier");
		StyleConstants.setFontSize(baseAttributeSet, 10);
	}

	public void setSyntax(Syntax syntax) {
		this.syntax = syntax;
	}

	public Syntax getSyntax() {
		return syntax;
	}

	public void setColorScheme(ColorScheme colorScheme) {
		this.colorScheme = colorScheme;
	}

	public ColorScheme getColorScheme() {
		return colorScheme;
	}

	public void setFontFamily(String fontFamily) {
		StyleConstants.setFontFamily(baseAttributeSet, fontFamily);
	}

	public String getFontFamily() {
		return StyleConstants.getFontFamily(baseAttributeSet);
	}
	
	public void setFontSize(int size) {
		StyleConstants.setFontSize(baseAttributeSet, size);
	}

	public int getFontSize() {
		return StyleConstants.getFontSize(baseAttributeSet);
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offs, str, a);
		rehighlight();
	}

	public void remove(int offs, int len) throws BadLocationException {
		super.remove(offs, len);
		rehighlight();
	}

	private void rehighlight() throws BadLocationException {
		// Clear all styles
		setCharacterAttributes(0, getLength(), baseAttributeSet, true);

		// If we have syntax and a color scheme...
		if(syntax != null && colorScheme != null) {
			// Let's highlight some syntax!
			Map<Pattern, String> patterns = syntax.getSyntax();
			// Get the entire text of this document as a string for the Regexes to work on
			String text = getText(0, getLength());
			for(Pattern pattern : patterns.keySet()) {
				// Get the matching AttributeSet from the ColorScheme object
				AttributeSet as = colorScheme.attributesForType(patterns.get(pattern));
				// Make sure the color scheme has a style for this syntax type
				if(as != null) {
					// Apply attributes to each match of the Regex
					Matcher matcher = pattern.matcher(text);
					while(matcher.find()) {
						setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), as, false);
					}
				}
			}
		}
	}

}
