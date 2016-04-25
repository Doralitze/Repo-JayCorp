/*
JayCorp-Client/MainFrame.java
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.technikradio.jay_corp.JayCorp;
import org.technikradio.jay_corp.ProgressChangedNotifier;
import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.Settings;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.PermissionDeninedException;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;

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
		this.setTitle(Strings.getString("MainFrame.FrameTitle")); //$NON-NLS-1$
		this.setMinimumSize(new Dimension(830, 500));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
					if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) //$NON-NLS-1$ //$NON-NLS-2$
						saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.META_MASK));
					else
						saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
					saveItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							Thread t = new Thread(new Runnable() {

								@Override
								public void run() {
									save();
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
					if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) //$NON-NLS-1$ //$NON-NLS-2$
						restoreBackupItem.setAccelerator(
								KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_MASK + KeyEvent.META_MASK));
					else
						restoreBackupItem.setAccelerator(
								KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_MASK + KeyEvent.CTRL_MASK));
					restoreBackupItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								c.setInfoMessage(Strings.getString("MainFrame.LoadBckupMessage")); //$NON-NLS-1$
								Protocol.transmitTable(Protocol.getProgress(Protocol.getCurrentUser().getID()), null,
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
							exit();
						}

					});
					this.addWindowListener(new WindowListener() {

						@Override
						public void windowOpened(WindowEvent e) {

						}

						@Override
						public void windowClosing(WindowEvent e) {
							exit();

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
					logoutItem.setToolTipText(Strings.getString("MainFrame.LogoutToolTip")); //$NON-NLS-1$
					logoutItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								if (showJesNoSaveDialog()) {
									Protocol.disconnect();
									JayCorp.exit(0, true);
								}
							} catch (Exception e) {
								Console.log(LogType.Error, this, "Couldn�t disconnect the client."); //$NON-NLS-1$
								e.printStackTrace();
								JayCorp.exit(1, true);
							}
						}

					});
					if (Boolean.parseBoolean(Settings.getString("SupportLogout")))
						fileMenu.add(logoutItem);
					fileMenu.addSeparator();
					JMenuItem settingsItem = new JMenuItem();
					settingsItem.setText(Strings.getString("MainFrame.FileMenu.settings")); //$NON-NLS-1$
					settingsItem.setName("file-menu:settings"); //$NON-NLS-1$
					settingsItem.setToolTipText(Strings.getString("MainFrame.SettingsItemToolTip")); //$NON-NLS-1$
					if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) //$NON-NLS-1$ //$NON-NLS-2$
						settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, KeyEvent.META_MASK));
					else
						settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, KeyEvent.CTRL_MASK));
					settingsItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							SettingsFrame sf = new SettingsFrame(ownHandle);
							sf.setOwner(ownHandle);
							sf.setVisible(true);
						}

					});
					fileMenu.add(settingsItem);
					JMenuItem aboutItem = new JMenuItem();
					aboutItem.setText("Über");
					aboutItem.setName("file-menu:about");
					aboutItem.setToolTipText("Hier werden die Copyrighthinweise und die Lizenz angezeigt.");
					aboutItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							new AboutDialog().show();
						}
					});
					fileMenu.add(aboutItem);
				}
				menuStrip.add(fileMenu);
			}
			{
				JMenu markMenu = new JMenu();
				markMenu.setName("mark-menu");
				markMenu.setText("Markieren");
				markMenu.setToolTipText("In diesem Menu können Sie automatismen zur auswahl aufrufen.");
				JMenuItem clearAllItem = new JMenuItem();
				clearAllItem.setText("Gesamte Auswahl aufheben");
				clearAllItem.setName("mark-menu:clearall");
				clearAllItem.setToolTipText(
						"Mit einem Click auf diesen Button setzen Sie ihre gesamte " + "Auswahl zurück");
				clearAllItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						c.clearAll();
					}
				});
				markMenu.add(clearAllItem);
				JMenuItem selectRangeItem = new JMenuItem();
				selectRangeItem.setText("Bereich auswählen");
				selectRangeItem.setName("mark-menu:selectrange");
				selectRangeItem.setToolTipText(
						"Hiermit können Sie einen zusammenhängenden Bereich auswählen."
						+ "\n(Zum Beispiel eine ganze Woche)");
				selectRangeItem.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						Thread t = new Thread(new Runnable(){

							@Override
							public void run() {
								String[] sa = {"Bitte das Anfangsdatum angeben: ",
								"Bitte das Enddatum angeben: "};
								ParaDate dates[] = DateSelectorFrame.query(sa);
								if(dates != null)
									try {
										c.selectRange(dates[0], dates[1], Status.selected);
									} catch (SelectionNotAllowedException e) {
										Console.log(LogType.Information, this, "Cannot select all wanted dates.");
										e.printStackTrace();
									}
							}});
						t.setName("RangeSelectionQuerryThread");
						t.start();
					}});
				markMenu.add(selectRangeItem);
				JMenuItem deselectRangeItem = new JMenuItem();
				deselectRangeItem.setText("Bereich aufheben");
				deselectRangeItem.setName("mark-menu:deselectrange");
				deselectRangeItem.setToolTipText(
						"Hiermit können Sie einen zusammenhängenden Bereich aufheben."
						+ "\n(Zum Beispiel eine ganze Woche)");
				deselectRangeItem.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						Thread t = new Thread(new Runnable(){

							@Override
							public void run() {
								String[] sa = {"Bitte das Anfangsdatum angeben: ",
								"Bitte das Enddatum angeben: "};
								ParaDate dates[] = DateSelectorFrame.query(sa);
								if(dates != null)
									try {
										c.selectRange(dates[0], dates[1], Status.allowed);
									} catch (SelectionNotAllowedException e) {
										Console.log(LogType.Information, this, "Cannot deselect all wanted dates.");
										e.printStackTrace();
									}
							}});
						t.setName("RangeDeselectionQuerryThread");
						t.start();
					}});
				markMenu.add(deselectRangeItem);
				menuStrip.add(markMenu);
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
		doPostChecks();
		Console.log(LogType.Information, this, "Edit state: " + Boolean.toString(Protocol.isEditEnabled())); //$NON-NLS-1$
	}

	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);

	}

	@Override
	public String toString() {
		return "MainFrame"; //$NON-NLS-1$
	}

	public static int getScale(int workAge) {
		if (workAge < 0)
			return 365;
		return 0;
	}

	public void updateState(boolean savereq) {
		Protocol.collectInformation();
		c.setMaxNumDay(getScale(Protocol.getCurrentUser().getWorkAge()) + Protocol.getCurrentUser().getExtraDays());
		c.setEditEnabled(Protocol.isEditEnabled());
		c.setMaxNumDay(getScale(Protocol.getCurrentUser().getWorkAge()) + Protocol.getCurrentUser().getExtraDays());
		c.repaint();
		doPostChecks();
		if (savereq)
			save();
	}

	/**
	 * This method is an highlevel save method for the ui
	 */
	private void save() {
		c.setMessage(Strings.getString("MainFrame.SaveNotifierPopUp")); //$NON-NLS-1$
		ownHandle.setEnabled(false);
		c.setInfoMessage(Strings.getString("MainFrame.SaveData")); //$NON-NLS-1$
		ownHandle.repaint();
		boolean successfullBackup = Protocol.moveToBackup(Protocol.getCurrentUser().getID());
		if (successfullBackup)
			Protocol.rmDatabaseEntries(Protocol.getCurrentUser().getID());

		if (Protocol.transmitTable(c.buildFromCache(), c.getOriginalContent(), Protocol.getCurrentUser().getID(),
				new ProgressChangedNotifier() {

					@Override
					public void progressChanged(int min, int max, int current) {

						c.setInfoMessage(Strings.getString("MainFrame.SaveData") + ": " + current //$NON-NLS-1$ //$NON-NLS-2$
								+ "/" + max); //$NON-NLS-1$
					}
				})) {
			Console.log(LogType.StdOut, this, "Successfully transmitted Data"); //$NON-NLS-1$
			c.setChanged(false);
			ownHandle.repaint();
			c.setInfoMessage("Führe Backup aus..."); //$NON-NLS-1$
			ownHandle.repaint();
		}

		else {
			Console.log(LogType.StdOut, this, "Failed to transmitt Data"); //$NON-NLS-1$
			c.setChanged(true);
			c.setInfoMessage(Strings.getString("MainFrame.TransmitFailMessage")); //$NON-NLS-1$
			ownHandle.repaint();
		}
		Protocol.save();
		c.setInfoMessage(""); //$NON-NLS-1$
		c.setMessage(null);
		ownHandle.setEnabled(true);
		ownHandle.repaint();
	}

	private void doPostChecks() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
				if (Protocol.getCurrentUser().getPassword()
						.equals(Strings.getString("CSVImporter.DefaultNewPassword"))) { //$NON-NLS-1$
					JOptionPane.showMessageDialog(null, Strings.getString("MessageStreamHandler.OnPSWDMessage"), //$NON-NLS-1$
							Strings.getString("MessageStreamHandler.MessageHeaderDays") + toString(), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
				}
			}

			@Override
			public String toString() {
				return super.toString().substring(0, super.toString().length() - 11) + "@doPostChecks():t$r.run()"; //$NON-NLS-1$
			}

		});
		t.setDaemon(true);
		t.setName("Thread:MFPostChecks"); //$NON-NLS-1$
		t.setPriority(2);
		t.start();
	}

	/**
	 * Show a dialog if it should save the content
	 * 
	 * @return true if the application is allowed to exit otherwise false
	 */
	private boolean showJesNoSaveDialog() {
		Console.log(LogType.Information, this, "Evaluating if it would be good to display the save message");
		if (c.isChanged()) {
			Object[] elements = { Strings.getString("MainFrame.Dialog.Yes"), //$NON-NLS-1$
					Strings.getString("MainFrame.Dialog.No"), //$NON-NLS-1$
					Strings.getString("MainFrame.Dialog.Abort") //$NON-NLS-1$
			};
			int n = FixedOptionPane.showFixedOptionDialog(ownHandle, Strings.getString("MainFrame.AskForSave"), //$NON-NLS-1$
					Strings.getString("MainFrame.Attention"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, elements, elements[2]);
			if (n == 1) {
				save();
			} else if (n == 2) {
				return false;
			}
		}
		return true;
	}

	private void exit() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				ownHandle.setEnabled(false);
				if (showJesNoSaveDialog()) {
					ownHandle.dispose();
					Protocol.disconnect();
					System.exit(0);
				}
				ownHandle.setEnabled(false);
			}
		});
		t.setName("SaveAndCloseThread");
		t.setPriority(10);
		t.start();
	}
}
