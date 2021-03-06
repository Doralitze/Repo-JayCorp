/*
JayCorp-Client/SetupNotifier.java
Copyright (C) 2015-2016  Leon C. Dietrich (Doralitze)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
