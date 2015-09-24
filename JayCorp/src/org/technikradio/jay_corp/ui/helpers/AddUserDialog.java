package org.technikradio.jay_corp.ui.helpers;

import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.RightEditFrame;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.Righttable;
import org.technikradio.jay_corp.user.User;

public class AddUserDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6596856982871102108L;
	private final AddUserDialog ownHandle = this;
	private JTextField nameField;
	private JTextField userNameField;
	private JSpinner freeDaysSpinner;
	private JButton passwordButton;
	private JButton rightsButton;
	private JButton okButton;
	private JButton abortButton;
	private String password;
	private Righttable rights;

	public AddUserDialog() {
		setup();
	}

	private void setup() {
		this.setTitle(Strings.getString("AddUserDialog.Title")); //$NON-NLS-1$
		this.setResizable(false);
		this.setSize(300, 300);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		if (this.getOwner() != null) {
			Window mf = this.getOwner();
			int x = mf.getLocation().x + ((mf.getWidth() / 2) - this.getWidth() / 2);
			int y = mf.getLocation().y + ((mf.getHeight() / 2) - this.getHeight() / 2);
			this.setLocation(x, y);
		}
		rights = new Righttable();
		{
			// Name field
			JPanel panel = new JPanel();
			JLabel label = new JLabel();
			label.setText(Strings.getString("AddUserDialog.FullNameLabel")); //$NON-NLS-1$
			panel.setLayout(null);
			panel.setName("NameFieldPanel"); //$NON-NLS-1$
			panel.setSize(new Dimension(this.getWidth(), 30));
			// panel.setBackground(Color.RED);
			label.setLocation(5, 10);
			label.setSize(label.getPreferredSize());
			panel.add(label);
			nameField = new JTextField();
			nameField.setLocation(10 + label.getWidth(), 5);
			nameField.setSize(200, 30);
			nameField.setToolTipText(Strings.getString("AddUserDialog.FullNameToolTip")); //$NON-NLS-1$
			/*
			 * System.out.println(nameField.getWidth() + " " +
			 * nameField.getHeight() + " " + nameField.getX() + " " +
			 * nameField.getY());
			 */
			nameField.repaint();
			panel.add(nameField);
			this.add(panel);
		}
		{
			// Username field
			JPanel panel = new JPanel();
			JLabel label = new JLabel();
			label.setText(Strings.getString("AddUserDialog.UserNameLabel")); //$NON-NLS-1$
			panel.setLayout(null);
			panel.setName("UserNameFieldPanel"); //$NON-NLS-1$
			panel.setSize(new Dimension(this.getWidth(), 30));
			label.setLocation(5, 10);
			label.setSize(label.getPreferredSize());
			panel.add(label);
			userNameField = new JTextField();
			userNameField.setLocation(10 + label.getWidth(), 5);
			userNameField.setSize(133, 30);
			userNameField.setToolTipText(Strings.getString("AddUserDialog.UserNameToolTip")); //$NON-NLS-1$
			userNameField.repaint();
			panel.add(userNameField);
			this.add(panel);
		}
		{
			// Free days field
			JPanel panel = new JPanel();
			JLabel label = new JLabel();
			label.setText(Strings.getString("AddUserDialog.FreeDaysLabel")); //$NON-NLS-1$
			panel.setLayout(null);
			panel.setName("FreeDaysSpinnerPanel"); //$NON-NLS-1$
			panel.setSize(new Dimension(this.getWidth(), 30));
			label.setLocation(5, 10);
			label.setSize(label.getPreferredSize());
			panel.add(label);
			freeDaysSpinner = new JSpinner();
			freeDaysSpinner.setModel(new SpinnerNumberModel(1, 0, 366, 1));
			freeDaysSpinner.setLocation(10 + label.getWidth(), 5);
			freeDaysSpinner.setSize(170, 30);
			freeDaysSpinner.setToolTipText(
					Strings.getString("AddUserDialog.FreeDaysToolTip")); //$NON-NLS-1$
			freeDaysSpinner.repaint();
			panel.add(freeDaysSpinner);
			this.add(panel);
		}
		{
			// Free days field
			JPanel panel = new JPanel();
			JLabel label = new JLabel();
			label.setText(Strings.getString("AddUserDialog.PasswordLabel")); //$NON-NLS-1$
			panel.setLayout(null);
			panel.setName("PasswordButtonPanel"); //$NON-NLS-1$
			panel.setSize(new Dimension(this.getWidth(), 30));
			label.setLocation(5, 10);
			label.setSize(label.getPreferredSize());
			panel.add(label);
			passwordButton = new JButton();
			passwordButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							password = PasswordInputDialog.showDialog(ownHandle, "", Strings.getString("AddUserDialog.PasswordTitle")); //$NON-NLS-1$ //$NON-NLS-2$
						}
					});
					t.setPriority(2);
					t.setName("UIWaiterThread"); //$NON-NLS-1$
					t.start();
				}
			});
			passwordButton.setText(Strings.getString("AddUserDialog.PasswordButtonLabel")); //$NON-NLS-1$
			passwordButton.setToolTipText(
					Strings.getString("AddUserDialog.PasswortToolTip") //$NON-NLS-1$
							+ Strings.getString("CSVImporter.DefaultNewPassword") //$NON-NLS-1$
							+ Strings.getString("AddUserDialog.PasswordToolTipEnd")); //$NON-NLS-1$
			passwordButton.setLocation(10 + label.getWidth(), 5);
			passwordButton.setSize(160, 30);
			passwordButton.repaint();
			panel.add(passwordButton);
			this.add(panel);
		}
		{
			// Free days field
			JPanel panel = new JPanel();
			JLabel label = new JLabel();
			label.setText(Strings.getString("AddUserDialog.RightsLabel")); //$NON-NLS-1$
			panel.setLayout(null);
			panel.setName("RightsButtonPanel"); //$NON-NLS-1$
			panel.setSize(new Dimension(this.getWidth(), 30));
			label.setLocation(5, 10);
			label.setSize(label.getPreferredSize());
			panel.add(label);
			rightsButton = new JButton();
			rightsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							RightEditFrame.showDialog(rights);
						}
					});
					t.setPriority(2);
					t.setName("UIWaiterThread"); //$NON-NLS-1$
					t.start();
				}
			});
			rightsButton.setText(Strings.getString("AddUserDialog.RightButtonText")); //$NON-NLS-1$
			rightsButton
					.setToolTipText(Strings.getString("AddUserDialog.RightButtonToolTip")); //$NON-NLS-1$
			rightsButton.setLocation(10 + label.getWidth(), 5);
			rightsButton.setSize(177, 30);
			rightsButton.repaint();
			panel.add(rightsButton);
			this.add(panel);
		}
		{
			// Free days field
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout());
			((FlowLayout) panel.getLayout()).setAlignment(FlowLayout.TRAILING);
			panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			panel.setName("CloseButtonsPanel"); //$NON-NLS-1$
			panel.setSize(new Dimension(this.getWidth(), 20));
			abortButton = new JButton();
			abortButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							ownHandle.setVisible(false);
						}
					});
					t.setPriority(2);
					t.setName("UIWaiterThread"); //$NON-NLS-1$
					t.start();
				}
			});
			abortButton.setText(Strings.getString("AddUserDialog.AbortButton")); //$NON-NLS-1$
			abortButton.setToolTipText(
					Strings.getString("AddUserDialog.AbortToolTip")); //$NON-NLS-1$
			abortButton.repaint();
			panel.add(abortButton);
			okButton = new JButton();
			okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							if (nameField.getText() == null || nameField.getText().equals("")) { //$NON-NLS-1$
								JOptionPane.showMessageDialog(ownHandle, Strings.getString("AddUserDialog.EnterNameHint"), //$NON-NLS-1$
										Strings.getString("AddUserDialog.NameNotSetTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
								return;
							}
							if (userNameField.getText() == null || userNameField.getText().equals("")) { //$NON-NLS-1$
								JOptionPane.showMessageDialog(ownHandle,
										Strings.getString("AddUserDialog.EnterUserNameHint"), //$NON-NLS-1$
										Strings.getString("AddUserDialog.UserNameNotSetTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
								return;
							}
							if (password == null || password.equals("")) { //$NON-NLS-1$
								JOptionPane.showMessageDialog(ownHandle,
										Strings.getString("AddUserDialog.EnterPasswordHint") //$NON-NLS-1$
												+ Strings.getString("CSVImporter.DefaultNewPassword") //$NON-NLS-1$
												+ Strings.getString("AddUserDialog.EnterPasswordHintEnd"), //$NON-NLS-1$
										Strings.getString("AddUserDialog.PasswordNotSetTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
								return;
							}
							ownHandle.setTitle(ownHandle.getTitle() + Strings.getString("AddUserDialog.TransMitDataHint")); //$NON-NLS-1$
							ownHandle.setEnabled(false);
							User u = new User();
							u.setName(nameField.getText());
							SpinnerNumberModel model = (SpinnerNumberModel) freeDaysSpinner.getModel();
							u.setExtraDays(model.getNumber().intValue());
							u.setPassword(password);
							u.setSelectedDays(new DayTable());
							u.setUsername(userNameField.getText());
							u.setWorkAge(1);
							u.setID(Protocol.getIDCount() + 1);
							u.setRights(rights);
							Protocol.addUser(u);
							Protocol.save();
							ownHandle.setVisible(false);
						}
					});
					t.setPriority(2);
					t.setName("UIWaiterThread"); //$NON-NLS-1$
					t.start();
				}
			});
			okButton.setText(Strings.getString("AddUserDialog.OKButtonText")); //$NON-NLS-1$
			okButton.setToolTipText(
					Strings.getString("AddUserDialog.OKButtonToolTip")); //$NON-NLS-1$
			okButton.repaint();
			panel.add(okButton);
			this.add(panel);
		}
	}

	public AddUserDialog(Frame owner) {
		super(owner);
		setup();
	}

	public AddUserDialog(Dialog owner) {
		super(owner);
		setup();
	}

	public AddUserDialog(Window owner) {
		super(owner);
		setup();
	}

	public AddUserDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public AddUserDialog(Frame owner, String title) {
		super(owner, title);
		setup();
	}

	public AddUserDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public AddUserDialog(Dialog owner, String title) {
		super(owner, title);
		setup();
	}

	public AddUserDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup();
	}

	public AddUserDialog(Window owner, String title) {
		super(owner, title);
		setup();
	}

	public AddUserDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public AddUserDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public AddUserDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setup();
	}

	public AddUserDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public AddUserDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public AddUserDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup();
	}

}
