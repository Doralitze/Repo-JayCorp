package org.technikradio.JayCorp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

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

	private static final int maxLoginAttemps = Integer.parseInt(Settings
			.getString("ClientConnector.maxLoginAttemps")); //$NON-NLS-1$
	private int loginAttemps = 0;

	private Socket client;
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
			switch (request[0]) {
			case "login": //$NON-NLS-1$
				loginAttemps++;
				if (loginAttemps > maxLoginAttemps) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					Console.log(LogType.Warning, this,
							"Trying to log in very often...");
					break;
				}
				u = Data.getUser(request[1]);
				if (u == null) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					Console.log(LogType.StdOut, this, "User >" + request[1]
							+ "< not existant");
					break;
				} else {
					if (u.getPassword().equals(Base64Coding.decode(request[2]))) {
						user = u;
						out.println("true;".concat(Integer.toString(u.getID()))); //$NON-NLS-1$
						out.flush();
						Console.log(LogType.StdOut, this,
								"User '" + u.getName()
										+ "' successfully logged in...");
					} else {
						out.println("false"); //$NON-NLS-1$
						out.flush();
						Console.log(LogType.StdOut, this, "Wrong login: User: "
								+ request[1] + " Password: " + request[2]);
						break;
					}
				}
				break;
			case "getProg": //$NON-NLS-1$
				ID = Integer.parseInt(request[1]);
				if ((user.getID() != ID && user.getRights()
						.isViewOtherSelectionsAllowed()) || user.getID() == ID) {
					u = Data.getUser(ID);
					StringBuilder sb = new StringBuilder();
					sb.append("true;"); //$NON-NLS-1$
					if (u.getSelectedDays().getDays() == null) {
						u.setSelectedDays(Data.getDefaultConfiguration()
								.clone());
					}
					if (u.getSelectedDays().getDays().size() == 0) {
						sb.append("NO_DATA");
					} else
						for (ParaDate pd : u.getSelectedDays().getDays()
								.keySet()) {
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
				} else {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				break;
			case "setDay": //$NON-NLS-1$
				int pr0 = this.getPriority();
				this.setPriority(Thread.MAX_PRIORITY);
				if (!Data.isEditEnabled()
						&& user.getID() != Data.getUser("root").getID()) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				ParaDate pd = null;
				pd = ParaDate.valueOf(request[1]);
				Status s = Status.valueOf(request[2]);
				ID = Integer.valueOf(request[3]);
				if (ID != user.getID()
						&& !user.getRights().isEditUserInputAllowed()) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				User um = Data.getUser(ID);
				um.getSelectedDays().getDays().remove(getCompared(pd));
				um.getSelectedDays().getDays().put(pd, s);
				out.println("true"); //$NON-NLS-1$
				out.flush();
				this.setPriority(pr0);
				break;
			case "addUser": //$NON-NLS-1$
				String name = request[1];
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
					Data.addUser(newUser);
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
				break;
			case "getRights":
				ID = Integer.valueOf(request[1]);
				if (user.getID() == ID
						|| user.getRights().isViewOtherSelectionsAllowed()) {
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
					sb.append(Boolean.toString(rt
							.isViewOtherSelectionsAllowed()));
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
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							{
								DayTable dt = new DayTable();
								DayTable rd = root.getSelectedDays();
								for (ParaDate p : rd.getDays().keySet()) {
									Status s = rd.getDays().get(p);
									Status real;
									switch (s) {
									case allowed:
										dt.getDays().put(
												ParaDate.valueOf(p.toString()),
												Status.normal);
										break;
									case normal:
										dt.getDays().put(
												ParaDate.valueOf(p.toString()),
												Status.normal);
										break;
									case selected:
										dt.getDays().put(
												ParaDate.valueOf(p.toString()),
												Status.allowed);
										break;
									default:
										break;
									}
								}
								Data.setDefaultConfiguration(dt);
							}
							DayTable dc = Data.getDefaultConfiguration();
							int updatedUsers = 0;
							for (int id = 1; id <= Data.getLatestID(); id++) {
								User u = Data.getUser(id);
								boolean updated = false;
								for (ParaDate p : u.getSelectedDays().getDays()
										.keySet()) {
									Status s1 = dc.getDays().get(
											dc.findCorresponding(p));
									Status s2 = u.getSelectedDays().getDays()
											.get(p);
									if (s1 == Status.normal) {
										u.getSelectedDays().getDays()
												.remove(getCompared(p));
										u.getSelectedDays().getDays()
												.put(p, Status.normal);
									} else if (s1 == Status.allowed
											&& s2 == Status.normal) {
										u.getSelectedDays().getDays()
												.remove(getCompared(p));
										u.getSelectedDays().getDays()
												.put(p, Status.allowed);
									}
								}
								if (updated)
									updatedUsers++;
							}
							Console.log(LogType.StdOut, this, "root did "
									+ updatedUsers + " database updates...");
						}
					});
					t.setName("DatabaseUpdateThread");
					t.setPriority(9);
					t.start();
					Data.setDefaultConfiguration(root.getSelectedDays());
					Console.log(LogType.StdOut, this, "root saved allowed days");
				}
			}
				Data.save();
				out.println("true"); //$NON-NLS-1$
				out.flush();
				break;
			case "changePSWD":
				int reqID = Integer.parseInt(request[1]);
				if (!(user.getID() == reqID || user.getRights()
						.isEditUserAllowed())) {
					out.println("false"); //$NON-NLS-1$
					out.flush();
					break;
				}
				u = Data.getUser(reqID);
				u.setPassword(Base64Coding.decode(request[2]));
				out.println("true"); //$NON-NLS-1$
				out.flush();
				if (reqID == user.getID()
						&& Data.getEntry(user, MetaReg.SHOULD_CHANGE_PASSWORD)
								.equals("true"))
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
					if (Data.getEntry(user, MetaReg.MUST_SET_EXTRA_DAYS)
							.equals("true")) {
						user.setExtraDays(days);
						Data.setEntry(user, MetaReg.MUST_SET_EXTRA_DAYS,
								"false");
						out.println("true"); //$NON-NLS-1$
						out.flush();
						break;
					}
				}
				out.println("false"); //$NON-NLS-1$
				out.flush();
				break;
			case "disconnect": //$NON-NLS-1$
				Console.log(LogType.StdOut, this, "User '" + user.getUsername()
						+ "' diconnected");
				disconnect();
				if (messageStream != null)
					messageStream.destroy();
				break;
			default:
				out.println("null"); //$NON-NLS-1$
				out.flush();
				Console.log(LogType.Warning, this, "Corrupted data recieved: "
						+ request[0]);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			out = new PrintStream(client.getOutputStream());
			while (!this.isInterrupted()) {
				if (!processRequest(in.readLine())) {
					Console.log(LogType.StdOut, this,
							"User '" + user.getUsername() + "' lost connection");
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
		this.interrupt();
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

}
