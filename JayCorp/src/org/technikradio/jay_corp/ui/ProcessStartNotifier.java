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
	 * Get the amount of work left already done
	 * 
	 * @return the amount of work done
	 */
	public int getWorkDone();
}
