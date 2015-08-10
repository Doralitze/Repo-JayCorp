package org.technikradio.jay_corp.ui;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.technikradio.jay_corp.JayCorp;
import org.technikradio.jay_corp.ProgressChangedNotifier;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.user.PermissionDeninedException;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -7376675328576164082L;

	private JMenuBar menuStrip;
	private Calendar c;
	private final MainFrame ownHandle = this;

	public void setup() {
		{
			int posy, posx, width, height;
			Insets s = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
			posx = 0 + s.left;
			posy = 0 + s.top;
			width = Toolkit.getDefaultToolkit().getScreenSize().width - s.right;
			height = Toolkit.getDefaultToolkit().getScreenSize().height - s.bottom;
			super.setBounds(new Rectangle(posx, posy, width, height));
		}
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		{
			menuStrip = new JMenuBar();
			{
				JMenu fileMenu = new JMenu();
				fileMenu.setText(Strings.getString("MainFrame.MainFrame.Menu.Manage.Name")); //$NON-NLS-1$
				fileMenu.setName("file-menu"); //$NON-NLS-1$
				{
					JMenuItem saveItem = new JMenuItem();
					saveItem.setText(Strings.getString("MainFrame.FileMenu.Save")); //$NON-NLS-1$
					saveItem.setName("file-menu:save"); //$NON-NLS-1$
					saveItem.setToolTipText(Strings.getString("MainFrame.SaveItemToolTip")); //$NON-NLS-1$
					saveItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							Thread t = new Thread(new Runnable() {

								@Override
								public void run() {
									c.setInfoMessage(Strings.getString("MainFrame.SaveData")); //$NON-NLS-1$

									boolean successfullBackup = Protocol
											.moveToBackup(Protocol.getCurrentUser().getID());
									if (successfullBackup)
										Protocol.rmDatabaseEntries(Protocol.getCurrentUser().getID());

									if (Protocol.transmitTable(c.buildFromCache(), Protocol.getCurrentUser().getID(),
											new ProgressChangedNotifier() {

										@Override
										public void progressChanged(int min, int max, int current) {

											c.setInfoMessage(Strings.getString("MainFrame.SaveData") + ": " + current //$NON-NLS-1$ //$NON-NLS-2$
													+ "/" + max); //$NON-NLS-1$

										}
									})) {
										Console.log(LogType.StdOut, this, "Successfully transmitted Data"); //$NON-NLS-1$
										c.setChanged(false);
										c.setInfoMessage(""); //$NON-NLS-1$
									}

									else {
										Console.log(LogType.StdOut, this, "Failed to transmitt Data"); //$NON-NLS-1$
										c.setChanged(true);
										c.setInfoMessage(Strings.getString("MainFrame.TransmitFailMessage")); //$NON-NLS-1$
									}
									Protocol.save();
								}
							});
							t.setName("TransmitDataThread"); //$NON-NLS-1$
							t.setPriority(Thread.MAX_PRIORITY);
							t.start();
						}
					});
					fileMenu.add(saveItem);
					JMenuItem restoreBackupItem = new JMenuItem();
					restoreBackupItem.setText(Strings.getString("MainFrame.RestoreBackup")); //$NON-NLS-1$
					restoreBackupItem.setToolTipText(Strings.getString("MainFrame.RestoreBackupToolTip")); //$NON-NLS-1$
					restoreBackupItem.setName("file-menu:restore"); //$NON-NLS-1$
					restoreBackupItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								c.setInfoMessage(Strings.getString("MainFrame.LoadBckupMessage")); //$NON-NLS-1$
								Protocol.transmitTable(Protocol.getProgress(Protocol.getCurrentUser().getID()),
										Protocol.getCurrentUser().getID(), new ProgressChangedNotifier() {

									@Override
									public void progressChanged(int min, int max, int current) {

										c.setInfoMessage(Strings.getString("MainFrame.SaveData") + ": " + current //$NON-NLS-1$ //$NON-NLS-2$
												+ "/" + max); //$NON-NLS-1$

									}
								});
							} catch (IOException e1) {
								Console.log(LogType.Error, ownHandle, "An error occured doing the restore operation:"); //$NON-NLS-1$
								e1.printStackTrace();
							} catch (PermissionDeninedException e1) {
								Console.log(LogType.Error, ownHandle, "An error occured doing the restore operation:"); //$NON-NLS-1$
								e1.printStackTrace();
							}
						}

					});
					fileMenu.add(restoreBackupItem);
					fileMenu.addSeparator();
					JMenuItem disconnectItem = new JMenuItem();
					disconnectItem.setText(Strings.getString("MainFrame.FileMenu.disconnect")); //$NON-NLS-1$
					disconnectItem.setName("file-menu:disconnect"); //$NON-NLS-1$
					disconnectItem.setToolTipText(Strings.getString("MainFrame.DisconnectToolTip")); //$NON-NLS-1$
					disconnectItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								if (c.isChanged()) {
									Object[] elements = { Strings.getString("MainFrame.Dialog.Yes"), //$NON-NLS-1$
											Strings.getString("MainFrame.Dialog.No"), //$NON-NLS-1$
											Strings.getString("MainFrame.Dialog.Abort") //$NON-NLS-1$
									};
									int n = FixedOptionPane.showFixedOptionDialog(ownHandle,
											Strings.getString("MainFrame.AskForSave"), //$NON-NLS-1$
											Strings.getString("MainFrame.Attention"), //$NON-NLS-1$
											JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
											elements, elements[2]);
									if (n == 1) {
										c.setInfoMessage(Strings.getString("MainFrame.SaveData")); //$NON-NLS-1$
										if (Protocol.transmitTable(c.buildFromCache(),
												Protocol.getCurrentUser().getID(), new ProgressChangedNotifier() {

											@Override
											public void progressChanged(int min, int max, int current) {
												c.setInfoMessage(Strings.getString("MainFrame.SaveData") + ": " //$NON-NLS-1$ //$NON-NLS-2$
														+ current + "/" + max); //$NON-NLS-1$
											}
										}))
											Console.log(LogType.StdOut, this, "Successfully transmitted Data"); //$NON-NLS-1$
										else
											Console.log(LogType.StdOut, this, "Failed to transmitt Data"); //$NON-NLS-1$
										Protocol.save();
										c.setChanged(false);
										c.setInfoMessage(""); //$NON-NLS-1$
									} else if (n == 2) {
										return;
									}
								}
								Protocol.disconnect();
								JayCorp.exit(0);
							} catch (Exception e) {
								Console.log(LogType.Error, this, "Couldn�t disconnect the client."); //$NON-NLS-1$
								e.printStackTrace();
								JayCorp.exit(1);
							}
						}

					});
					this.addWindowListener(new WindowListener() {

						@Override
						public void windowOpened(WindowEvent e) {

						}

						@Override
						public void windowClosing(WindowEvent e) {
							try {
								if (c.isChanged()) {
									Object[] elements = { Strings.getString("MainFrame.Dialog.Yes"), //$NON-NLS-1$
											Strings.getString("MainFrame.Dialog.No"), //$NON-NLS-1$
											Strings.getString("MainFrame.Dialog.Abort") //$NON-NLS-1$
									};
									int n = FixedOptionPane.showFixedOptionDialog(ownHandle,
											Strings.getString("MainFrame.AskForSave"), //$NON-NLS-1$
											Strings.getString("MainFrame.Attention"), //$NON-NLS-1$
											JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
											elements, elements[2]);
									if (n == 1) {
										c.setInfoMessage(Strings.getString("MainFrame.SaveData")); //$NON-NLS-1$
										if (Protocol.transmitTable(c.buildFromCache(),
												Protocol.getCurrentUser().getID(), new ProgressChangedNotifier() {

											@Override
											public void progressChanged(int min, int max, int current) {
												c.setInfoMessage(Strings.getString("MainFrame.SaveData") + ": " //$NON-NLS-1$ //$NON-NLS-2$
														+ current + "/" + max); //$NON-NLS-1$
											}
										}))
											Console.log(LogType.StdOut, this, "Successfully transmitted Data"); //$NON-NLS-1$
										else
											Console.log(LogType.StdOut, this, "Failed to transmitt Data"); //$NON-NLS-1$
										Protocol.save();
										c.setChanged(false);
										c.setInfoMessage(""); //$NON-NLS-1$
									} else if (n == 2) {
										return;
									}
								}
								Protocol.disconnect();
								JayCorp.exit(0);
							} catch (Exception e1) {
								Console.log(LogType.Error, this, "Couldn�t disconnect the client."); //$NON-NLS-1$
								e1.printStackTrace();
								JayCorp.exit(1);
							}
						}

						@Override
						public void windowClosed(WindowEvent e) {

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
					fileMenu.add(disconnectItem);
					JMenuItem logoutItem = new JMenuItem();
					logoutItem.setText(Strings.getString("MainFrame.Logout")); //$NON-NLS-1$
					logoutItem.setName("file-menu:logout"); //$NON-NLS-1$
					logoutItem.setToolTipText(
							Strings.getString("MainFrame.LogoutToolTip")); //$NON-NLS-1$
					logoutItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								if (c.isChanged()) {
									Object[] elements = { Strings.getString("MainFrame.Dialog.Yes"), //$NON-NLS-1$
											Strings.getString("MainFrame.Dialog.No"), //$NON-NLS-1$
											Strings.getString("MainFrame.Dialog.Abort") //$NON-NLS-1$
									};
									int n = FixedOptionPane.showFixedOptionDialog(ownHandle,
											Strings.getString("MainFrame.AskForSave"), //$NON-NLS-1$
											Strings.getString("MainFrame.Attention"), //$NON-NLS-1$
											JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
											elements, elements[2]);
									if (n == 1) {
										c.setInfoMessage(Strings.getString("MainFrame.SaveData")); //$NON-NLS-1$
										if (Protocol.transmitTable(c.buildFromCache(),
												Protocol.getCurrentUser().getID(), new ProgressChangedNotifier() {

											@Override
											public void progressChanged(int min, int max, int current) {
												c.setInfoMessage(Strings.getString("MainFrame.SaveData") + ": " //$NON-NLS-1$ //$NON-NLS-2$
														+ current + "/" + max); //$NON-NLS-1$
											}
										}))
											Console.log(LogType.StdOut, this, "Successfully transmitted Data"); //$NON-NLS-1$
										else
											Console.log(LogType.StdOut, this, "Failed to transmitt Data"); //$NON-NLS-1$
										Protocol.save();
										c.setChanged(false);
										c.setInfoMessage(""); //$NON-NLS-1$
									} else if (n == 2) {
										return;
									}
								}
								Protocol.disconnect();
								JayCorp.exit(0, true);
							} catch (Exception e) {
								Console.log(LogType.Error, this, "Couldn�t disconnect the client."); //$NON-NLS-1$
								e.printStackTrace();
								JayCorp.exit(1, true);
							}
						}

					});
					fileMenu.add(logoutItem);
					fileMenu.addSeparator();
					JMenuItem settingsItem = new JMenuItem();
					settingsItem.setText(Strings.getString("MainFrame.FileMenu.settings")); //$NON-NLS-1$
					settingsItem.setName("file-menu:settings"); //$NON-NLS-1$
					settingsItem.setToolTipText(Strings.getString("MainFrame.SettingsItemToolTip")); //$NON-NLS-1$
					settingsItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							SettingsFrame sf = new SettingsFrame();
							sf.setOwner(ownHandle);
							sf.setVisible(true);
						}

					});
					fileMenu.add(settingsItem);
				}
				menuStrip.add(fileMenu);
			}
			this.setJMenuBar(menuStrip);
			c = new Calendar();
			try {
				c.setContent(Protocol.getCurrentUser().getSelectedDays());
			} catch (NullPointerException e) {
				Console.log(LogType.Error, this, "Didn´t recieved user information yet"); //$NON-NLS-1$
			} catch (Exception e) {
				Console.log(LogType.Error, this, "An unknown error occured:"); //$NON-NLS-1$
				e.printStackTrace();
			}
			c.setEditEnabled(Protocol.isEditEnabled());
			c.setMaxNumDay(getScale(Protocol.getCurrentUser().getWorkAge()) + Protocol.getCurrentUser().getExtraDays());
			this.add(c);
		}

		Console.log(LogType.Information, this, "Edit state: " + Boolean.toString(Protocol.isEditEnabled())); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		return "MainFrame"; //$NON-NLS-1$
	}

	private int getScale(int workAge) {
		if (workAge < 0)
			return 365;
		return 0;
	}

	public void updateState() {
		Protocol.collectInformation();
		c.setMaxNumDay(getScale(Protocol.getCurrentUser().getWorkAge()) + Protocol.getCurrentUser().getExtraDays());
		c.setEditEnabled(Protocol.isEditEnabled());
		c.repaint();
	}

}
