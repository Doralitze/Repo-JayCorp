package org.technikradio.jay_corp.ui.setup_pages;

import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.technikradio.jay_corp.ProgressChangedNotifier;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.AdvancedPage;
import org.technikradio.jay_corp.ui.Calendar;
import org.technikradio.jay_corp.ui.MainFrame;
import org.technikradio.jay_corp.ui.ProcessStartNotifier;
import org.technikradio.jay_corp.ui.SetupNotifier;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

@AdvancedPage
public class SelectedDaysDialog extends JPanel implements SetupNotifier, ProcessStartNotifier {

	private static final long serialVersionUID = 7891918860984860252L;

	private Calendar c;
	private int progress = 0;

	public SelectedDaysDialog() {
		super();
		setup();
	}

	private void setup() {
		// this.setLayout(null);
		c = new Calendar();
		c.setContent(Protocol.getCurrentUser().getSelectedDays());
		c.setMinimumSize(new Dimension(830, 500));
		c.setPreferredSize(new Dimension(880, 530));
		c.setEditEnabled(true);
		c.setMaxNumDay(
				MainFrame.getScale(Protocol.getCurrentUser().getWorkAge()) + Protocol.getCurrentUser().getExtraDays());
		Console.log(LogType.StdOut, this, "Calendar size: " + this.getBounds()); //$NON-NLS-1$
		// c.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.add(c);
	}

	/*
	 * @Override public void setBounds(Rectangle r) { super.setBounds(r);
	 * c.setBounds(0, 0, this.getWidth(), this.getHeight()); c.repaint(); }
	 */

	public SelectedDaysDialog(LayoutManager layout) {
		super(layout);
		setup();
	}

	public SelectedDaysDialog(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setup();
	}

	public SelectedDaysDialog(LayoutManager layout, boolean isDoubleBuffered) {
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
		Console.log(LogType.Warning, this, "Temporary enabled editing on server..."); //$NON-NLS-1$
		Protocol.setEditEnableOnServer(true);
		Protocol.setEditEnabled(true);
	}

	@Override
	public void leaveFocus() {
		// System.out.println(c.getBounds());
	}

	@Override
	public void startTransmission() {
		boolean successfullBackup = Protocol.moveToBackup(Protocol.getCurrentUser().getID());
		if (successfullBackup)
			Protocol.rmDatabaseEntries(Protocol.getCurrentUser().getID());
		// Protocol.moveToBackup(Protocol.getCurrentUser().getID());
		progress++;
		Protocol.transmitTable(c.buildFromCache(), Protocol.getCurrentUser().getID(), new ProgressChangedNotifier() {

			@Override
			public void progressChanged(int min, int max, int current) {
				progress = 1 + current;
			}
		}, false);
	}

	@Override
	public String toString() {
		return "Setup:SelectedDaysPage"; //$NON-NLS-1$
	}

	@Override
	public int getStrenght() {
		return 1 + c.buildFromCache().getDays().size();
	}

	@Override
	public int getWorkDone() {
		return progress;
	}

}
