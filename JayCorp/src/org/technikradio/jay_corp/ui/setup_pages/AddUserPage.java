package org.technikradio.jay_corp.ui.setup_pages;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.AdvancedPage;
import org.technikradio.jay_corp.ui.ProcessStartNotifier;
import org.technikradio.jay_corp.ui.SetupNotifier;
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
		setFont(UIManager.getFont("Label.font"));
		f.setWrapStyleWord(true);
		f.setLineWrap(true);
		f.setBounds(0, 0, this.getWidth(), this.getHeight());
		// f.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		f.setText("Sie werden am Ende dieses Setup dazu aufgefordert werden,\n"
				+ "die Benutzerdatei zu laden. Desweiteren werden alle Änderungen,\n"
				+ "welche Sie hier vorgenommen haben wirksam.\n\n"
				+ "Wenn Sie bereit sind, clicken Sie auf \"Fertigstellen\".");
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
		Console.log(LogType.StdOut, this, "Set focus on this");
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
		Console.log(LogType.StdOut, this, "resuming process of transmitting data");
		Protocol.setEditEnableOnServer(false);
		Protocol.setEditEnabled(false);
		Protocol.save();
	}

	@Override
	public String toString() {
		return "Setup:AddUserPage";
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
