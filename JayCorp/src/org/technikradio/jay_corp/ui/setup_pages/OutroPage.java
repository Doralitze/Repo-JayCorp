package org.technikradio.jay_corp.ui.setup_pages;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.technikradio.jay_corp.ui.AdvancedPage;
import org.technikradio.jay_corp.ui.ProcessStartNotifier;
import org.technikradio.jay_corp.ui.SetupNotifier;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

@AdvancedPage
public class OutroPage extends JPanel implements SetupNotifier, ProcessStartNotifier {

	private static final long serialVersionUID = 7891918860984860252L;

	private JTextArea f;

	public OutroPage() {
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
		f.setText(
				"Sie haben nun alle wichtigen Informationen eigegeben.\nWenn Sie auf \"Fertigstellen\" klicken wird das Programm neu starten und Sie\nkönnen (sobald es der Administrator freigibt) Ihre Urlaubstage eintragen.");
		this.add(f);
	}

	public OutroPage(LayoutManager layout) {
		super(layout);
		setup();
	}

	public OutroPage(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setup();
	}

	public OutroPage(LayoutManager layout, boolean isDoubleBuffered) {
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
	}

	@Override
	public String toString() {
		return "Setup:IntroPage";
	}

	@Override
	public int getStrenght() {
		return 0;
	}

	@Override
	public int getWorkDone() {
		return 0;
	}

}
