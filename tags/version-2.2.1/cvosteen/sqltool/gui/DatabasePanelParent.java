/**
 * This interface is an attempt to decouple the DatabasePanel from
 * any potential container it might be in.
 * These methods below would be called by a DatabasePanel which
 * does not have the ability to accomplish the tasks below by
 * itself.
 */
package cvosteen.sqltool.gui;

public interface DatabasePanelParent {

	/**
	 * The calling DatabasePanel has requested that changes it
	 * has made be saved to disk.
	 */
	public void saveRequested(DatabasePanel databasePanel);

	/**
	 * Asks the current DatabasePanel for object that implements
	 * the Printable interface so that it can be printed.
	 */
	public void printRequested(DatabasePanel databasePanel);

}
