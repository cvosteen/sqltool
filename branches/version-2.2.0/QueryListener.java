/**
 * Changes in v2.1:
 * - New interface to listen on query that is executed asynchronously
 */

public interface QueryListener {

	public void queryCompleted();
	public void queryFailed();
}
