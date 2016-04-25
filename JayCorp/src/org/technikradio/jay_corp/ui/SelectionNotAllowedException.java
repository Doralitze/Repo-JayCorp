package org.technikradio.jay_corp.ui;

import java.util.ArrayList;

import org.technikradio.universal_tools.ParaDate;

public class SelectionNotAllowedException extends Exception {

	private static final long serialVersionUID = 727136990702288041L;
	private ArrayList<ParaDate> forbiddenDays;

	public SelectionNotAllowedException() {
		super();
		forbiddenDays = new ArrayList<ParaDate>();
	}

	public SelectionNotAllowedException(String message) {
		super(message);
		forbiddenDays = new ArrayList<ParaDate>();
	}

	public SelectionNotAllowedException(Throwable cause) {
		super(cause);
		forbiddenDays = new ArrayList<ParaDate>();
	}

	public SelectionNotAllowedException(String message, Throwable cause) {
		super(message, cause);
		forbiddenDays = new ArrayList<ParaDate>();
	}

	public SelectionNotAllowedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		forbiddenDays = new ArrayList<ParaDate>();
	}
	
	public boolean addForbiddenDate(ParaDate date){
		return forbiddenDays.add(date);
	}
	
	public ParaDate[] getProblematicDays(){
		return (ParaDate[]) forbiddenDays.toArray();
	}

}
