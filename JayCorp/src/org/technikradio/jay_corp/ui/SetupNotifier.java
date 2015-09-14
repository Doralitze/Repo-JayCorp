package org.technikradio.jay_corp.ui;

public interface SetupNotifier {

	/**
	 * Tell the page that it is activated
	 */
	public void activateCurrentPage();

	/**
	 * Ask the page if is OK to go forward
	 * 
	 * @return the answer
	 */
	public boolean canGoForward();

	/**
	 * Tell the page that it was added to a slider container
	 */
	public void addedToSlider();

	/**
	 * Tell the page that it lost focus
	 */
	public void leaveFocus();

}
