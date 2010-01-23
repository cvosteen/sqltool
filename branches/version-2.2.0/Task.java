import java.util.*;

public abstract class Task extends Thread {

	private boolean finished = false;
	private Object status = null;
	private Object result = null;
	private Exception error = null;
	private boolean cancelled = false;

	private List<TaskListener> listeners = new Vector<TaskListener>();

	public Task() {
		super();
	} 

	// Methods to manage TaskListeners
	public synchronized void addTaskListener(TaskListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeTaskListener(TaskListener listener) {
		listeners.remove(listener);
	}

	// protected methods to alert TaskListeners
	// Note: Don't modify objects that have been passed to these
	// four methods below!!! Pass a copy if you are going to modify
	// the object.
	protected synchronized void reportFinished() {
		finished = true;
		for(TaskListener listener : listeners)
			listener.taskFinished();
	}

	protected synchronized void reportStatus(Object obj) {
		status = obj;
		for(TaskListener listener : listeners)
			listener.taskStatus(obj);
	}

	protected synchronized void reportResult(Object obj) {
		result = obj;
		for(TaskListener listener : listeners)
			listener.taskResult(obj);
	}

	protected synchronized void reportError(Exception e) {
		error = e;
		for(TaskListener listener : listeners)
			listener.taskError(e);
	}

	// Public methods to inquire on the state of the Task
	public synchronized boolean isFinished() {
		return finished;
	}

	public synchronized Object getStatus() {
		return status;
	}

	public synchronized boolean hasResult() {
		return result != null;
	}

	public synchronized Object getResult() {
		return result;
	}

	public synchronized boolean hasError() {
		return error != null;
	}

	public synchronized Exception getError() {
		return error;
	}

	// methods for stopping the Task
	public synchronized void cancel() {
		cancelled = true;
	}

	public synchronized boolean isCancelled() {
		return cancelled;
	}

}
