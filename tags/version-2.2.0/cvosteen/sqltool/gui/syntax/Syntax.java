/**
 * An interface which should provide all
 * of the syntax keywords and their types.
 */

package cvosteen.sqltool.gui.syntax;

import java.util.Map;
import java.util.regex.Pattern;

public interface Syntax {
	public Map<Pattern, String> getSyntax();
}

