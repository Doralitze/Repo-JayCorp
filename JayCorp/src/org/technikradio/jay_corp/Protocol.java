package org.technikradio.jay_corp;

import java.io.IOException;
import java.util.Hashtable;

import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.PermissionDeninedException;
import org.technikradio.jay_corp.user.Righttable;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;

import de.bennyden.coding.Base64Coding;

public class Protocol {

	private static Connection c;
	private static boolean validLogin = false;
	private static boolean editEnabled = false;
	private static User currentUser;

	static {
		c = new Connection();
		validLogin = false;
	}

	private static String[] decodeAnswer(String answer) {
		if (answer == "null") //$NON-NLS-1$
			throw new NullPointerException();
		if (answer == null)
			return null;
		return answer.split(";"); //$NON-NLS-1$
	}

	public static User getUser(int ID) throws PermissionDeninedException, IOException {
		User u = new User();
		u.setID(ID);
		c.transmit("getUser ".concat(Integer.toString(ID))); //$NON-NLS-1$
		String[] result = decodeAnswer(c.receive());
		if (result[0].equals("$NOUSER§")) //$NON-NLS-1$
			return null;
		if (result[0] == "false") { //$NON-NLS-1$
			throw new PermissionDeninedException();
		} else {
			u.setName(result[1]);
			u.setPassword(Base64Coding.decode(result[2]));
			u.setWorkAge(Integer.parseInt(result[3]));
			u.setExtraDays(Integer.parseInt(result[4]));
			try {
				u.setUsername(result[5]);
			} catch (ArrayIndexOutOfBoundsException e) {
				Console.log(LogType.Warning, "ProtocolHandler", //$NON-NLS-1$
						"The server uses an outdated protocol and didn´t transferred the username..."); //$NON-NLS-1$
				u.setUsername("Username not given by server"); //$NON-NLS-1$
			}
		}
		return u;
	}

	public static DayTable getProgress(int ID) throws IOException, PermissionDeninedException {
		DayTable d = new DayTable();
		Hashtable<ParaDate, Status> ht = new Hashtable<ParaDate, Status>();
		c.transmit("getProg ".concat(Integer.toString(ID))); //$NON-NLS-1$
		String[] result = decodeAnswer(c.receive());
		if (result[0] == "false") { //$NON-NLS-1$
			throw new PermissionDeninedException();
		} else if (result[0] == "NO_DATA" || result[0] == "NO") { //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		} else {
			for (int i = 1; i < result.length; i++) {
				String[] a = result[i].split("="); //$NON-NLS-1$
				ParaDate pd = ParaDate.valueOf(a[0]);
				ht.put(pd, Status.valueOf(a[1]));
			}
		}
		d.setDays(ht);
		return d;
	}

	public static DayTable getProgressFromBackup(int ID) throws IOException, PermissionDeninedException {
		DayTable d = new DayTable();
		Hashtable<ParaDate, Status> ht = new Hashtable<ParaDate, Status>();
		c.transmit("getProgBackup ".concat(Integer.toString(ID))); //$NON-NLS-1$
		String[] result = decodeAnswer(c.receive());
		if (result[0] == "false") { //$NON-NLS-1$
			throw new PermissionDeninedException();
		} else if (result[0] == "NO_DATA" || result[0] == "NO") { //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		} else {
			for (int i = 1; i < result.length; i++) {
				String[] a = result[i].split("="); //$NON-NLS-1$
				ParaDate pd = ParaDate.valueOf(a[0]);
				ht.put(pd, Status.valueOf(a[1]));
			}
		}
		d.setDays(ht);
		return d;
	}

	public static boolean isLoginFree(String name) {
		c.transmit("isLoginFree " + name); //$NON-NLS-1$
		try {
			String[] result = decodeAnswer(c.receive());
			return Boolean.parseBoolean(result[0]);
		} catch (IOException e) {
			Console.log(LogType.Error, "Protocol", "Connection refused:"); //$NON-NLS-1$ //$NON-NLS-2$
			e.printStackTrace();
			return false;
		}

	}

	public static boolean moveToBackup(int ID) {
		c.transmit("mvToBackup " + Integer.toString(ID)); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean rmDatabaseEntries(int ID) {
		c.transmit("rmDB " + Integer.toString(ID)); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean login(String username, String password) {
		c.transmit("login ".concat(username).concat(" ") //$NON-NLS-1$ //$NON-NLS-2$
				.concat(Base64Coding.encode(password)));
		try {
			String result = c.receive();
			String[] a = decodeAnswer(result);
			validLogin = Boolean.parseBoolean(a[0]);
			if (validLogin) {
				currentUser = getUser(Integer.parseInt(a[1]));
			} else {
				Console.log(LogType.Warning, "ProtocolHandler", //$NON-NLS-1$
						"Incorrect login"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (PermissionDeninedException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {

		}
		return validLogin;
	}

	public static boolean setDay(ParaDate d, Status s, int ID) {
		StringBuilder sb = new StringBuilder();
		sb.append("setDay "); //$NON-NLS-1$
		sb.append(d.toString());
		sb.append(' ');
		sb.append(s.toString());
		sb.append(' ');
		sb.append(Integer.toString(ID));
		c.transmit(sb.toString());
		try {
			return Boolean.valueOf(c.receive());
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"An unexpected exception occurred: " + e.getMessage()); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
	}

	public static boolean addUser(String name, String username, String password, int ID, int workingAge) {
		c.transmit("addUser " //$NON-NLS-1$
				.concat(Base64Coding.encode(name)).concat(" ") //$NON-NLS-1$
				.concat(Base64Coding.encode(password)).concat(" ") //$NON-NLS-1$
				.concat(Integer.toString(ID).concat(" ") //$NON-NLS-1$
						.concat(Integer.toString(workingAge)))
				.concat(" ") //$NON-NLS-1$
				.concat(username));
		try {
			String a = decodeAnswer(c.receive())[0];
			if (a == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean addUser(User u) {
		boolean success = addUser(u.getName(), u.getUsername(), u.getPassword(), u.getID(), u.getWorkAge());
		if (!success)
			return false;
		success = changeExtraDays(u.getExtraDays(), u.getID());
		if (!success)
			return false;
		success = Protocol.changeRights(u.getID(), u.getRights());
		return success;
	}

	public static int[] getUsers() {
		c.transmit("getUsers"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			int[] rtv = new int[a.length];
			for (int i = 0; i < a.length; i++) {
				rtv[i] = Integer.parseInt(a[i]);
			}
			return rtv;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getIDCount() {
		c.transmit("getIDCount"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			return Integer.parseInt(a[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean save() {
		c.transmit("save"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void disconnect() {
		Console.log(LogType.StdOut, "Protocol", "Disconnecting from server");
		c.transmit("disconnect"); //$NON-NLS-1$
		try {
			c.stop();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			c = null;
			validLogin = false;
			currentUser = null;
		}
	}

	public static boolean assoziateID(int ID, String username) {
		c.transmit("assoziate " + Integer.toString(ID) + " " + username); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setEditEnableOnServer(boolean flag) {
		c.transmit("setEditEnable " + Boolean.toString(flag)); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		loadEditEnabled();
		return true;
	}

	public static Righttable getRights(int ID) {
		Righttable rt = new Righttable();
		c.transmit("getRights ".concat(Integer.toString(ID))); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return null;
			rt.setAccessUserInputAllowed(Boolean.valueOf(a[1]));
			rt.setAddUserAllowed(Boolean.valueOf(a[2]));
			rt.setEditUserAllowed(Boolean.valueOf(a[3]));
			rt.setEditUserInputAllowed(Boolean.valueOf(a[4]));
			rt.setGetIDCountAllowed(Boolean.valueOf(a[5]));
			rt.setListAllUsersAllowed(Boolean.valueOf(a[6]));
			rt.setOpenCloseEditAllowed(Boolean.valueOf(a[7]));
			rt.setViewOtherSelectionsAllowed(Boolean.valueOf(a[8]));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return rt;
	}

	public static boolean loadEditEnabled() {
		c.transmit("getEditEnabled"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			// System.out.println(a[0]);
			if (a[0].toLowerCase().equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"An unexpected error occured (perhaps networking?):"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean collectInformation() {
		currentUser.setRights(getRights(currentUser.getID()));
		setEditEnabled(loadEditEnabled());
		try {
			currentUser.setSelectedDays(getProgress(currentUser.getID()));
		} catch (IOException | PermissionDeninedException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"An error occured doing content excange"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		Console.log(LogType.StdOut, "ProtocolHandler", //$NON-NLS-1$
				"Successfully loaded all data."); //$NON-NLS-1$
		return true;
	}

	public static void setEditEnabled(boolean editEnabled) {
		Protocol.editEnabled = editEnabled;
	}

	public static boolean isEditEnabled() {
		return editEnabled;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static boolean transmitTable(DayTable d, int ID) {
		boolean success = true;
		for (ParaDate pd : d.getDays().keySet()) {
			if (!setDay(pd, d.getDays().get(pd), ID))
				success = false;
		}
		return success;
	}

	public static boolean transmitTable(DayTable d, int ID, ProgressChangedNotifier pcn) {
		boolean success = true;
		int max = d.getDays().size();
		int current = 1;
		for (ParaDate pd : d.getDays().keySet()) {
			if (!setDay(pd, d.getDays().get(pd), ID))
				success = false;
			pcn.progressChanged(1, max, current);
			current++;
		}
		return success;
	}

	public static boolean transmitTable(DayTable d, int ID, ProgressChangedNotifier pcn, boolean fast) {
		if (!fast)
			return transmitTable(d, ID, pcn);
		else {
			boolean success = true;
			int max = d.getDays().size();
			int current = 0;
			StringBuilder sb = new StringBuilder();

			for (ParaDate pd : d.getDays().keySet()) {
				sb.append("setDay ");
				sb.append(pd.toString());
				sb.append(' ');
				Status s = d.getDays().get(pd);
				sb.append(' ');
				sb.append(Integer.toString(ID));
				sb.append("\n");
				pcn.progressChanged(1, max * 2 + 1, current + 1);
				current++;
			}
			for (int i = 0; i <= current; i++) {
				try {
					if (!Boolean.valueOf(c.receive()))
						success = false;
					pcn.progressChanged(1, max + i + 1, current + 1);
				} catch (IOException e) {
					Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
							"An unexpected exception occurred: " + e.getMessage()); //$NON-NLS-1$
					e.printStackTrace();
				}
			}
			return success;
		}
		/*
		 * public static boolean setDay(ParaDate d, Status s, int ID) {
		 * StringBuilder sb = new StringBuilder(); sb.append("setDay ");
		 * //$NON-NLS-1$ sb.append(d.toString()); sb.append(' ');
		 * sb.append(s.toString()); sb.append(' ');
		 * sb.append(Integer.toString(ID)); c.transmit(sb.toString()); try {
		 * return Boolean.valueOf(c.receive()); } catch (IOException e) {
		 * Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
		 * "An unexpected exception occurred: " + e.getMessage()); //$NON-NLS-1$
		 * e.printStackTrace(); return false; } }
		 */
	}

	public static String getMessageHash() {
		c.transmit("getMessageHash"); //$NON-NLS-1$
		try {
			return c.receive();
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve message hash"); //$NON-NLS-1$
			e.printStackTrace();
			return null;
		}
	}

	public static boolean changePassword(String newPassword, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append("changePSWD "); //$NON-NLS-1$
		sb.append(id);
		sb.append(' ');
		sb.append(Base64Coding.encode(newPassword));
		c.transmit(sb.toString());
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve critical request answer:"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean changeExtraDays(int days, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append("changeDays "); //$NON-NLS-1$
		sb.append(id);
		sb.append(' ');
		sb.append(days);
		c.transmit(sb.toString());
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve critical request answer:"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean changeRights(int id, Righttable rt) {
		StringBuilder sb = new StringBuilder();
		sb.append("setRights "); //$NON-NLS-1$
		sb.append(id);
		sb.append(' ');
		sb.append(Boolean.toString(rt.isAccessUserInputAllowed()));
		sb.append(' ');
		sb.append(Boolean.toString(rt.isAddUserAllowed()));
		sb.append(' ');
		sb.append(Boolean.toString(rt.isEditUserAllowed()));
		sb.append(' ');
		sb.append(Boolean.toString(rt.isEditUserInputAllowed()));
		sb.append(' ');
		sb.append(Boolean.toString(rt.isGetIDCountAllowed()));
		sb.append(' ');
		sb.append(Boolean.toString(rt.isListAllUsersAllowed()));
		sb.append(' ');
		sb.append(Boolean.toString(rt.isOpenCloseEditAllowed()));
		sb.append(' ');
		sb.append(Boolean.toString(rt.isViewOtherSelectionsAllowed()));
		c.transmit(sb.toString());
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve critical request answer:"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean destroyDatabase() {
		c.transmit("destroyAll");
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0] == "false") //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve critical request answer:"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
