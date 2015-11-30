package org.technikradio.jay_corp.ui;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.technikradio.jay_corp.MessageStreamHandler;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = -5270618477711942318L;
	private JTextField username;
	private JPasswordField password;
	private JButton submitButton;
	private JButton abortButton;
	private JLabel userLabel;
	private JLabel passLabel;
	private JLabel copyrightLabel;
	private boolean didEntered;
	private JFrame parent;

	public LoginPanel() {
		super();
		setup();
	}

	public LoginPanel(LayoutManager arg0) {
		super(arg0);
		setup();
	}

	public LoginPanel(boolean arg0) {
		super(arg0);
		setup();
	}

	public LoginPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		setup();
	}

	private void setup() {
		this.setSize(500, 200);
		this.setLayout(null);
		this.setPreferredSize(getSize());
		this.copyrightLabel = new JLabel("Copyright (c) Leon Dietrich 2014 - 2015"); //$NON-NLS-1$
		this.copyrightLabel.setBounds(10, this.getHeight() - 25, this.getWidth() - 20, 20);
		this.copyrightLabel.setForeground(Color.WHITE);
		this.add(this.copyrightLabel);
		this.username = new JTextField();
		this.username.setBounds(150, 10, 300, 30);
		this.add(username);
		this.userLabel = new JLabel(Strings.getString("LoginPanel.Username")); //$NON-NLS-1$
		this.userLabel.setBounds(10, 10, 130, 30);
		this.userLabel.setForeground(Color.WHITE);
		this.add(userLabel);
		this.password = new JPasswordField();
		this.password.setBounds(150, 50, 300, 30);
		this.add(password);
		this.passLabel = new JLabel(Strings.getString("LoginPanel.Password")); //$NON-NLS-1$
		this.passLabel.setBounds(10, 50, 130, 30);
		this.passLabel.setForeground(Color.WHITE);
		this.add(passLabel);
		this.submitButton = new JButton(Strings.getString("LoginPanel.Submit")); //$NON-NLS-1$
		this.submitButton.setBounds(390, 150, 100, 30);
		this.submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disableInputs();
				if (username.getText() != "" //$NON-NLS-1$
						&& new String(password.getPassword()) != "") //$NON-NLS-1$
					if (Protocol.isLoginFree(username.getText()))
						if (Protocol.login(username.getText(), new String(password.getPassword())))
							loadWorkspace();
						else {
							JOptionPane.showMessageDialog(parent, Strings.getString("LoginPanel.IncorrectLogin")); //$NON-NLS-1$
							Console.log(LogType.Information, "LoginPanel", //$NON-NLS-1$
									"Entered wrong info: User: " //$NON-NLS-1$
											+ username.getText() + " Password: " //$NON-NLS-1$
											+ new String(password.getPassword()));
							didEntered = false;
							reenable();
							return;
						}
					else{
							didEntered = false;
							reenable();
							JOptionPane.showMessageDialog(parent, "Dieser Benutzer ist bereits angemeldet.\n\nSollten Sie nicht angemeldet sein, warten Sie\nbitte in etwa 5 Minuten und versuchen Sie\nes dann erneut."); //$NON-NLS-1$
							return;
						}
				else {
					didEntered = false;
					reenable();
					JOptionPane.showMessageDialog(parent, Strings.getString("LoginPanel.PleaseEnterLogin")); //$NON-NLS-1$
					return;
				}
				didEntered = true;
			}
		});
		this.add(submitButton);
		this.abortButton = new JButton(Strings.getString("LoginPanel.Abort")); //$NON-NLS-1$
		this.abortButton.setBounds(280, 150, 100, 30);
		this.abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Console.log(LogType.Information, this, "Abortbutton clicket: going to stop"); //$NON-NLS-1$
				System.exit(0);
			}

			@Override
			public String toString() {
				return "LoginPanel"; //$NON-NLS-1$
			}
		});
		this.add(abortButton);
		this.repaint();
	}

	private void disableInputs() {
		this.username.setEnabled(false);
		this.password.setEnabled(false);
		this.abortButton.setEnabled(false);
		this.submitButton.setEnabled(false);
		this.copyrightLabel.setText(Strings.getString("LoginPanel.LoadDataInfo")); //$NON-NLS-1$
	}

	public void reenable() {
		this.username.setEnabled(true);
		this.password.setEnabled(true);
		this.abortButton.setEnabled(true);
		this.submitButton.setEnabled(true);
		this.copyrightLabel.setText("Copyright (c) Leon Dietrich 2014 - 2015"); //$NON-NLS-1$
	}

	public Hashtable<String, String> getValues(boolean reenable) {
		Hashtable<String, String> v = new Hashtable<String, String>();
		while (!didEntered && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				v.put("broken", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				v.put("Exception", e.toString()); //$NON-NLS-1$
				Thread.currentThread().interrupt();
			}
		}
		v.put("password", new String(this.password.getPassword())); //$NON-NLS-1$
		v.put("user", this.username.getText()); //$NON-NLS-1$
		if (reenable)
			reenable();
		return v;
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		this.submitButton.setVisible(b);
		this.abortButton.setVisible(b);
		this.passLabel.setVisible(b);
		this.password.setVisible(b);
		this.username.setVisible(b);
		this.userLabel.setVisible(b);
	}

	private void loadWorkspace() {
		this.setVisible(false);
		Protocol.collectInformation();
		Thread mst = new Thread(new Runnable() {

			@Override
			public void run() {
				MessageStreamHandler msh = new MessageStreamHandler(Protocol.getMessageHash());
				if (msh.VALID)
					msh.run();
			}

		});
		mst.setDaemon(true);
		mst.setName("CageThread: MessageStreamHandler"); //$NON-NLS-1$
		mst.start();
	}

	public void setParent(JFrame p) {
		this.parent = p;
	}
}
