/*
JayCorp-Server/Server.java
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
package org.technikradio.JayCorp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import org.technikradio.JayCorp.server.ui.Menues;
import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;

public class Server {
	public static final int port = Integer.parseInt(Settings.getString("Settings.port")); //$NON-NLS-1$
	public static final String VERSION = "1.3.3.00";

	private static boolean running = true;
	private static ArrayList<ClientConnector> clients;
	private static Thread commandThread;
	private static ServerSocket server;
	private static Scanner s;

	public static void main(String[] args) {
		Console.log(LogType.Information, "UpstartAgent", "©2014 - 2016 Leon Dietrich");
		Console.log(LogType.Information, "UpstartAgent", "Software version: " + VERSION);

		clients = new ArrayList<ClientConnector>();
		if (args.length == 0) {
			String defaultFile = Settings.getString("Default.UserFile");
			if (defaultFile == "") {
				Console.log(LogType.Error, "UpstartAgent", "No data path provided");
				System.exit(1);
			} else
				Data.loadDataFile(defaultFile);
		} else
			Data.loadDataFile(args[0]);
		try {
			server = new ServerSocket(port);
			{
				int timeout = Integer.parseInt(Settings.getString("Settings.timeout"));
				if (timeout != -1) {
					server.setSoTimeout(timeout);
					Console.log(LogType.Information, "UpstartAgent", "Set waiting connection to " + timeout);
				}
			}
			commandThread = new Thread(new Runnable() {

				@Override
				public void run() {
					Console.log(LogType.Information, "UpstartAgent", "Successfully started server");
					Console.log(LogType.Information, "UpstartAgent",
							"current edit state: " + Boolean.toString(Data.isEditEnabled()));
					Console.log(LogType.StdOut, this, "Type 'help' to get a list of all aviable commands.");
					s = new Scanner(System.in);
					while (running == true && !commandThread.isInterrupted()) {
						System.out.print("# ");
						try {
							String line = s.nextLine();
							String command = line.split(" ")[0];
							switch (command) {
							case "help":
								Console.log(LogType.StdOut, this, "Here is a list of all commands:");
								System.out.println("help		display this help page");
								System.out.println("stop		shut the sever down");
								System.out.println("list		displays the current number of logged in useres");
								System.out.println("save		manually save the database");
								System.out.println("free    get the amount of aviable memory.");
								System.out.println("addUser		this will open a dialoug to add a user");
								System.out
										.println("listUsers		this will list the registered users and their selections");
								System.out.println("getDCDump   this will dump the default configuration");
								System.out.println();
								System.out.println("setEditEnabled <true | false>		This enables/disables the editing");
								System.out.println(
										"writeOnBroadcastChannel <message>       This wites <message> on to the broadcast channel");
								System.out.println("getID <USER>                          This will return the USER's ID");
								System.out.println(
										"getDBDump <ID>                        This will dump the selection of user ID");
								System.out.println(
										"getclearDBDump <ID>                   This will dump the selections of user ID filtered by important dates.");
								System.out.println(
										"getBackupDump <ID>                    This will dump the backup of user ID");
								System.out.println(
										"invalidate <ID>                       This will invalide the selections of ID... Danger!");
								System.out.println(
										"isDBUpdateRunning					   This will print true if it is the case.");
								System.out.println();
								break;
							case "stop":
								exit();
								break;
							case "list":
								Console.log(LogType.StdOut, this,
										"There are " + clients.size() + " users currently logged in.");
								break;
							case "save":
								Data.save();
								Console.log(LogType.Information, this, "Successfully saved database");
								break;
							case "addUser":
								Menues.addUser();
								break;
							case "listUsers":
								System.out.println("Amount of registered users: " + Data.getUserCount());
								StringBuilder sb = new StringBuilder();
								for (int id : Data.getAllIDs()) {
									boolean run = false;
									ParaDate start = null, end = null;
									User u = Data.getUser(id);
									DayTable dt = u.getSelectedDays();
									sb.append(u.getID());
									sb.append(' ');
									sb.append(u.getWorkAge());
									sb.append(' ');
									sb.append(u.getName());
									sb.append(' ');
									sb.append(u.getUsername());
									sb.append(" ::");
									for (ParaDate pd : dt.getDays().keySet()) {
										if (dt.getDays().get(pd) == Status.selected) {
											if (!run) {
												run = true;
												start = pd;
											} else {
												end = pd;
											}
										} else {
											if (run) {
												run = false;
												if (!(end == null || start == null)) {
													sb.append(' ');
													sb.append(start.getMinimalDate());
													sb.append(" - ");
													sb.append(end.getMinimalDate());
													sb.append(" || ");
												}
											}
										}
									}
									sb.append('\n');
								}
								System.out.println(sb.toString());
								break;
							case "setEditEnabled":
								Data.setEditEnabled(Boolean.valueOf(line.split(" ")[1]));
								Console.log(LogType.StdOut, "CommandDeamon",
										"Set EDIT_ENABLED_FLAG to " + line.split(" ")[1]);
								MessageStream.throwOnBroadcastChannelAsync("reload");
								break;
							case "writeOnBroadcastChannel":
								MessageStream.throwOnBroadcastChannel(line.split(" ")[1]);
								break;
							case "getID":
								System.out.println(Data.getUser(line.split(" ")[1]).getID());
								break;
							case "free":
								int mb = 1024 * 1024;
								Runtime instance = Runtime.getRuntime();
								System.out.println("***** Heap utilization statistics [MB] *****\n");
								System.out.println("Total Memory: " + instance.totalMemory() / mb);
								System.out.println("Free Memory : " + instance.freeMemory() / mb);
								System.out.println("Used Memory : "
											+ (instance.totalMemory() - instance.freeMemory()) / mb);
								System.out.println("Max Memory  : " + instance.maxMemory() / mb);
								break;
							case "getDBDump":
								try {
									DayTable dt = Data.getUser(Integer.parseInt(line.split(" ")[1])).getSelectedDays();
									for (ParaDate pd : dt.getDays().keySet()) {
										System.out.println(pd.toString() + ": " + dt.getDays().get(pd).toString());
									}
								} catch (NumberFormatException e) {
									System.out.println(line.split(" ")[1] + " is not a number");
								}
								break;
							case "getclearDBDump":
								try {
									DayTable dt = Data.getUser(Integer.parseInt(line.split(" ")[1])).getSelectedDays();
									for (ParaDate pd : dt.getDays().keySet()) {
										Status s = dt.getDays().get(pd);
										if(s == Status.allowed || s == Status.selected)
											System.out.println(pd.toString() + ": " + s.toString());
									}
								} catch (NumberFormatException e) {
									System.out.println(line.split(" ")[1] + " is not a number");
								}
								break;
							case "getDCDump": {
								DayTable dt = Data.getDefaultConfiguration();
								for (ParaDate pd : dt.getDays().keySet()) {
									System.out.println(pd.toString() + ": " + dt.getDays().get(pd).toString());
								}
							}
								break;
							case "getBackupDump":
								try {
									DayTable dt = Data.getUser(Integer.parseInt(line.split(" ")[1])).getBackup();
									for (ParaDate pd : dt.getDays().keySet()) {
										System.out.println(pd.toString() + ": " + dt.getDays().get(pd).toString());
									}
								} catch (NumberFormatException e) {
									System.out.println(line.split(" ")[1] + " is not a number");
								}
								break;
							case "invalidate":
								try {
									System.out.println("Are you shure? <y/n>");
									if (Menues.getBoolInput(s))
										Data.getUser(Integer.parseInt(line.split(" ")[1])).setSelectedDays(new DayTable());
								} catch (NumberFormatException e) {
									System.out.println(line.split(" ")[1] + " is not a number");
								}
								break;
case "isDBUpdateRunning":
								System.out.println(ClientConnector.isDBUpdateRunning());
								break;
							default:
								Console.log(LogType.Error, this, "Unknown command: " + command);
							}

						} catch (Exception e) {
							Console.log(LogType.Warning, this, "An error occured inside the command:");
							e.printStackTrace();
						}
					}
					Console.log(LogType.Information, this, "stopping command deamon");

				}

				@Override
				public String toString() {
					return "CommandDeamon";
				}
			});
			commandThread.setDaemon(true);
			commandThread.setName("CommandDeamon");
			commandThread.setPriority(Thread.MIN_PRIORITY);
			commandThread.start();
			Console.log(LogType.Information, "UpstartAgent", "Opening Server @" + port);
			while (running) {
				try {
					ClientConnector c = new ClientConnector(server.accept());
					Console.log(LogType.StdOut, "ConnectionAgent", "Got client: " + c.getRemoteAdress());
					clients.add(c);
				} catch (SocketException e) {
					if (running) {
						Console.log(LogType.Error, "ConnectionCollector", "An unknown error occured");
						e.printStackTrace();
					}
				}
			}
			for (ClientConnector c : clients) {
				c.interrupt();
			}
			server.close();
		} catch (IOException e) {
			Console.log(LogType.Error, Thread.currentThread().getName(), "Could not open server"); //$NON-NLS-1$
			e.printStackTrace();
			System.exit(1);
		}
		Console.log(LogType.Information, "ShutdownAgent", "Closing Server");
	}

	public static void exit() {
		Console.log(LogType.Information, "ShutdownAgent", "sending stop request");
		for (ClientConnector c : clients)
			c.postMessage("disconnect");
		running = false;
		Data.save();
		try {
			server.close();
		} catch (IOException e) {
			Console.log(LogType.Error, "ShutdownAgent", "failed to close socket");
			e.printStackTrace();
		}
		s.close();
		for (ClientConnector c : clients) {
			c.disconnect();
		}
	}

	public static boolean remove(ClientConnector c) {
		return clients.remove(c);
	}

	public static void kick(User u) {
		for (ClientConnector c : clients) {
			if (c.getConnectedUser() != null)
				if (c.getConnectedUser().getID() == u.getID()) {
					c.disconnect();
					c.interrupt();
					clients.remove(u);
				}
		}
	}

	public static boolean usersConnected(){
		if(clients.size() == 0)
			return false;
		return true;
	}

}
