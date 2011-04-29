/**
 * A convenience class to make is easier and less verbose
 * to create TaskListeners.
 */

package cvosteen.sqltool.task;

public class TaskAdapter implements TaskListener {

	public void taskFinished() { }
	public void taskStatus(Object obj) { }
	public void taskResult(Object obj) { }
	public void taskError(Exception e) { }

}
