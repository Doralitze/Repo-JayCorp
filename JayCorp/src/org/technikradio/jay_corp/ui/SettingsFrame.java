/*
JayCorp-Client/SettingsFrame.java
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
package org.technikradio.jay_corp.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.technikradio.jay_corp.JayCorp;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.helpers.AddUserDialog;
import org.technikradio.jay_corp.ui.helpers.CSVImporter;
import org.technikradio.jay_corp.ui.helpers.DataDownloadProcessor;
import org.technikradio.jay_corp.ui.helpers.PasswordInputDialog;
import org.technikradio.jay_corp.ui.helpers.ProgressIndicator;
import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.PermissionDeninedException;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class SettingsFrame extends JDialog {

	private static final long serialVersionUID = 2879636623484398543L;

	private int initialVValue = 0;
	private boolean mustSave = false;

	private JButton addUserButton;
	private JButton addUserBySystemButton;
	private JButton cancleButton;
	private JButton okButton;
	private JButton changePSWButton;
	private JButton downloadFileButton;
	private JButton destroyDBButton;
	private JButton openUserFrameButton;
	private JTabbedPane tabBox;
	private JPanel[] pages;
	private JCheckBox enableAccessCheckBox;
	private JSpinner allowedDaysSelector;
	private JProgressBar dataloadProgressBar;
	private MainFrame owner;
	private final DataLoader dl = new DataLoader();
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
		addUserBySystemButton = new JButton();
		changePSWButton = new JButton();
		downloadFileButton = new JButton();
		destroyDBButton = new JButton();
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
		addUserBySystemButton.setText(Strings.getString("SettingsFrame.LoadUserFileButton")); //$NON-NLS-1$
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
		pages[0].add(Box.createRigidArea(new Dimension(15, 15)));
		pages[0].add(enableAccessCheckBox);
		{
			changePSWButton.setText(Strings.getString("SettingsFrame.ChangePasswordText")); //$NON-NLS-1$
			changePSWButton.setToolTipText(Strings.getString("SettingsFrame.ChangePasswordToolTip")); //$NON-NLS-1$
			downloadFileButton.setText(Strings.getString("SettingsFrame.DownloadSelectionFile")); //$NON-NLS-1$
			downloadFileButton.setToolTipText(Strings.getString("SettingsFrame.DownloadSelectionFileToolTip")); //$NON-NLS-1$
			destroyDBButton.setText(Strings.getString("SettingsFrame.ResetDatabaseButton")); //$NON-NLS-1$
			destroyDBButton.setToolTipText(Strings.getString("SettingsFrame.ResetDatabaseToolTip")); //$NON-NLS-1$
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
			destroyDBButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// delete database...
					int result = JOptionPane.showConfirmDialog(null,
							Strings.getString("SettingsFrame.ResetDatabaseQuestion"), //$NON-NLS-1$
							Strings.getString("SettingsFrame.ResetDatabaseQuestionHeader"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (result == JOptionPane.YES_OPTION)
						if (Protocol.getCurrentUser().getID() == 0)
							if (Protocol.destroyDatabase()) {
								JOptionPane.showMessageDialog(null,
										Strings.getString("SettingsFrame.DatabaseSuccessfulDelete"), //$NON-NLS-1$
										Strings.getString("SettingsFrame.DatabaseSuccessfulDeleteHeader"), //$NON-NLS-1$
										JOptionPane.INFORMATION_MESSAGE);
								Protocol.disconnect();
								JayCorp.exit(0, true);
							} else
								JOptionPane.showMessageDialog(null,
										Strings.getString("SettingsFrame.DatabaseResetFail"), //$NON-NLS-1$
										Strings.getString("SettingsFrame.DatabaseResetFailHeader"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
						else
							JOptionPane.showMessageDialog(null,
									Strings.getString("SettingsFrame.DatabaseResetPermissionDenined"), //$NON-NLS-1$
									Strings.getString("SettingsFrame.DatabaseResetPermissionDeninedHeader"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);

				}
			});
			pages[0].add(Box.createRigidArea(new Dimension(15, 15)));
			pages[0].add(changePSWButton);
			{
				JSeparator js = new JSeparator(SwingConstants.HORIZONTAL);
				js.setBorder(new EmptyBorder(10, 10, 10, 10));
				pages[0].add(Box.createRigidArea(new Dimension(15, 15)));
				pages[0].add(js);
			}
			pages[0].add(Box.createRigidArea(new Dimension(15, 15)));
			pages[0].add(downloadFileButton);
			pages[0].add(Box.createRigidArea(new Dimension(15, 15)));
			pages[0].add(destroyDBButton);
		}
		addUserBySystemButton.addActionListener(new ActionListener() {

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
				mustSave = true;
			}
		});
		{
			JPanel cp = new JPanel();
			JPanel bp = new JPanel();
			bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));
			cp.setLayout(new BorderLayout(10, 10));
			Dimension d = new Dimension();
			d.setSize(630, 370);
			{
				openUserFrameButton = new JButton("Benutzertabelle öffnen");
				openUserFrameButton.setEnabled(false);
				openUserFrameButton.setToolTipText("Hiermit öffnen Sie die Benutzertabelle.");
				openUserFrameButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dl.show();

					}
				});
				cp.add(openUserFrameButton, BorderLayout.CENTER);
				dataloadProgressBar = new JProgressBar();
				cp.add(dataloadProgressBar, BorderLayout.PAGE_START);
			}
			addUserButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// ownHandle.setVisible(false);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						Console.log(LogType.Error, ownHandle, "Waiting for UI sync interrupted:"); //$NON-NLS-1$
						e1.printStackTrace();
					}
					AddUserDialog a = new AddUserDialog(ownHandle);
					a.setVisible(true);
					mustSave = true;
				}
			});
			// bp.add(Box.createRigidArea(new Dimension(15,15)));
			bp.add(addUserButton);
			// bp.add(Box.createRigidArea(new Dimension(15,15)));
			bp.add(addUserBySystemButton);
			{
				JLabel userLabel = new JLabel();
				userLabel.setText(Strings.getString("SettingsFrame.UserLabel") + Protocol.getCurrentUser().getID()); //$NON-NLS-1$
				userLabel.setToolTipText(Strings.getString("SettingsFrame.UserLabelToolTip")); //$NON-NLS-1$
				bp.add(userLabel);
			}
			// cp.add(Box.createRigidArea(new Dimension(15,15)));
			cp.add(bp, BorderLayout.PAGE_END);
			// pages[1].add(Box.createRigidArea(new Dimension(15,15)));
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
			this.getContentPane().add(Box.createRigidArea(new Dimension(15, 15)));
			this.getContentPane().add(jc);
		}
		this.setSize(660, 550);
		{
			JPanel jc = new JPanel();
			jc.setLayout(new BoxLayout(jc, BoxLayout.X_AXIS));
			jc.add(Box.createRigidArea(new Dimension(15, 15)));
			jc.add(cancleButton);
			jc.add(Box.createRigidArea(new Dimension(15, 15)));
			jc.add(okButton);
			jc.add(Box.createRigidArea(new Dimension(15, 15)));
			this.getContentPane().add(Box.createRigidArea(new Dimension(15, 15)));
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
			containerLabel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
			containerLabel.add(Box.createRigidArea(new Dimension(15, 15)));
			containerLabel.add(hintLabel);
			{
				JSeparator js = new JSeparator(SwingConstants.HORIZONTAL);
				js.setBorder(new EmptyBorder(10, 10, 10, 10));
				containerLabel.add(Box.createRigidArea(new Dimension(15, 15)));
				containerLabel.add(js);
			}
			containerLabel.add(Box.createRigidArea(new Dimension(15, 15)));
			containerLabel.add(allowedDaysSelector);
			pages[0].add(Box.createRigidArea(new Dimension(15, 15)));
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
					addUserBySystemButton.setEnabled(Protocol.getCurrentUser().getRights().isAddUserAllowed());
					downloadFileButton.setEnabled(Protocol.getCurrentUser().getRights().isViewOtherSelectionsAllowed()
							&& Protocol.getCurrentUser().getRights().isGetIDCountAllowed()
							&& Protocol.getCurrentUser().getRights().isListAllUsersAllowed()
							&& Protocol.getCurrentUser().getRights().isAccessUserInputAllowed());
					destroyDBButton.setEnabled(Protocol.getCurrentUser().getID() == 0);

				}
				try {
					if (Protocol.getCurrentUser().getRights().isListAllUsersAllowed()
							&& Protocol.getCurrentUser().getRights().isEditUserAllowed()) {
						dl.start();
						openUserFrameButton.setEnabled(true);
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
		{
			// Add paddings to layout
			addUserButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			addUserBySystemButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			cancleButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			okButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			changePSWButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			downloadFileButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			destroyDBButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			openUserFrameButton.setBorder(new EmptyBorder(5, 10, 5, 10));
			enableAccessCheckBox.setBorder(new EmptyBorder(10, 10, 10, 10));
			allowedDaysSelector.setBorder(new EmptyBorder(5, 10, 5, 5));
			dataloadProgressBar.setBorder(new EmptyBorder(10, 10, 10, 10));

		}
		repaint();
	}

	private class DataLoader {

		private Thread loadingThread;
		private boolean running = true;
		private boolean done = false;
		private int max = 0;
		private int vPos = 0;
		private String currentUser = "";
		private final String[] tableNames = { Strings.getString("SettingsFrame.UserNameHeader"), //$NON-NLS-1$
				Strings.getString("SettingsFrame.FullNameHeader"), //$NON-NLS-1$
				Strings.getString("SettingsFrame.RightsHeader"), //$NON-NLS-1$
				Strings.getString("SettingsFrame.AgeHeader"), //$NON-NLS-1$
				Strings.getString("SettingsFrame.IDHeader"), //$NON-NLS-1$
				Strings.getString("SettingsFrame.AviableDays"), //$NON-NLS-1$
				Strings.getString("SettingsFrame.SelectedDays") }; //$NON-NLS-1$
		private String[][] data;
		private UserWatchFrame frame;

		public DataLoader() {
			loadingThread = new Thread(new Runnable() {

				@Override
				public void run() {

					// Alle Benutzer laden
					ArrayList<User> users = new ArrayList<User>();
					int ids[] = Protocol.getUsers();
					max = ids.length;
					vPos = 0;
					dataloadProgressBar.setMaximum(max);
					dataloadProgressBar.setValue(0);
					dataloadProgressBar.setMinimum(0);
					for (int id : ids) {
						vPos++;
						dataloadProgressBar.setValue(vPos);
						try {
							if (!running)
								return;
							User u = Protocol.getUser(id);
							u.setRights(Protocol.getRights(id));
							users.add(u);
							currentUser = u.getName();
						} catch (Exception e) {
							Console.log(LogType.Error, this, "An unknown error occured"); //$NON-NLS-1$
							e.printStackTrace();
						}
					}

					// Daten eintragen
					String[][] localData = new String[users.size()][tableNames.length];
					for (int i = 0; i < users.size(); i++) {
						User u = users.get(i);
						String[] mdata = new String[tableNames.length];
						mdata[0] = u.getUsername();
						mdata[1] = u.getName();
						mdata[2] = Byte.toString(RightEditFrame.getRight(u.getRights()));
						mdata[3] = Integer.toString(u.getWorkAge());
						mdata[4] = Integer.toString(u.getID());
						mdata[5] = Integer.toString(u.getExtraDays());
						if (Protocol.getCurrentUser().getRights().isAccessUserInputAllowed()) {
							try {
								DayTable sd = Protocol.getProgress(u.getID());
								mdata[6] = Integer.toString(sd.countDays());
							} catch (IOException | PermissionDeninedException e) {
								Console.log(LogType.Error, ownHandle,
										"This shouldn't happen due to the fact that it's checked:");
								e.printStackTrace();
							}
						} else {
							mdata[6] = "<<No Data>>";
						}
						Console.log(LogType.StdOut, this, "Add user '" + u.getName() + "' to table"); //$NON-NLS-1$ //$NON-NLS-2$
						localData[i] = mdata;
					}
					data = localData;
					dataloadProgressBar.setVisible(false);
					done = true;
				}
			});
			loadingThread.setName("UserTableLoadingThread");
		}

		public void start() {
			running = true;
			loadingThread.start();
		}

		public void stop() {
			running = false;
		}

		public void show() {
			Thread showDialogThread = new Thread(new Runnable() {

				@Override
				public void run() {
					ProgressIndicator e = new ProgressIndicator(ownHandle);
					if (!done) {
						e.setVisible(true);
					}
					while (!done) {
						try {
							e.setValv(0, max, vPos);
							e.setInfoLabelText(currentUser);
							Thread.sleep(1);
						} catch (InterruptedException e1) {
							Console.log(LogType.Warning, ownHandle, "UI Thread interrupted");
							e1.printStackTrace();
						}
					}
					e.setVisible(false);
					e = null;
					frame = new UserWatchFrame(tableNames, data, ownHandle);
					frame.setVisible(true);
				}
			});
			showDialogThread.setName("UserDialogWaitThread");
			showDialogThread.start();
		}
	}

	private void pushSettings() {
		dl.stop();
		this.setEnabled(false);
		this.setTitle("Aktualisiere Einstellungen... Bitte warten...");
		boolean savereq = false;
		if (enableAccessCheckBox.isSelected() != Protocol.isEditEnabled() && enableAccessCheckBox.isEnabled()) {
			// set EDIT_ENABLED_FLAG
			Protocol.setEditEnableOnServer(enableAccessCheckBox.isSelected());
			savereq = true;
		}
		if (mustSave)
			savereq = true;
		if (Protocol.getCurrentUser().getID() != 0) {
			SpinnerNumberModel model = (SpinnerNumberModel) allowedDaysSelector.getModel();
			if (model.getNumber().intValue() != 0 && model.getNumber().intValue() != initialVValue) {
				Protocol.changeExtraDays(model.getNumber().intValue(), Protocol.getCurrentUser().getID());
			}
		}
		Console.log(LogType.StdOut, this, "Successfully transmitted settings"); //$NON-NLS-1$
		owner.updateState(savereq);
	}

	public void setOwner(MainFrame mf) {
		this.owner = mf;
		int x = mf.getLocation().x + ((mf.getWidth() / 2) - this.getWidth() / 2);
		int y = mf.getLocation().y + ((mf.getHeight() / 2) - this.getHeight() / 2);
		this.setLocation(x, y);
	}
}
