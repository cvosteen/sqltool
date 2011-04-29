/**
 * Task: Subclass of Thread.
 * Similar to a .NET class for long running threaded processes.
 * The Task can fire events for observers to catch.
 * Tasks can also be cancelled.
 */

package cvosteen.sqltool.task;
import java.util.ArrayList;
import java.util.List;

public abstract class Task extends Thread {

	private boolean finished = false;
	private Object status = null;
	private Object result = null;
	private Exception error = null;
	private boolean cancelled = false;
	private List<TaskListener> listeners = new ArrayList<TaskListener>();

	/**
	 * Adds an observer to this instance.
	 * A shallow copy of the original list is made just in case this method is
	 * called while iterating through the list.  This could happen if called by
	 * a TaskListener.
	 */
	public void addTaskListener(TaskListener listener) {
		ArrayList<TaskListener> newListeners = new ArrayList<TaskListener>(listeners);
		newListeners.add(listener);
		listeners = newListeners;
	}

	/**
	 * Removes an observer from this instance.
	 * A shallow copy of the original list is made just in case this method is
	 * called while iterating through the list.  This could happen if called by
	 * a TaskListener.
	 */
	public void removeTaskListener(TaskListener listener) {
		ArrayList<TaskListener> newListeners = new ArrayList<TaskListener>(listeners);
		newListeners.remove(listener);
		listeners = newListeners;
	}

	/**
	 * Alerts all observers that this task is finished.
	 * Should be called by subclasses when the task is finished.
	 */
	protected void reportFinished() {
		finished = true;
		for(TaskListener listener : listeners)
			listener.taskFinished();
	}

	/**
	 * Alerts all observers of the current status of the task.
	 * Should be called report progress and/or intermediate information, etc.
	 * Any objects passed should be copies and/or immutable so that the
	 * same object is not modified by multiple threads.
	 */
	protected void reportStatus(Object obj) {
		status = obj;
		for(TaskListener listener : listeners)
			listener.taskStatus(obj);
	}

	/**
	 * Alerts all observers of the result of this task.
	 * Should be called to return the result from this task, if any.
	 */
	protected void reportResult(Object obj) {
		result = obj;
		for(TaskListener listener : listeners)
			listener.taskResult(obj);
	}

	/**
	 * Alerts all observers of exceptions or errors.
	 * Should be called instead of throwing an exception,
	 * which goes nowhere because this is a separate thread.
	 */
	protected void reportError(Exception e) {
		error = e;
		for(TaskListener listener : listeners)
			listener.taskError(e);
	}

	/**
	 * Returns wether or not the task has reported that it is finished.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Returns the last reported status from this task.
	 */
	public Object getStatus() {
		return status;
	}

	/**
	 * Returns wether or not there is a result to report.
	 */
	public boolean hasResult() {
		return result != null;
	}

	/**
	 * Returns the task's reported result.
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * Returns wether or not there is a error to report.
	 */
	public boolean hasError() {
		return error != null;
	}

	/**
	 * Returns the task's reported error.
	 */
	public Exception getError() {
		return error;
	}

	/**
	 * Requests that the current task be cancelled.
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Returns wether or not this task has been cancelled.
	 * Subclasses should periodically check this.
	 */
	public boolean isCancelled() {
		return cancelled;
	}

}
