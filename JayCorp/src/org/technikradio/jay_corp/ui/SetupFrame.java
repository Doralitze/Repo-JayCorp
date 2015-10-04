package org.technikradio.jay_corp.ui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JDialog;

import org.technikradio.jay_corp.JayCorp;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.helpers.ProgressIndicator;
import org.technikradio.jay_corp.ui.setup_pages.AddUserPage;
import org.technikradio.jay_corp.ui.setup_pages.AllowedDaysPage;
import org.technikradio.jay_corp.ui.setup_pages.DaysInfoPage;
import org.technikradio.jay_corp.ui.setup_pages.IntroPage;
import org.technikradio.jay_corp.ui.setup_pages.OutroPage;
import org.technikradio.jay_corp.ui.setup_pages.PasswordPage;
import org.technikradio.jay_corp.ui.setup_pages.SelectedDaysDialog;
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
		{
			int posy, posx, width, height;
			posx = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - 250;
			posy = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - 250;
			width = 500;
			height = 500;
			this.setBounds(new Rectangle(posx, posy, width, height));
		}
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
				if (!(smc.getIndex() + 1 == smc.getCardSize())) {
					smc.goTo(smc.getIndex() + 1);
					Console.log(LogType.StdOut, this, "went foreward");
				} else {
					// TODO get / process actions
					final ProgressIndicator i = new ProgressIndicator();
					int totalWork = 0;
					for (ProcessStartNotifier p : psns) {
						totalWork += p.getStrenght();
					}
					final int mTotal = totalWork;
					i.setValv(0, totalWork, 0);
					i.setInfoLabelText("Ihre Änderungen werden angewendet...");
					i.setTitle("Bitte haben Sie einen Moment Geduld.");
					i.setVisible(true);
					Console.log(LogType.Information, this,
							"Setup contained " + smc.getCardSize() + " elements and was @" + smc.getIndex());
					final Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							boolean running = true;
							while (running) {
								int sum = 0;
								for (ProcessStartNotifier p : psns) {
									sum += p.getWorkDone();
								}
								if (sum == mTotal)
									running = false;
								i.setValv(0, mTotal, sum);
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
									return;
								}
							}
						}
					});
					t.setName("SetupFrame:ProgressUpdater");
					t.start();
					for (ProcessStartNotifier p : psns) {
						p.startTransmission();
					}
					// Protocol.save();
					t.interrupt();
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
				this.setSize(900, 600);
				smc.setSize(new Dimension(900, 580));
				smc.repaint();
				Console.log(LogType.StdOut, this, "Loading root user setup...");
				// Add days info page
				{
					DaysInfoPage dip = new DaysInfoPage();
					psns.add(dip);
					smc.addPanel(dip);
				}
				// Add selected days dialog
				{
					// Add add user dialog (with rights)
					AddUserPage aud = new AddUserPage();
					psns.add(aud);
					smc.addPanel(aud);
					// Add days page
					SelectedDaysDialog sdd = new SelectedDaysDialog();
					smc.addPanel(sdd);
					psns.add(sdd);
				}
			} else {
				Console.log(LogType.StdOut, this, "Loading normal user setup...");
				// Add allowed days dialog
				{
					AllowedDaysPage adp = new AllowedDaysPage();
					psns.add(adp);
					smc.addPanel(adp);
				}
				// Add out + warning page
				{
					OutroPage op = new OutroPage();
					psns.add(op);
					smc.addPanel(op);
				}
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
