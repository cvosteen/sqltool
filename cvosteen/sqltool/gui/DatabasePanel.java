/**
 * This interface is an attempt to decouple the DatabasePanel from
 * any potential container it might be in.
 *
 * This interface also acts as a sort of documentation of exactly
 * the ways in which the DatabasePanel might interact with its
 * container.
 */
package cvosteen.sqltool.gui;
import java.awt.print.*;

public interface DatabasePanel {

	/**
	 * Asks the current DatabasePanel for object that implements
	 * the Printable interface so that it can be printed.
	 */
	public Printable getPrintableComponent();

	/**
	 * Tells the selected DatabasePanel to shutdown().
	 */
	public void shutdown();

	/**
	 * Tells the selected DatabasePanel to commit().
	 */
	public void commit();

	/**
	 * Tells the selected DatabasePanel to rollback().
	 */
	public void rollback();

}
