package gui;

public interface ResponseGetter<T> {

	/**
	 * Called by parent window or other client to get the
	 * user's choice or response from this dialog.
	 */
	public T getResponse();
}
