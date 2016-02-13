/*
JayCorp-Client/AddUserPage.java
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
package org.technikradio.jay_corp.ui.setup_pages;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.AdvancedPage;
import org.technikradio.jay_corp.ui.ProcessStartNotifier;
import org.technikradio.jay_corp.ui.SetupNotifier;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.jay_corp.ui.helpers.AlternateCSVImporter;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

@AdvancedPage
public class AddUserPage extends JPanel implements SetupNotifier, ProcessStartNotifier {

	private static final long serialVersionUID = 7891918860984860252L;

	private final AddUserPage ownHandle = this;

	private JTextArea f;
	private boolean done;

	public AddUserPage() {
		super();
		setup();
	}

	private void setup() {
		f = new JTextArea(26, 40);
		f.setEditable(false);
		f.setCursor(null);
		f.setOpaque(false);
		f.setFocusable(false);
		setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		f.setWrapStyleWord(true);
		f.setLineWrap(true);
		f.setBounds(0, 0, this.getWidth(), this.getHeight());
		// f.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		f.setText(Strings.getString("AddUserPage.LabelPart1") //$NON-NLS-1$
				+ Strings.getString("AddUserPage.LabelPart2") //$NON-NLS-1$
				+ Strings.getString("AddUserPage.LabelPart3") //$NON-NLS-1$
				+ Strings.getString("AddUserPage.LabelPart4")); //$NON-NLS-1$
		this.add(f);
	}

	public AddUserPage(LayoutManager layout) {
		super(layout);
		setup();
	}

	public AddUserPage(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setup();
	}

	public AddUserPage(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		setup();
	}

	@Override
	public void activateCurrentPage() {
		Console.log(LogType.StdOut, this, "Set focus on this"); //$NON-NLS-1$
	}

	@Override
	public boolean canGoForward() {
		return true;
	}

	@Override
	public void addedToSlider() {

	}

	@Override
	public void leaveFocus() {

	}

	@Override
	public void startTransmission() {
		new AlternateCSVImporter(ownHandle).upload();
		/*
		 * Thread t = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // new CSVImporter(ownHandle).upload();
		 * } }); t.setName("UITaskSyncThread"); t.start(); while (t.isAlive()) {
		 * try { Thread.sleep(50); } catch (InterruptedException e) {
		 * e.printStackTrace(); Thread.currentThread().interrupt(); } }
		 */
		Console.log(LogType.StdOut, this, "resuming process of transmitting data"); //$NON-NLS-1$
		Protocol.setEditEnableOnServer(false);
		Protocol.setEditEnabled(false);
		Protocol.save();
	}

	@Override
	public String toString() {
		return "Setup:AddUserPage"; //$NON-NLS-1$
	}

	@Override
	public int getStrenght() {
		return 1;
	}

	@Override
	public int getWorkDone() {
		if (!done)
			return 0;
		else
			return 1;
	}

}
