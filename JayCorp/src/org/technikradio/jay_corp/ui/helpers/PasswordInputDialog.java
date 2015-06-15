package org.technikradio.jay_corp.ui.helpers;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPasswordField;

import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class PasswordInputDialog extends JDialog {

	private static final long serialVersionUID = 8975995288510019150L;

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

	public PasswordInputDialog(Frame owner) {
		super(owner);
		setup();
	}

	public PasswordInputDialog(Dialog owner) {
		super(owner);
		setup();
	}

	public PasswordInputDialog(Window owner) {
		super(owner);
		setup();
	}

	public PasswordInputDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public PasswordInputDialog(Frame owner, String title) {
		super(owner, title);
		setup();
	}

	public PasswordInputDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public PasswordInputDialog(Dialog owner, String title) {
		super(owner, title);
		setup();
	}

	public PasswordInputDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup();
	}

	public PasswordInputDialog(Window owner, String title) {
		super(owner, title);
		setup();
	}

	public PasswordInputDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public PasswordInputDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public PasswordInputDialog(Window owner, String title,
			ModalityType modalityType) {
		super(owner, title, modalityType);
		setup();
	}

	public PasswordInputDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public PasswordInputDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public PasswordInputDialog(Window owner, String title,
			ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup();
	}

	private void setup() {
		// For debug purposes
		// this.setVisible(true);

		final PasswordInputDialog owner = this;

		this.setResizable(false);
		this.setSize(800, 125);

		okButton = new JButton(
				Strings.getString("AdvancedFileInputDialog.OKText")); //$NON-NLS-1$
		okButton.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.OKToolTip")); //$NON-NLS-1$
		okButton.setLocation(630, 70);
		okButton.setSize(150, 30);
		abortButton = new JButton(
				Strings.getString("AdvancedFileInputDialog.AbortText")); //$NON-NLS-1$
		abortButton.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.AbortToolTip")); //$NON-NLS-1$
		abortButton.setSize(150, 30);
		abortButton.setLocation(470, 70);
		pathText = new JPasswordField();
		pathText.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.TextFieldToolTip")); //$NON-NLS-1$
		pathText.setSize(610, 30);
		pathText.setLocation(10, 30);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pathText.getText().equals(pass2.getText()))
					found = WorkState.found;
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

		this.add(okButton);
		this.add(abortButton);
		this.add(pathText);

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				owner.setVisible(true);
			}
		});
		this.invalidate();
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
		return pathText.getText();
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
		PasswordInputDialog afid = new PasswordInputDialog(owner);
		if (startFile != null)
			afid.setFile(startFile);

		afid.setTitle(title);
		int lastPrio = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(1);
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
