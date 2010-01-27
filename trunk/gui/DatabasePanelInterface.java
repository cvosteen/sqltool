package gui;

public interface DatabasePanel {

	/**
	 * Asks the current DatabasePanel to print its table using the
	 * current PageFormat
	 */
	public void printTable(PageFormat pageFormat);

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
