public interface TaskListener {

	public void finished();
	public void status(Object obj);
	public void result(Object obj);
	public void error(Exception e);

}
