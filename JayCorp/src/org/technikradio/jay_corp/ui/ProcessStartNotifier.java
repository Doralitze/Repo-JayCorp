package org.technikradio.jay_corp.ui;

public interface ProcessStartNotifier {

	/**
	 * Notify about the transmission start
	 */
	public void startTransmission();

	/**
	 * Get the amount of work to do
	 * 
	 * @return the amount of work
	 */
	public int getStrenght();

	/**
	 * Get the amount of work left to do
	 * 
	 * @return the amount of work left
	 */
	public int getWorkLeft();
}
