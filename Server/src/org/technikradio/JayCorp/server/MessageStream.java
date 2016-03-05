/*
JayCorp-Server/MessageStream.java
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
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class MessageStream implements Runnable, BroadcastListener {

	private PrintStream out;
	private ArrayList<String> outList;
	private Scanner in;
	private Socket c;
	private Thread workThread;
	private boolean imPush = false;

	// private final Runnable postInitExecution;

	private static Thread entryThread;
	private static ArrayList<BroadcastListener> bcListeners;
	private static Hashtable<String, ClientConnector> pendingList;

	static {
		bcListeners = new ArrayList<BroadcastListener>();
		entryThread = new Thread(new Runnable() {

			@Override
			public void run() {
				/*
				 * This Thread is used to recognize incoming applications for the messaging system
				 */
				ServerSocket s = null;
				try {
					s = new ServerSocket(Integer.parseInt(Settings.getString("Settings.messagePort")));
				} catch (NumberFormatException | IOException e) {
					Console.log(LogType.Error, "MessageStreamRegistry", "Couldn´t bind message stram port:");
					e.printStackTrace();
					System.exit(1);
				}
				while (!entryThread.isInterrupted()) {
					try {
						Socket newConnection = s.accept();
						new MessageStream(newConnection);
					} catch (IOException e) {
						Console.log(LogType.Information, "MessageStreamRegistry", "Closed message stream:");
						e.printStackTrace();
						break;
					}
				}
				try {
					s.close();
				} catch (IOException e) {
					Console.log(LogType.Error, "MessageStreamRegistry", "An unexpected Exception occured:");
					e.printStackTrace();
				}
			}
		});
		entryThread.setDaemon(true);
		entryThread.setName("MessageStreamEntryThread");
		entryThread.start();
		pendingList = new Hashtable<String, ClientConnector>();
	}

	public static boolean addBroadcastListener(BroadcastListener bcl) {
		return bcListeners.add(bcl);
	}

	public static boolean removeBroadcastListener(BroadcastListener bcl) {
		return bcListeners.remove(bcl);
	}

	private MessageStream(Socket newConnection) {
		c = newConnection;
		try {
			out = new PrintStream(c.getOutputStream());
			in = new Scanner(c.getInputStream());
			workThread = new Thread(this);
			workThread.setDaemon(true);
			workThread.setName(this.toString() + ":Initialiser");
			workThread.start();
			addBroadcastListener(this);
		} catch (IOException e) {
			Console.log(LogType.Error, this, "An unexpected Exception occured:");
			e.printStackTrace();
		}
		// postInitExecution = null;
		outList = new ArrayList<String>();
	}

	// private MessageStream(Socket newConnection, Runnable postInit) {
	// c = newConnection;
	// try {
	// out = new PrintStream(c.getOutputStream());
	// in = new Scanner(c.getInputStream());
	// workThread = new Thread(this);
	// workThread.setDaemon(true);
	// workThread.setName(this.toString() + ":Initialiser");
	// workThread.start();
	// addBroadcastListener(this);
	// } catch (IOException e) {
	// Console.log(LogType.Error, this, "An unexpected Exception occured:");
	// e.printStackTrace();
	// }
	// postInitExecution = postInit;
	// }

	public static void addConnectionWaiter(String hash, ClientConnector target) {
		pendingList.put(hash, target);
	}

	public static boolean removeIfUnused(ClientConnector target) {
		boolean found = false;
		for (String s : pendingList.keySet()) {
			if (pendingList.get(s) == target) {
				pendingList.remove(s);
				found = true;
			}
		}
		return found;
	}

	public static void stop() {
		if(Boolean.valueOf(Settings.getString("AdvancedOutputMode")))
			Console.log(LogType.Error, "Static:MessageStream", "Kicking entry Thread");
		entryThread.interrupt();
	}

	public static void throwOnBroadcastChannel(String message) {
		for (BroadcastListener r : bcListeners) {
			r.react(message);
		}
	}

	public static void throwOnBroadcastChannelAsync(final String message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				for (BroadcastListener r : bcListeners) {
					r.react(message);
				}
			}
		});
		t.setPriority(4);
		t.setName("AsyncBroadcasterHelper [" + message + "]");
		t.start();
	}

	public void throwMessage(String message) {
		outList.add(message);
		if (imPush)
			refocus();
	}

	public void throwMessageImidiate(String message) {
		out.println(message);
	}

	public void refocus() {
		ArrayList<String> toRemove = new ArrayList<String>();
		for (String s : outList) {
			throwMessageImidiate(s);
			// outList.remove(s);
			toRemove.add(s);
		}
		for (String s : toRemove) {
			outList.remove(s);
		}
	}

	public void destroy() {
		out.println("stop");
		out.close();
		in.close();
		try {
			c.close();
		} catch (IOException e) {
			Console.log(LogType.Error, this, "An unexpected Exception occured:");
			e.printStackTrace();
		} finally {
			removeBroadcastListener(this);
		}
	}

	// Waiting to accept a client
	@Override
	public void run() {
		String hashline = in.nextLine();
		if (pendingList.containsKey(hashline)) {
			pendingList.get(hashline).setMessageStream(this);
			out.println("Success");
			imPush = true;
			refocus();
			// try {
			// // Wait for the other side
			// Thread.sleep(500);
			// // A new thread isn't required
			// postInitExecution.run();
			// } catch (InterruptedException e) {
			// Console.log(LogType.Error, this.toString(),
			// "This shouldn't be interrupted");
			// e.printStackTrace();
			// }
		}
	}

	@Override
	public String toString() {
		return "MessageStream:" + c.getInetAddress();
	}

	@Override
	public void react(String message) {
		throwMessage(message);
	}

}
