/**
 * Implements an observer pattern on Task objects.
 * Objects that implement this interface can receive
 * notifications about the status of the observed
 * Task instance.
 */

public interface TaskListener {

	/**
	 * Called when the observed task has finished.
	 */
	public void taskFinished();

	/**
	 * Called when the observed task reports its status.
	 */
	public void taskStatus(Object obj);

	/**
	 * Called when the observed task reports its result.
	 */
	public void taskResult(Object obj);

	/**
	 * Called when the observed task throws an exception.
	 */
	public void taskError(Exception e);

}
