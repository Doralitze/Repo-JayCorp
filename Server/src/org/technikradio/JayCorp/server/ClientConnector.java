/*
JayCorp-Server/ClientConnector.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.Righttable;
import org.technikradio.jay_corp.user.User;
import org.technikradio.task.ID;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;

import de.bennyden.coding.Base64Coding;

public class ClientConnector extends Thread {

	private static final int maxLoginAttemps = Integer.parseInt(Settings.getString("ClientConnector.maxLoginAttemps")); //$NON-NLS-1$
	private static final ArrayList<User> loggedInUsers = new ArrayList<User>();
	private static final boolean crt = Boolean.parseBoolean(Settings.getString("Settings.amdCrt"));
	private int loginAttemps = 0;
	private long lastCall = System.currentTimeMillis() / 1000;
	private boolean needtoFixDB = false;

	private Socket client;
	private Thread kickThread;
	private MessageStream messageStream;
	private BufferedReader in;
	private PrintStream out;
	private User user;
	private String mh;

	public ClientConnector(Socket client) {
		super();
		this.client = client;
		this.setName(this.toString());
		this.start();
		mh = new ID("MessageStreamHash", true).toString();
		MessageStream.addConnectionWaiter(mh, this);
		kickThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						kickThread.interrupt();
						return;
					}
					if (lastCall + 240 < (System.currentTimeMillis() / 1000)) {
						Console.log(LogType.Warning, kickThread.getName(),
								"This client didn´t responded sice more than 3 minutes. Let´s assume it crashed and kick him.");
						disconnect();
						if (messageStream != null)
							messageStream.destroy();
					}
				}
			}
		});
		kickThread.setName("CrashKicker:[" + client.getInetAddress() + ":" + Integer.toString(client.getPort()) + "]");
		kickThread.setDaemon(true);
		kickThread.start();
	}

	private boolean processRequest(String input) {
		// Console.log(LogType.StdOut, this, "Input: " + input);
		if (input == null)
			return false;
		try {
			String[] request = input.split(" "); //$NON-NLS-1$
			User u = null;
			int ID = 0;
			// Console.log(LogType.StdOut, this, "Getting command: " + request);
			lastCall = System.currentTimeMillis() / 1000;
			switch (request[0]) {
			case "login": //$NON-NLS-1$
				loginAttemps++;
				if (loginAttemps > maxLoginAttemps) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					Console.log(LogType.Warning, this, "Trying to log in very often...");
					break;
				}
				u = Data.getUser(request[1]);
				if (u == null) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					Console.log(LogType.StdOut, this, "User >" + request[1] + "< not existant");
					break;
				} else {
					if (u.getPassword().equals(Base64Coding.decode(request[2]))) {
						user = u;
						out.println("true;".concat(Integer.toString(u.getID()))); //$NON-NLS-1$
						out.flush();
						loggedInUsers.add(u);
						Console.log(LogType.StdOut, this, "User '" + u.getName() + "' successfully logged in...");
					} else {
						out.println("false"); //$NON-NLS-1$
						out.flush();
						Console.log(LogType.StdOut, this,
								"Wrong login: User: " + request[1] + " Password: " + request[2]);
						break;
					}
				}
				break;
			case "keepAlive":
				lastCall = System.currentTimeMillis() / 1000;
				break;
			case "isLoginFree": {
				String name = request[1];
				if (loggedInUsers.contains(Data.getUser(name)))
					out.println("false"); //$NON-NLS-1$
				else
					out.println("true"); //$NON-NLS-1$
				out.flush();
				break;
			}
			case "getProg": //$NON-NLS-1$
				ID = Integer.parseInt(request[1]);
				if ((user.getID() != ID && user.getRights().isViewOtherSelectionsAllowed()) || user.getID() == ID) {
					u = Data.getUser(ID);
					StringBuilder sb = new StringBuilder();
					sb.append("true;"); //$NON-NLS-1$
					if (u.getSelectedDays().getDays() == null) {
						u.setSelectedDays(Data.getDefaultConfiguration().clone());
					}
					if (u.getSelectedDays().getDays().size() == 0) {
						sb.append("NO_DATA");
					} else
						Console.log(LogType.StdOut, this,
								"User '" + user.getUsername() + "' requested an DB read. There are currently "
										+ u.getSelectedDays().getDays().size() + " elements in '" + u.getUsername()
										+ "'´s database register, the CUID is "
										+ u.getSelectedDays().getConnectedUser());
					for (ParaDate pd : u.getSelectedDays().getDays().keySet()) {
						Status s = u.getSelectedDays().getDays().get(pd);
						sb.append(pd.toString());
						// System.out.println(pd.toString());
						sb.append("="); //$NON-NLS-1$
						sb.append(s.toString()); // gegenstück:
													// s.valueOf(String);
						sb.append(";"); //$NON-NLS-1$
					}
					out.println(sb.toString());
					out.flush();
					if (u.getSelectedDays().getConnectedUser() != -1 && u.getSelectedDays().getConnectedUser() != ID)
						Console.log(LogType.Warning, this, "Wrong user connection in daytable");
					else if (u.getSelectedDays().getConnectedUser() == -1)
						u.getSelectedDays().setConnectedUser(ID);
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "getProgBackup":
				ID = Integer.parseInt(request[1]);
				if ((user.getID() != ID && user.getRights().isViewOtherSelectionsAllowed()) || user.getID() == ID) {
					u = Data.getUser(ID);
					StringBuilder sb = new StringBuilder();
					sb.append("true;"); //$NON-NLS-1$
					if (u.getBackup().getDays() == null) {
						u.setBackup(Data.getDefaultConfiguration().clone());
					}
					if (u.getBackup().getDays().size() == 0) {
						sb.append("NO_DATA");
					} else
						Console.log(LogType.StdOut, this,
								"User '" + user.getUsername() + "' requested an DB read. There are currently "
										+ u.getBackup().getDays().size() + " elements in '" + u.getUsername()
										+ "'´s database register");
					for (ParaDate pd : u.getBackup().getDays().keySet()) {
						Status s = u.getBackup().getDays().get(pd);
						sb.append(pd.toString());
						// System.out.println(pd.toString());
						sb.append("="); //$NON-NLS-1$
						sb.append(s.toString()); // gegenstück:
													// s.valueOf(String);
						sb.append(";"); //$NON-NLS-1$
					}
					out.println(sb.toString());
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "mvToBackup":
				ID = Integer.parseInt(request[1]);
				if ((user.getID() != ID && user.getRights().isEditUserInputAllowed()) || user.getID() == ID) {
					user.setBackup(user.getSelectedDays());
					out.println("true"); //$NON-NLS-1$
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "rmDB":
				ID = Integer.parseInt(request[1]);
				if ((user.getID() != ID && user.getRights().isEditUserInputAllowed()) || user.getID() == ID) {
					user.setSelectedDays(new DayTable());
					out.println("true"); //$NON-NLS-1$
					out.flush();
					if (user.getID() == Data.getUser("root").getID()) {
						Data.setDefaultConfiguration(new DayTable());
						Console.log(LogType.StdOut, this, "root cleared DC");
					}
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "setDay": //$NON-NLS-1$
				int pr0 = this.getPriority();
				this.setPriority(Thread.MAX_PRIORITY);
				if (!Data.isEditEnabled() && user.getID() != Data.getUser("root").getID()) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				ParaDate pd = null;
				pd = ParaDate.valueOf(request[1]);
				Status s = Status.valueOf(request[2]);
				ID = Integer.valueOf(request[3]);
				if (ID != user.getID() && !user.getRights().isEditUserInputAllowed()) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				User um = Data.getUser(ID);
				try {
					um.getSelectedDays().getDays().remove(getCompared(pd));
				} catch (NullPointerException e) {
					// Should we do something is there is nothing to delete?
				}
				um.getSelectedDays().getDays().put(pd, s);

				/*if (user.getID() == Data.getUser("root").getID()) {
					// Update dc
					Status ss = Status.normal;
					switch (s) {
					case undefined:
					case normal:
						// Should never happen
						Console.log(LogType.Error, this, "Root has normal state @setDay::updateDC");
						needtoFixDB = true;
					case allowed:
						ss = Status.normal;
						break;
					case selected:
						ss = Status.allowed;
						break;
					}

					Data.getDefaultConfiguration().getDays().remove(getCompared(pd));
					Data.getDefaultConfiguration().getDays().put(pd, ss);
				}*/

				out.println("true"); //$NON-NLS-1$
				out.flush();
				this.setPriority(pr0);
				break;
			case "addUser": //$NON-NLS-1$
				String name = Base64Coding.decode(request[1]);
				String password = Base64Coding.decode(request[2]);// request[2];
				ID = Integer.parseInt(request[3]);
				int workAge = Integer.parseInt(request[4]);
				if (user.getRights().isAddUserAllowed()) {
					User newUser = new User();
					if (Data.getUser(ID) != null) {
						// Duplicate ID
						out.println("false"); //$NON-NLS-1$
						out.flush();
						break;
					} else
						newUser.setID(ID);
					newUser.setName(name);
					newUser.setPassword(password);
					newUser.setWorkAge(workAge);
					newUser.setUsername(request[5]);
					newUser.setSelectedDays(Data.getDefaultConfiguration().clone());
					Data.addUser(newUser);
					out.println("true"); //$NON-NLS-1$
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "assoziate": //$NON-NLS-1$
				ID = Integer.parseInt(request[1]);
				if (user.getRights().isAddUserAllowed()) {
					Data.getUser(ID).setUsername(request[2]);
					out.println("true"); //$NON-NLS-1$
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "getUsers": //$NON-NLS-1$
				if (user.getRights().isListAllUsersAllowed()) {
					StringBuilder sb = new StringBuilder();
					int[] ids = Data.getAllIDs();
					for (int i : ids) {
						sb.append(Integer.toString(i));
						sb.append(';');
					}
					out.println(sb.toString());
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "getIDCount": //$NON-NLS-1$
				if (user.getRights().isGetIDCountAllowed()) {
					int idc = Data.getAllIDs().length;
					out.println(Integer.toString(idc));
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "getUser": //$NON-NLS-1$
				ID = Integer.parseInt(request[1]);
				if (user.getID() == ID || user.getRights().isEditUserAllowed()) {
					StringBuilder sb = new StringBuilder();
					sb.append("true;");
					User us = Data.getUser(ID);
					if (us != null) {
						sb.append(us.getName());
						sb.append(';');
						sb.append(Base64Coding.encode(new String(us.getPassword())));
						sb.append(';');
						sb.append(Integer.toString(us.getWorkAge()));
						sb.append(';');
						sb.append(Integer.toString(us.getExtraDays()));
						sb.append(';');
						sb.append(us.getUsername());
						out.println(sb.toString());
					} else
						out.println("$NOUSER§;" + ID);
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "setEditEnable": //$NON-NLS-1$
				if (user.getRights().isOpenCloseEditAllowed()) {
					Data.setEditEnabled(Boolean.valueOf(request[1]));
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				out.println("true"); //$NON-NLS-1$
				out.flush();
				break;
			case "getRights":
				ID = Integer.valueOf(request[1]);
				if (user.getID() == ID || user.getRights().isViewOtherSelectionsAllowed()) {
					Righttable rt = Data.getUser(ID).getRights();
					StringBuilder sb = new StringBuilder();
					sb.append("true;");
					sb.append(Boolean.toString(rt.isAccessUserInputAllowed()));
					sb.append(";");
					sb.append(Boolean.toString(rt.isAddUserAllowed()));
					sb.append(";");
					sb.append(Boolean.toString(rt.isEditUserAllowed()));
					sb.append(";");
					sb.append(Boolean.toString(rt.isEditUserInputAllowed()));
					sb.append(";");
					sb.append(Boolean.toString(rt.isGetIDCountAllowed()));
					sb.append(";");
					sb.append(Boolean.toString(rt.isListAllUsersAllowed()));
					sb.append(";");
					sb.append(Boolean.toString(rt.isOpenCloseEditAllowed()));
					sb.append(";");
					sb.append(Boolean.toString(rt.isViewOtherSelectionsAllowed()));
					sb.append(";");
					out.println(sb.toString());
					out.flush();
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "getEditEnabled":
				out.println(Boolean.toString(Data.isEditEnabled()));
				out.flush();
				Console.log(LogType.StdOut, this, "requested edit state");
				break;
			case "getMessageHash":
				out.println(mh);
				break;
			case "save": //$NON-NLS-1$
			{
				final User root = Data.getUser("root");
				if (user == root) {
					if(needtoFixDB){
						fixRootDB();
					}
					//processDCWrite();
					updateDatabase(root);
				} else {
					Data.save();
				}
			}
				out.println("true"); //$NON-NLS-1$
				out.flush();
				break;
			case "changePSWD":
				int reqID = Integer.parseInt(request[1]);
				if (!(user.getID() == reqID || user.getRights().isEditUserAllowed())) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				u = Data.getUser(reqID);
				u.setPassword(Base64Coding.decode(request[2]));
				out.println("true"); //$NON-NLS-1$
				out.flush();
				if (reqID == user.getID() && Data.getEntry(user, MetaReg.SHOULD_CHANGE_PASSWORD).equals("true"))
					Data.setEntry(user, MetaReg.SHOULD_CHANGE_PASSWORD, "false");
				break;
			case "changeDays":
				reqID = Integer.parseInt(request[1]);
				int days = Integer.parseInt(request[2]);
				if (user.getRights().isEditUserAllowed()) {
					u = Data.getUser(reqID);
					u.setExtraDays(days);
					out.println("true"); //$NON-NLS-1$
					out.flush();
					break;
				} else if (reqID == user.getID()) {
					if (Data.getEntry(user, MetaReg.MUST_SET_EXTRA_DAYS).equals("true")
							|| Boolean.parseBoolean(Settings.getString("Settings.MultiEDSetAllowed"))) {
						user.setExtraDays(days);
						Data.setEntry(user, MetaReg.MUST_SET_EXTRA_DAYS, "false");
						out.println("true"); //$NON-NLS-1$
						out.flush();
						break;
					}
				}
				out.println("false"); //$NON-NLS-1$
				out.flush();
				break;
			case "setRights":
				if (!user.getRights().isEditUserAllowed()) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
				}
				reqID = Integer.parseInt(request[1]);
				u = Data.getUser(reqID);
				Righttable rt = u.getRights();
				rt.setAccessUserInputAllowed(Boolean.parseBoolean(request[2]));
				rt.setAddUserAllowed(Boolean.parseBoolean(request[3]));
				rt.setEditUserAllowed(Boolean.parseBoolean(request[4]));
				rt.setEditUserInputAllowed(Boolean.parseBoolean(request[5]));
				rt.setGetIDCountAllowed(Boolean.parseBoolean(request[6]));
				rt.setListAllUsersAllowed(Boolean.parseBoolean(request[7]));
				rt.setOpenCloseEditAllowed(Boolean.parseBoolean(request[8]));
				rt.setViewOtherSelectionsAllowed(Boolean.parseBoolean(request[9]));
				out.println("true"); //$NON-NLS-1$
				out.flush();
				break;
			case "destroyAll": {
				if (user.getID() == Data.getUser("root").getID()) {
					Data.destroyDatabase();
					for (User us : loggedInUsers) {
						if (!(us.getID() == Data.getUser("root").getID())) {
							// Kick user
							Server.kick(us);
						}
					}
					out.println("true");
					out.flush();
				} else {
					out.println("false");
					out.flush();
				}
				break;
			}
			case "setRange": {
				String[] m = request[1].split(";");
				for (String b : m) {
					if(b.length() < 3)
						break;
					String[] sr = b.split("=");
					pd = ParaDate.valueOf(sr[0]);
					s = Status.valueOf(sr[1]);
					try {
						user.getSelectedDays().getDays().remove(getCompared(pd));
					} catch (NullPointerException e) {
						// Should we do something is there is nothing to delete?
						// probably not
					}
					user.getSelectedDays().getDays().put(pd, s);
					/*if (user.getID() == Data.getUser("root").getID()) {
						// Update dc
						Status ss = Status.normal;
						switch (s) {
						case undefined:
						case normal:
							// Should never happen
							Console.log(LogType.Error, this, "Root has normal state @setRange::updateDC");
							needtoFixDB = true;
						case allowed:
							ss = Status.normal;
							break;
						case selected:
							ss = Status.allowed;
							break;
						}

						Data.getDefaultConfiguration().getDays().remove(getCompared(pd));
						Data.getDefaultConfiguration().getDays().put(pd, ss);
					}*/
				}
				out.println("true");
				out.flush();
			}
				break;
			case "disconnect": //$NON-NLS-1$
				try {
					Console.log(LogType.StdOut, this, "User '" + user.getUsername() + "' diconnected");
				} catch (NullPointerException e) {
					if (Boolean.valueOf(Settings.getString("AdvancedOutputMode")))
						e.printStackTrace();
				}
				disconnect();
				if (messageStream != null)
					messageStream.destroy();
				break;
			default:
				out.println("null"); //$NON-NLS-1$
				out.flush();
				Console.log(LogType.Warning, this, "Corrupted data recieved: " + request[0]);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void fixRootDB() {
		//fix root's database if broken
		User root = Data.getUser("root");
		Hashtable<ParaDate, Status> rootDays = root.getSelectedDays().getDays();
		Enumeration<ParaDate> er = rootDays.keys();
		while(er.hasMoreElements()){
			ParaDate pda = er.nextElement();
			if(pda != null){
				Status pdas = rootDays.get(pda);
				switch(pdas){
				case allowed:
				case selected:
					rootDays.remove(pda);
					rootDays.put(pda, Status.selected);
					break;
				case normal:
				case undefined:
				default:
					rootDays.remove(pda);
					rootDays.put(pda, Status.allowed);
					break;
				
				}
			}
		}
		Console.log(LogType.Warning, this, "Was required to fix roots database.");
	}

	public User getConnectedUser() {
		return user;
	}

	private ParaDate getCompared(ParaDate pd) {
		ParaDate compObj = null;
		String s1 = pd.toString();
		for (ParaDate op : user.getSelectedDays().getDays().keySet()) {
			String s2 = op.toString();
			if (s1.contentEquals(s2)) {
				compObj = op;
				break;
			}
		}
		return compObj;
	}

	@Override
	public void run() {
		try {
			Console.log(LogType.StdOut, this, "Client incomming"); //$NON-NLS-1$
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintStream(client.getOutputStream());
			while (!this.isInterrupted()) {
				if (!processRequest(in.readLine())) {
					try {
						Console.log(LogType.StdOut, this, "User '" + user.getUsername() + "' lost connection");
						disconnect();
					} catch (NullPointerException e) {
					}
					break;
				}
			}
			in.close();
			out.close();
			client.close();
			Server.remove(this);
			System.gc();
		} catch (IOException e) {
			Console.log(LogType.Error, this, "Faild to transmit data"); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ClientConnector {" + client.getInetAddress() + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void disconnect() {
		loggedInUsers.remove(user);
		this.interrupt();
		kickThread.interrupt();
	}

	public String getRemoteAdress() {
		return client.getInetAddress() + ":" + client.getPort();
	}

	public void setMessageStream(MessageStream messageStream) {
		this.messageStream = messageStream;
		// Do post-init stuff
		if (Data.getEntry(user, MetaReg.MUST_SET_EXTRA_DAYS).equals("true")) {
			messageStream.throwMessage("setDays");
		}
		if (Data.getEntry(user, MetaReg.SHOULD_CHANGE_PASSWORD).equals("true")) {
			messageStream.throwMessage("changePassword");
		}
	}

	public boolean postMessage(String message) {
		if (messageStream == null)
			return false;
		messageStream.throwMessage(message);
		return true;
	}

	@SuppressWarnings("unused")
	private Status[][] listToSortedArray(DayTable t) {
		Status[][] sar = new Status[13][32];
		for (ParaDate p : t.getDays().keySet()) {
			Status s = t.getDays().get(p);
			sar[p.getMonth()][p.getDay()] = s;
		}
		return sar;
	}

	@SuppressWarnings("unused")
	private DayTable sortedArrayToList(Status[][] sar, int year) {
		DayTable dt = new DayTable();
		for (short m = 1; m <= 12; m++)
			for (short t = 1; t <= 31; t++) {
				if (sar[m][t] != null) {
					ParaDate pd = new ParaDate();
					pd.setDay(t);
					pd.setMonth(m);
					pd.setYear(year);
					dt.getDays().put(pd, sar[m][t]);
				}
			}
		return dt;
	}

	private ParaDate find(ParaDate corresponding, DayTable field) {
		ParaDate found = null;
		final String key = corresponding.getMinimalDate();
		for (ParaDate p : field.getDays().keySet()) {
			if (key.equals(p.getMinimalDate())) {
				found = p;
				break;
			}
		}
		return found;
	}

	/**
	 * This medthod updates all dates from non root users
	 * @param root The root user
	 */
	private void updateDatabase(final User root) {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				processDCWrite(root);
				int changed = 0;
				int total = 0;
				DayTable dc = Data.getDefaultConfiguration();
				for (int id = 1; id <= Data.getLatestID(); id++) {
					User _user = Data.getUser(id);
					if (_user != null /* && _user != Data.getUser("root") */) {
						if(crt)
						Console.log(LogType.Information, "DatabaseUpdater", "Adopting days of user: " + Integer.toString(_user.getID()) + " " + _user.getUsername());
						DayTable dtUser = _user.getSelectedDays();
						DayTable dtUserNew = new DayTable();
						try {
							for (ParaDate pdDC : dc.getDays().keySet()) {
								try {
									ParaDate pdUser = find(pdDC, dtUser);
									Status expected = dc.getDays().get(pdDC), got = dtUser.getDays().get(pdUser);
									switch (expected) {
									case normal:
									case undefined:
										if (dtUserNew.getDays().put(pdUser, Status.normal) == null)
											if (crt)
												Console.log(LogType.Error, this, "Invalid operation @updateDatabase()");
										changed++;
										break;
									case allowed:
									case selected:
									default:
										if ((got == Status.normal) || (got == Status.undefined)) {
											changed++;
											if (dtUserNew.getDays().put(pdUser, Status.allowed) == null)
												if (crt)
													Console.log(LogType.Error, this,
															"Invalid operation @updateDatabase()");
										} else
											dtUserNew.getDays().put(pdUser, got);
										break;
									}
								} catch (NullPointerException e) {
									Console.log(LogType.Error, this,
											"Nullpointer in cleanup routine: " + e.getLocalizedMessage());
								}
							}
						} catch (NullPointerException e) {
							Console.log(LogType.Error, this, "Nullpointer on user " + _user.getName() + ": ");
							e.printStackTrace();
						}
						_user.setSelectedDays(dtUserNew);
						total++;
					}
				}
				Console.log(LogType.Information, this,
						"Removed " + changed + " selected entries and " + total + " total checks");
				Data.save();
			}
		});
		t.setName("DatabaseUpdateThread");
		t.setPriority(9);
		t.start();
		Console.log(LogType.StdOut, this, "root saved allowed days");
	}

	/**
	 * This method prepares the default configuration.
	 */
	private void processDCWrite(User root) {
		Data.setDefaultConfiguration(root.getSelectedDays().clone());
		Hashtable<ParaDate, Status> dcp = Data.getDefaultConfiguration().getDays();
		for (ParaDate p : dcp.keySet()) {
			Status ss = dcp.get(p);
			Status s = Status.normal;
			switch (ss) {
			case selected:
				s = Status.allowed;
				break;
			case allowed:
			case normal:
			case undefined:
			default:
				s = Status.normal;
				break;
			}
			if (dcp.put(p, s) == null)
				Console.log(LogType.Error, this, "Invalid operation @processDCWrite()");
		}
		// System.out.println(dcp.size());
	}
}
