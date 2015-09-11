package org.technikradio.jay_corp.ui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.technikradio.jay_corp.JayCorp;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class SetupFrame extends JFrame {

	private static final long serialVersionUID = 42424380352170331L;
	private final SetupFrame ownHandle = this;
	private SlideMenuContainer smc;
	private boolean done = false;

	public SetupFrame() throws HeadlessException {
		setup();
	}

	public SetupFrame(GraphicsConfiguration gc) {
		super(gc);
		setup();
	}

	public SetupFrame(String title) throws HeadlessException {
		super(title);
		setup();
	}

	public SetupFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		setup();
	}

	public void setup() {
		Console.log(LogType.StdOut, this, "Loading setup");
		this.setDefaultCloseOperation(SetupFrame.DISPOSE_ON_CLOSE);
		this.setSize(500, 500);
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
				Protocol.disconnect();
				JayCorp.exit(12);
			}
		});
		smc.setSize(new Dimension(500, 500));
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
				Protocol.disconnect();
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
		repaint();
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
