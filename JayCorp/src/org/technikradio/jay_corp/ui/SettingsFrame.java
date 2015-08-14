package org.technikradio.jay_corp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.helpers.AddUserDialog;
import org.technikradio.jay_corp.ui.helpers.CSVImporter;
import org.technikradio.jay_corp.ui.helpers.DataDownloadProcessor;
import org.technikradio.jay_corp.ui.helpers.PasswordInputDialog;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class SettingsFrame extends JDialog {

	private static final long serialVersionUID = 2879636623484398543L;

	private int initialVValue = 0;

	private JButton addUserButton;
	private JButton addUserBySystem;
	private JButton cancleButton;
	private JButton okButton;
	private JButton changePSWButton;
	private JButton downloadFileButton;
	private JTabbedPane tabBox;
	private JPanel[] pages;
	private JCheckBox enableAccessCheckBox;
	private JTable userTable;
	private JSpinner allowedDaysSelector;
	private MainFrame owner;
	private final SettingsFrame ownHandle = this;

	public SettingsFrame() {
		super();
		setup();
	}

	public SettingsFrame(Frame arg0) {
		super(arg0);
		setup();
	}

	public SettingsFrame(Dialog arg0) {
		super(arg0);
		setup();
	}

	public SettingsFrame(Window arg0) {
		super(arg0);
		setup();
	}

	public SettingsFrame(Frame arg0, boolean arg1) {
		super(arg0, arg1);
		setup();
	}

	public SettingsFrame(Frame arg0, String arg1) {
		super(arg0, arg1);
		setup();
	}

	public SettingsFrame(Dialog arg0, boolean arg1) {
		super(arg0, arg1);
		setup();
	}

	public SettingsFrame(Dialog arg0, String arg1) {
		super(arg0, arg1);
		setup();
	}

	public SettingsFrame(Window arg0, ModalityType arg1) {
		super(arg0, arg1);
		setup();
	}

	public SettingsFrame(Window arg0, String arg1) {
		super(arg0, arg1);
		setup();
	}

	public SettingsFrame(Frame arg0, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		setup();
	}

	public SettingsFrame(Dialog arg0, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		setup();
	}

	public SettingsFrame(Window arg0, String arg1, ModalityType arg2) {
		super(arg0, arg1, arg2);
		setup();
	}

	public SettingsFrame(Frame arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
		setup();
	}

	public SettingsFrame(Dialog arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
		setup();
	}

	public SettingsFrame(Window arg0, String arg1, ModalityType arg2, GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
		setup();
	}

	@Override
	public String toString() {
		return Strings.getString("ErrorMessages.SettingsFrame.name"); //$NON-NLS-1$
	}

	private void setup() {
		this.setResizable(false);
		{
			BoxLayout b = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
			this.setLayout(b);
		}
		tabBox = new JTabbedPane();
		enableAccessCheckBox = new JCheckBox();
		cancleButton = new JButton();
		okButton = new JButton();
		addUserButton = new JButton();
		addUserBySystem = new JButton();
		changePSWButton = new JButton();
		downloadFileButton = new JButton();
		userTable = new JTable();
		pages = new JPanel[2];
		pages[0] = new JPanel();
		pages[1] = new JPanel();
		pages[0].setName(Strings.getString("SettingsFrame.panel_1")); //$NON-NLS-1$
		pages[1].setName(Strings.getString("SettingsFrame.panel_2")); //$NON-NLS-1$
		pages[0].setToolTipText(Strings.getString("SettingsFrame.HeaderGeneralToolTip")); //$NON-NLS-1$
		pages[1].setToolTipText(Strings.getString("SettingsFrame.HeaderUserToolTip")); //$NON-NLS-1$
		pages[0].setLayout(new BoxLayout(pages[0], BoxLayout.PAGE_AXIS));
		cancleButton.setText(Strings.getString("SettingsFrame.CancleButton")); //$NON-NLS-1$
		cancleButton.setToolTipText(Strings.getString("SettingsFrame.CancleButtonToolTip")); //$NON-NLS-1$
		okButton.setText(Strings.getString("SettingsFrame.OKButton")); //$NON-NLS-1$
		okButton.setToolTipText(Strings.getString("SettingsFrame.OKButtonToolTip")); //$NON-NLS-1$
		addUserButton.setText(Strings.getString("SettingsFrame.AddUserButton")); //$NON-NLS-1$
		addUserBySystem.setText(Strings.getString("SettingsFrame.LoadUserFileButton")); //$NON-NLS-1$
		okButton.setSize(100, 25);
		cancleButton.setSize(100, 25);
		okButton.setPreferredSize(okButton.getSize());
		cancleButton.setPreferredSize(cancleButton.getSize());
		enableAccessCheckBox.setText(Strings.getString("SettingsFrame.openEditEnabled")); //$NON-NLS-1$
		try {
			enableAccessCheckBox.setEnabled(Protocol.getCurrentUser().getRights().isOpenCloseEditAllowed());
		} catch (NullPointerException e) {
			Console.log(LogType.Error, this, Strings.getString("ErrorMessages.SettingsFrame.NullData")); //$NON-NLS-1$
			enableAccessCheckBox.setEnabled(false);
		}
		pages[0].add(enableAccessCheckBox);
		{
			changePSWButton.setText(Strings.getString("SettingsFrame.ChangePasswordText")); //$NON-NLS-1$
			changePSWButton.setToolTipText(Strings.getString("SettingsFrame.ChangePasswordToolTip")); //$NON-NLS-1$
			downloadFileButton.setText(Strings.getString("SettingsFrame.DownloadSelectionFile")); //$NON-NLS-1$
			downloadFileButton.setToolTipText(Strings.getString("SettingsFrame.DownloadSelectionFileToolTip")); //$NON-NLS-1$
			changePSWButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							Console.log(LogType.StdOut, ownHandle, "Change password button pressed"); //$NON-NLS-1$
							String newPSWD = PasswordInputDialog.showDialog(ownHandle, "", //$NON-NLS-1$
									Strings.getString("SettingsFrame.ChangePassword")); //$NON-NLS-1$
							boolean success = false;
							if (newPSWD != null)
								success = Protocol.changePassword(newPSWD, Protocol.getCurrentUser().getID());
							if (success) {
								Protocol.getCurrentUser().setPassword(newPSWD);
							}
							Console.log(LogType.StdOut, ownHandle,
									"Successfull password change: " + Boolean.toString(success)); //$NON-NLS-1$
						}
					});
					t.setName("PasswordWaiterThread"); //$NON-NLS-1$
					t.start();
				}
			});
			downloadFileButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							Console.log(LogType.Information, this, "Downloading userfile"); //$NON-NLS-1$
							ownHandle.setVisible(false);
							new DataDownloadProcessor(owner).download();
							ownHandle.setVisible(true);
						}
					});
					t.setName("DownloadWaiterThread"); //$NON-NLS-1$
					t.start();

				}
			});
			pages[0].add(changePSWButton);
			pages[0].add(downloadFileButton);
		}
		addUserBySystem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						new CSVImporter(pages[1]).upload();
					}
				});
				t.setName("UploadWaiterThread"); //$NON-NLS-1$
				t.start();

			}
		});
		{
			JPanel cp = new JPanel();
			JPanel bp = new JPanel();
			bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));
			cp.setLayout(new BorderLayout());
			JScrollPane p = new JScrollPane();
			p.add(userTable);
			Dimension d = new Dimension();
			d.setSize(630, 370);
			p.setPreferredSize(d);
			p.setMaximumSize(d);
			p.setMinimumSize(d);
			cp.add(userTable.getTableHeader(), BorderLayout.PAGE_START);
			cp.add(p, BorderLayout.CENTER);
			addUserButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// ownHandle.setVisible(false);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						Console.log(LogType.Error, ownHandle, "Waiting for UI sync interrupted:");
						e1.printStackTrace();
					}
					AddUserDialog a = new AddUserDialog(ownHandle);
					a.setVisible(true);
				}
			});
			bp.add(addUserButton);
			bp.add(addUserBySystem);
			{
				JLabel userLabel = new JLabel();
				userLabel.setText(Strings.getString("SettingsFrame.UserLabel") + Protocol.getCurrentUser().getID()); //$NON-NLS-1$
				userLabel.setToolTipText(Strings.getString("SettingsFrame.UserLabelToolTip")); //$NON-NLS-1$
				bp.add(userLabel);
			}
			cp.add(bp, BorderLayout.PAGE_END);
			pages[1].add(cp);
		}
		for (JPanel p : pages) {
			tabBox.addTab(p.getName(), p);
		}
		this.setSize(660, 500);
		this.setTitle(Strings.getString("SettingsFrame.TitleLoading")); //$NON-NLS-1$
		{
			JPanel jc = new JPanel();
			jc.add(tabBox);
			Dimension d = new Dimension();
			d.setSize(this.getWidth(), this.getHeight() - 35);
			jc.setMaximumSize(d);
			this.getContentPane().add(jc);
		}
		this.setSize(660, 550);
		{
			JPanel jc = new JPanel();
			jc.setLayout(new BoxLayout(jc, BoxLayout.X_AXIS));
			jc.add(cancleButton);
			jc.add(okButton);
			this.getContentPane().add(jc);
		}
		{
			okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					pushSettings();
					ownHandle.setVisible(false);
					ownHandle.dispose();
				}
			});
			cancleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ownHandle.setVisible(false);
					ownHandle.dispose();
				}
			});
		}
		{
			allowedDaysSelector = new JSpinner();
			allowedDaysSelector.setModel(new SpinnerNumberModel(Protocol.getCurrentUser().getExtraDays(), 0, 366, 1));
			initialVValue = Protocol.getCurrentUser().getExtraDays();
			allowedDaysSelector.setToolTipText(Strings.getString("SettingsFrame.AllowedDaysToolTip")); //$NON-NLS-1$
			JLabel hintLabel = new JLabel(Strings.getString("SettingsFrame.AllowedDaysText")); //$NON-NLS-1$
			JPanel containerLabel = new JPanel();
			containerLabel.setLayout(new FlowLayout());
			containerLabel.add(hintLabel);
			containerLabel.add(allowedDaysSelector);
			pages[0].add(containerLabel);
			if (Protocol.getCurrentUser().getID() == 0) {
				containerLabel.setEnabled(false);
				hintLabel.setEnabled(false);
				allowedDaysSelector.setEnabled(false);
			}
		}

		// Load the data
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				{
					enableAccessCheckBox.setEnabled(Protocol.getCurrentUser().getRights().isOpenCloseEditAllowed());
					enableAccessCheckBox.setSelected(Protocol.isEditEnabled());
					addUserButton.setEnabled(Protocol.getCurrentUser().getRights().isAddUserAllowed());
					addUserBySystem.setEnabled(Protocol.getCurrentUser().getRights().isAddUserAllowed());
					downloadFileButton.setEnabled(Protocol.getCurrentUser().getRights().isViewOtherSelectionsAllowed()
							&& Protocol.getCurrentUser().getRights().isGetIDCountAllowed()
							&& Protocol.getCurrentUser().getRights().isListAllUsersAllowed()
							&& Protocol.getCurrentUser().getRights().isAccessUserInputAllowed());
				}
				try {
					if (Protocol.getCurrentUser().getRights().isListAllUsersAllowed()
							&& Protocol.getCurrentUser().getRights().isEditUserAllowed()) {
						String[] tableNames = { Strings.getString("SettingsFrame.UserNameHeader"), //$NON-NLS-1$
								Strings.getString("SettingsFrame.FullNameHeader"), //$NON-NLS-1$
								Strings.getString("SettingsFrame.RightsHeader"), //$NON-NLS-1$
								Strings.getString("SettingsFrame.AgeHeader"), //$NON-NLS-1$
								Strings.getString("SettingsFrame.IDHeader") }; //$NON-NLS-1$
						ArrayList<User> users = new ArrayList<User>();
						int ids[] = Protocol.getUsers();
						for (int id : ids) {
							try {
								User u = Protocol.getUser(id);
								u.setRights(Protocol.getRights(id));
								users.add(u);
							} catch (Exception e) {
								Console.log(LogType.Error, this, "An unknown error occured"); //$NON-NLS-1$
								e.printStackTrace();
							}
						}
						for (int i = 0; i < tableNames.length; i++) {
							TableColumn c = new TableColumn();
							c.setHeaderValue(tableNames[i]);
							c.setMinWidth(125);
							c.setResizable(true);
							userTable.getColumnModel().addColumn(c);
						}
						DefaultTableModel dtm = (DefaultTableModel) userTable.getModel();
						for (User u : users) {
							String[] data = new String[5];
							data[0] = u.getUsername();
							data[1] = u.getName();
							data[2] = Byte.toString(RightEditFrame.getRight(u.getRights()));
							data[3] = Integer.toString(u.getWorkAge());
							data[4] = Integer.toString(u.getID());
							Console.log(LogType.StdOut, this, "Add user '" + u.getName() + "' to table"); //$NON-NLS-1$ //$NON-NLS-2$
							dtm.addRow(data);
						}
						userTable.setModel(dtm);
						userTable.setSize(50, 50);
						userTable.setBackground(Color.black);
						userTable.repaint();
					}
					Console.log(LogType.StdOut, Strings.getString("ErrorMessages.SettingsFrame.name"), //$NON-NLS-1$
							"Successfully loaded the data"); //$NON-NLS-1$
				} catch (Exception e) {
					Console.log(LogType.Error, this, "An unknown exception occured while loading the data: "); //$NON-NLS-1$
					e.printStackTrace();
				}
				ownHandle.setTitle(Strings.getString("SettingsFrame.Title")); //$NON-NLS-1$
			}
		});
		t.setPriority(Thread.MAX_PRIORITY);
		t.setName("SettingsDataLoadThread"); //$NON-NLS-1$
		t.start();
		userTable.setVisible(false);

		repaint();
	}

	private void pushSettings() {
		if (enableAccessCheckBox.isSelected() != Protocol.isEditEnabled() && enableAccessCheckBox.isEnabled()) {
			// set EDIT_ENABLED_FLAG
			Protocol.setEditEnableOnServer(enableAccessCheckBox.isSelected());
		}
		if (Protocol.getCurrentUser().getID() != 0) {
			SpinnerNumberModel model = (SpinnerNumberModel) allowedDaysSelector.getModel();
			if (model.getNumber().intValue() != 0 && model.getNumber().intValue() != initialVValue) {
				Protocol.changeExtraDays(model.getNumber().intValue(), Protocol.getCurrentUser().getID());
			}
		}
		Console.log(LogType.StdOut, this, "Successfully transmitted settings"); //$NON-NLS-1$
		owner.updateState();
	}

	public void setOwner(MainFrame mf) {
		this.owner = mf;
		int x = mf.getLocation().x + ((mf.getWidth() / 2) - this.getWidth() / 2);
		int y = mf.getLocation().y + ((mf.getHeight() / 2) - this.getHeight() / 2);
		this.setLocation(x, y);
	}

}
