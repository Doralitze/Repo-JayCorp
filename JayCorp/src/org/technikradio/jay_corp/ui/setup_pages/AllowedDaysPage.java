/*
JayCorp-Client/AllowedDaysPage.java
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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.AdvancedPage;
import org.technikradio.jay_corp.ui.ProcessStartNotifier;
import org.technikradio.jay_corp.ui.SetupNotifier;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

@AdvancedPage
public class AllowedDaysPage extends JPanel implements SetupNotifier, ProcessStartNotifier {

	private static final long serialVersionUID = 7891918860984860252L;

	private JSpinner allowedDaysSelector;
	private JLabel infoLabel;
	private int initialValue = 0;
	private boolean done = false;

	public AllowedDaysPage() {
		super();
		setup();
	}

	private void setup() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		infoLabel = new JLabel(Strings.getString("AllowedDaysPage.InfoText")); //$NON-NLS-1$
		this.add(infoLabel);
		allowedDaysSelector = new JSpinner();
		allowedDaysSelector.setModel(new SpinnerNumberModel(Protocol.getCurrentUser().getExtraDays(), 0, 366, 1));
		allowedDaysSelector.setToolTipText(Strings.getString("SettingsFrame.AllowedDaysToolTip")); //$NON-NLS-1$
		initialValue = Protocol.getCurrentUser().getExtraDays();
		this.add(allowedDaysSelector);
	}

	public AllowedDaysPage(LayoutManager layout) {
		super(layout);
		setup();
	}

	public AllowedDaysPage(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setup();
	}

	public AllowedDaysPage(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		setup();
	}

	@Override
	public void activateCurrentPage() {
		Console.log(LogType.StdOut, this, "Set focus on this"); //$NON-NLS-1$
	}

	@Override
	public boolean canGoForward() {
		SpinnerNumberModel model = (SpinnerNumberModel) allowedDaysSelector.getModel();
		if (model.getNumber().intValue() != 0 && model.getNumber().intValue() != initialValue)
			return true;
		else
			return false;
	}

	@Override
	public void addedToSlider() {

	}

	@Override
	public void leaveFocus() {

	}

	@Override
	public void startTransmission() {
		SpinnerNumberModel model = (SpinnerNumberModel) allowedDaysSelector.getModel();
		Protocol.changeExtraDays(model.getNumber().intValue(), Protocol.getCurrentUser().getID());
		done = true;
	}

	@Override
	public String toString() {
		return "Setup:IntroPage"; //$NON-NLS-1$
	}

	@Override
	public int getStrenght() {
		return 1;
	}

	@Override
	public int getWorkDone() {
		if (done)
			return 1;
		else
			return 0;
	}

}
