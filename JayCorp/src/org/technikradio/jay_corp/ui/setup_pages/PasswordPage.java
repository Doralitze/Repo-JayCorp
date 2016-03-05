/*
JayCorp-Client/PasswordPage.java
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

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.AdvancedPage;
import org.technikradio.jay_corp.ui.ProcessStartNotifier;
import org.technikradio.jay_corp.ui.SetupNotifier;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

@AdvancedPage
public class PasswordPage extends JPanel implements SetupNotifier, ProcessStartNotifier {

	private static final long serialVersionUID = 7891918860984860252L;

	private JLabel infoLabelMain;
	private JLabel infoLabelProof;
	private JLabel statusLabel;
	private JPasswordField pathText;
	private JPasswordField pass2;

	private boolean isDone = false;

	public PasswordPage() {
		super();
		setup();
	}

	private void setup() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// this.setSize(500, 470);
		pathText = new JPasswordField();
		pathText.setToolTipText(Strings.getString("PasswordInputDialog.FirstInputToolTip")); //$NON-NLS-1$
		pathText.setSize(610, 30);
		pathText.setLocation(10, 50);
		pathText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkValidity();
			}
		});
		pathText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				checkValidity();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				checkValidity();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkValidity();
			}
		});
		pass2 = new JPasswordField();
		pass2.setToolTipText(Strings.getString("PasswordInputDialog.SecondInputToolTip")); //$NON-NLS-1$
		pass2.setSize(610, 30);
		pass2.setLocation(10, 15);
		pass2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkValidity();
			}
		});
		pass2.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				checkValidity();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				checkValidity();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkValidity();
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

		statusLabel = new JLabel(Strings.getString("PasswordPage.EmptyPasswordMessage")); //$NON-NLS-1$
		statusLabel.setForeground(Color.RED);

		this.add(infoLabelMain);
		this.add(pathText);
		this.add(infoLabelProof);
		this.add(pass2);
		this.add(statusLabel);
	}

	private boolean checkValidity() {
		if (pathText.getPassword().length == 0 || pass2.getPassword().length == 0) {
			statusLabel.setForeground(Color.RED);
			statusLabel.setText(Strings.getString("PasswordPage.SecondEmptyPasswordHint")); //$NON-NLS-1$
			return false;
		}
		if (compare(pathText.getPassword(), Protocol.getCurrentUser().getPassword().toCharArray())) {
			statusLabel.setForeground(Color.RED);
			statusLabel.setText(Strings.getString("PasswordPage.ChoseNewPasswordHint")); //$NON-NLS-1$
			return false;
		}
		if (!compare(pathText.getPassword(), pass2.getPassword())) {
			statusLabel.setForeground(Color.RED);
			statusLabel.setText(Strings.getString("PasswordPage.MismatchHint")); //$NON-NLS-1$
			return false;
		}
		statusLabel.setForeground(Color.GREEN);
		statusLabel.setText(Strings.getString("PasswordPage.OKHint")); //$NON-NLS-1$
		return true;
	}

	private boolean compare(char[] m1, char[] m2) {
		if (m1.length != m2.length)
			return false;
		for (int i = 0; i < m1.length; i++)
			if (m1[i] != m2[i])
				return false;
		return true;
	}

	public PasswordPage(LayoutManager layout) {
		super(layout);
		setup();
	}

	public PasswordPage(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setup();
	}

	public PasswordPage(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		setup();
	}

	@Override
	public void activateCurrentPage() {
		Console.log(LogType.StdOut, this, "Set focus on this"); //$NON-NLS-1$
	}

	@Override
	public boolean canGoForward() {
		return checkValidity();
	}

	@Override
	public void addedToSlider() {
		// Nothing to do here
	}

	@Override
	public void leaveFocus() {
		// Nothing to do here
	}

	@Override
	public void startTransmission() {
		Protocol.changePassword(new String(pathText.getPassword()), Protocol.getCurrentUser().getID());
		isDone = true;
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
		if (!isDone)
			return 0;
		return 1;
	}

}
