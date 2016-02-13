/*
JayCorp-Client/PasswordInputDialog.java
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
package org.technikradio.jay_corp.ui.helpers;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class PasswordInputDialog extends JFrame {

	private static final long serialVersionUID = 8975995288510019150L;

	private Window owner;
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

	public PasswordInputDialog(Window owner) {
		super();
		this.owner = owner;
		setup();
	}

	private void setup() {
		// For debug purposes
		// this.setVisible(true);

		final PasswordInputDialog owner = this;

		this.setLayout(null);
		this.setResizable(false);
		this.setSize(800, 150);

		okButton = new JButton(Strings.getString("PasswordInputDialog.Submit")); //$NON-NLS-1$
		okButton.setToolTipText(Strings.getString("PasswordInputDialog.SubmitToolTip")); //$NON-NLS-1$
		okButton.setLocation(630, 95);
		okButton.setSize(150, 30);
		abortButton = new JButton(Strings.getString("PasswordInputDialog.Cancle")); //$NON-NLS-1$
		abortButton.setToolTipText(Strings.getString("PasswordInputDialog.CancleToolTip")); //$NON-NLS-1$
		abortButton.setSize(150, 30);
		abortButton.setLocation(470, 95);
		pathText = new JPasswordField();
		pathText.setToolTipText(Strings.getString("PasswordInputDialog.FirstInputToolTip")); //$NON-NLS-1$
		pathText.setSize(610, 30);
		pathText.setLocation(10, 50);
		pass2 = new JPasswordField();
		pass2.setToolTipText(Strings.getString("PasswordInputDialog.SecondInputToolTip")); //$NON-NLS-1$
		pass2.setSize(610, 30);
		pass2.setLocation(10, 15);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (new String(pathText.getPassword()).equals(new String(pass2.getPassword())))
					found = WorkState.found;
				else
					JOptionPane.showMessageDialog(null, Strings.getString("PasswordInputDialog.NoMatchMessageText"), //$NON-NLS-1$
							Strings.getString("PasswordInputDialog.NoMatchMessageHeader"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
			}
		});

		abortButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				found = WorkState.aborted;
			}
		});

		infoLabelMain = new JLabel(Strings.getString("PasswordInputDialog.FirstHelperLabel")); //$NON-NLS-1$
		infoLabelMain.setLocation(30 + pathText.getWidth(), 15);
		infoLabelMain.setVisible(true);
		infoLabelMain.setSize(150, 25);
		infoLabelProof = new JLabel(Strings.getString("PasswordInputDialog.SecondHelperLabel")); //$NON-NLS-1$
		infoLabelProof.setLocation(30 + pathText.getWidth(), 50);
		infoLabelProof.setVisible(true);
		infoLabelProof.setSize(150, 25);

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
			Console.log(LogType.Error, owner, "Swing messed every thing up:"); //$NON-NLS-1$
			e1.printStackTrace();
		}
		this.invalidate();

		if (this.owner != null) {
			Window mf = this.owner;
			int x = mf.getLocation().x + ((mf.getWidth() / 2) - this.getWidth() / 2);
			int y = mf.getLocation().y + ((mf.getHeight() / 2) - this.getHeight() / 2);
			this.setLocation(x, y);
		}

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

	public static String showDialog(Window owner, String startFile, String title) {
		final PasswordInputDialog afid = new PasswordInputDialog(owner);
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
					Console.log(LogType.Error, "UIVisualizerThread", //$NON-NLS-1$
							"This shouln´t had happen:"); //$NON-NLS-1$
					e.printStackTrace();
				} // Wait for UI Thread
				afid.setVisible(true);
			}
		});
		t.setDaemon(true);
		t.setName("UIVisualizerThread"); //$NON-NLS-1$
		t.start();
		afid.repaint();
		while (afid.found == WorkState.notSelected) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Console.log(LogType.Warning, "AdvancedFileInputDialog", //$NON-NLS-1$
						"The process of choosing a new password was interrupted"); //$NON-NLS-1$
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
