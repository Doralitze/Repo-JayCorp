package org.technikradio.jay_corp.ui;

public class SelectionNotAllowedException extends Exception {

	private static final long serialVersionUID = 727136990702288041L;

	public SelectionNotAllowedException() {
		super();
	}

	public SelectionNotAllowedException(String message) {
		super(message);
	}

	public SelectionNotAllowedException(Throwable cause) {
		super(cause);
	}

	public SelectionNotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}

	public SelectionNotAllowedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
