public interface TaskListener {

	public void taskFinished();
	public void taskStatus(Object obj);
	public void taskResult(Object obj);
	public void taskError(Exception e);

}
