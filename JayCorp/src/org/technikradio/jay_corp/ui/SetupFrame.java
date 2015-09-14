package org.technikradio.jay_corp.ui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JDialog;

import org.technikradio.jay_corp.JayCorp;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.setup_pages.IntroPage;
import org.technikradio.jay_corp.ui.setup_pages.PasswordPage;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class SetupFrame extends JDialog {

	private static final long serialVersionUID = 42424380352170331L;
	private final SetupFrame ownHandle = this;
	private SlideMenuContainer smc;
	private boolean done = false;
	private ArrayList<ProcessStartNotifier> psns;

	public SetupFrame() throws HeadlessException {
		setup();
	}

	public void setup() {
		Console.log(LogType.StdOut, this, "Loading setup");
		this.setResizable(false);
		this.setDefaultCloseOperation(SetupFrame.DISPOSE_ON_CLOSE);
		this.setSize(500, 500);
		this.setTitle("Setup");
		psns = new ArrayList<ProcessStartNotifier>();
		smc = new SlideMenuContainer(new SetupMovingListener() {
			@Override
			public void goBack() {
				smc.goTo(smc.getIndex() - 1);
			}

			@Override
			public void goForeward() {
				if (!(smc.getIndex() + 1 == smc.getCardSize()))
					smc.goTo(smc.getIndex() + 1);
				else {
					// TODO get / process actions
					done = true;
				}
			}

			@Override
			public void abort() {
				ownHandle.setVisible(false);
				JayCorp.exit(12);
			}
		});
		smc.setSize(new Dimension(500, 480));
		this.add(smc);
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				// Nothing to do here
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				if (!done)
					JayCorp.exit(12);
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		});
		// Add pages
		{
			// Add intro page
			{
				IntroPage i = new IntroPage();
				psns.add(i);
				smc.addPanel(i);
			}
			// Add change password page
			{
				PasswordPage p = new PasswordPage();
				psns.add(p);
				smc.addPanel(p);
			}
			if (Protocol.getCurrentUser().getID() == 0) {

			}
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
		t.setName("UIUpdateThread_SetupFrame");
		t.setDaemon(true);
		t.start();
	}

	/*
	 * @Override public void repaint() { super.repaint(); smc.repaint(); }
	 */

	public void showDialog() {
		Console.log(LogType.StdOut, this, "Displaying setup");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ownHandle.setVisible(true);
			}
		});
		t.setName("Thread:UIUpdateThread_SetupFrame");
		t.start();
		int priority = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (!done)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Console.log(LogType.Error, this, "Sleep process interrupted:");
				e.printStackTrace();
				JayCorp.exit(52);
			}
		Thread.currentThread().setPriority(priority);
		this.dispose();
	}

	@Override
	public String toString() {
		return "SetupFrame";
	}

}
