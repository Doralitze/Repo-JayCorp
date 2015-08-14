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
	public static final String VERSION = "1.3.1.57";

	private static boolean running = true;
	private static ArrayList<ClientConnector> clients;
	private static Thread commandThread;
	private static ServerSocket server;
	private static Scanner s;

	public static void main(String[] args) {
		Console.log(LogType.Information, "UpstartAgend", "Opening Server @" + port);
		Console.log(LogType.Information, "UpstartAgend", "©2014 - 2015 Leon Dietrich");
		Console.log(LogType.Information, "UpstartAgend", "Software version: " + VERSION);

		clients = new ArrayList<ClientConnector>();
		if (args.length == 0) {
			Console.log(LogType.Error, "UpstartAgend", "No data path provided");
			System.exit(1);
		}
		Data.loadDataFile(args[0]);
		try {
			server = new ServerSocket(port);
			{
				int timeout = Integer.parseInt(Settings.getString("Settings.timeout"));
				if (timeout != -1) {
					server.setSoTimeout(timeout);
					Console.log(LogType.Information, "UpstartAgend", "Set waiting connection to " + timeout);
				}
			}
			commandThread = new Thread(new Runnable() {

				@Override
				public void run() {
					Console.log(LogType.Information, "UpstartAgend", "Successfully started server");
					Console.log(LogType.Information, "UpstartAgend",
							"current edit state: " + Boolean.toString(Data.isEditEnabled()));
					Console.log(LogType.StdOut, this, "Type 'help' to get a list of all aviable commands.");
					s = new Scanner(System.in);
					while (running == true && !commandThread.isInterrupted()) {
						String line = s.nextLine();
						String command = line.split(" ")[0];
						switch (command) {
						case "help":
							Console.log(LogType.StdOut, this, "Here is a list of all commands:");
							System.out.println("help		display this help page");
							System.out.println("stop		shut the sever down");
							System.out.println("list		displays the current number of logged in useres");
							System.out.println("save		manually save the database");
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
									"getDBDump <ID>                        This will dump the section of user ID");
							System.out.println(
									"getBackupDump <ID>                    This will dump the backup of user ID");
							System.out.println(
									"invalidate <ID>                       This will invalide the selections of ID... Danger!");
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
						default:
							Console.log(LogType.Error, this, "Unknown command: " + command);
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
			while (running) {
				try {
					ClientConnector c = new ClientConnector(server.accept());
					Console.log(LogType.StdOut, "ConnectionAgend", "Got client: " + c.getRemoteAdress());
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
		Console.log(LogType.Information, "ShutdownAgend", "Closing Server");
	}

	public static void exit() {
		Console.log(LogType.Information, "ShutdownAgend", "sending stop request");
		for (ClientConnector c : clients)
			c.postMessage("disconnect");
		running = false;
		Data.save();
		try {
			server.close();
		} catch (IOException e) {
			Console.log(LogType.Error, "ShutdownAgend", "failed to close socket");
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

}
