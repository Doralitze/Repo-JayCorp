package org.technikradio.jay_corp.ui.helpers;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class PasswordInputDialog extends JFrame {

	private static final long serialVersionUID = 8975995288510019150L;

	private JLabel infoLabelMain;
	private JLabel infoLabelProof;
	private JButton okButton;
	private JButton abortButton;
	private JPasswordField pathText;
	private JPasswordField pass2;
	private WorkState found = WorkState.notSelected;

	private enum WorkState {
		notSelected, found, aborted
	}

	public PasswordInputDialog() {
		setup();
	}

	private void setup() {
		// For debug purposes
		// this.setVisible(true);

		final PasswordInputDialog owner = this;

		this.setLayout(null);
		this.setResizable(false);
		this.setSize(800, 150);

		infoLabelMain = new JLabel("Passwort eingeben");
		infoLabelMain.setLocation(630, 30);
		infoLabelProof = new JLabel("Passwort wiederhohlen");
		infoLabelProof.setLocation(630, 50);

		okButton = new JButton(
				Strings.getString("AdvancedFileInputDialog.OKText")); //$NON-NLS-1$
		okButton.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.OKToolTip")); //$NON-NLS-1$
		okButton.setLocation(630, 95);
		okButton.setSize(150, 30);
		abortButton = new JButton(
				Strings.getString("AdvancedFileInputDialog.AbortText")); //$NON-NLS-1$
		abortButton.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.AbortToolTip")); //$NON-NLS-1$
		abortButton.setSize(150, 30);
		abortButton.setLocation(470, 95);
		// TODO fix labels and tool tips
		pathText = new JPasswordField();
		pathText.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.TextFieldToolTip")); //$NON-NLS-1$
		pathText.setSize(610, 30);
		pathText.setLocation(10, 50);
		pass2 = new JPasswordField();
		pass2.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.TextFieldToolTip")); //$NON-NLS-1$
		pass2.setSize(610, 30);
		pass2.setLocation(10, 15);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (new String(pathText.getPassword()).equals(new String(pass2
						.getPassword())))
					found = WorkState.found;
				// TODO use getCharSequenz and throw a message if the passwords
				// don´t match
			}
		});

		abortButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				found = WorkState.aborted;
			}
		});

		okButton.setVisible(true);
		abortButton.setVisible(true);
		pathText.setVisible(true);
		pass2.setVisible(true);

		this.add(okButton);
		this.add(abortButton);
		this.add(pathText);
		this.add(pass2);
		this.add(infoLabelMain);
		this.add(infoLabelProof);

		try {
			EventQueue.invokeLater(new Runnable() {

				public void run() {
					owner.setVisible(true);
				}
			});
		} catch (Exception e1) {
			Console.log(LogType.Error, owner, "Swing messed every thing up:");
			e1.printStackTrace();
		}
		this.invalidate();
		// this.setVisible(true);
	}

	public void setTitle(String title) {
		try {
			if (title == null || title.equals("")) //$NON-NLS-1$
				return;
			// this.setTitle(title);

		} catch (Exception e) {
			;
		}
	}

	public String getSelectedText() {
		return new String(pathText.getPassword());
	}

	public void setFile(String f) {
		pathText.setText(f);
	}

	/*
	 * @Override public String toString() { return "AdvancedFileInputDialog";
	 * //$NON-NLS-1$ }
	 */
	@Override
	public void update(Graphics g) {
		super.update(g);

		okButton.paint(g);
		abortButton.paint(g);
		pathText.paint(g);
	}

	public static String showDialog(Frame owner, String startFile, String title) {
		final PasswordInputDialog afid = new PasswordInputDialog();
		if (startFile != null)
			afid.setFile(startFile);

		afid.setTitle(title);
		int lastPrio = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(1);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Console.log(LogType.Error, "UIVisualizerThread",
							"This shouln´t had happen:");
					e.printStackTrace();
				}// Wait for UI Thread
				afid.setVisible(true);
			}
		});
		t.setDaemon(true);
		t.setName("UIVisualizerThread");
		t.start();
		while (afid.found == WorkState.notSelected) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Console.log(LogType.Warning, "AdvancedFileInputDialog", //$NON-NLS-1$
						"The process of choosing a file was interrupted"); //$NON-NLS-1$
				e.printStackTrace();
				Thread.currentThread().interrupt();
				return null;
			}
		}
		Thread.currentThread().setPriority(lastPrio);
		afid.setVisible(false);
		switch (afid.found) {
		case aborted:
			return null;
		case found:
			return afid.getSelectedText();
		case notSelected:
			throw new RuntimeException("Something went wrong..."); //$NON-NLS-1$
		default:
			throw new RuntimeException("Something went wrong..."); //$NON-NLS-1$
		}
	}

}
