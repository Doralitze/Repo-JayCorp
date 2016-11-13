/*
JayCorp-Client/Protocol.java
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
	private static Thread keepAliveThread;
	private static boolean validLogin = false;
	private static boolean editEnabled = false;
	private static boolean busy = false;
	private static User currentUser;

	static {
		c = new Connection();
		validLogin = false;
		keepAliveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!keepAliveThread.isInterrupted() && c != null) {
					block(true);
					c.transmit("keepAlive");
					release();
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						keepAliveThread.interrupt();
						e.printStackTrace();
					}
				}
			}
		});
		keepAliveThread.setName("KeepAliveThread");
		keepAliveThread.setPriority(2);
		keepAliveThread.start();
	}

	private static String[] decodeAnswer(String answer) {
		if (answer == null)
			return null;
		if (answer.equals("null")) //$NON-NLS-1$
			throw new NullPointerException();
		return answer.split(";"); //$NON-NLS-1$
	}

	private static void block() {
		int wait = 0;
		while (busy) {
			wait++;
			if (wait == 5000)
				throw new RuntimeException("Waiting too long. Assuming network crash");
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Console.log(LogType.Error, "ProtocolHandler", "System transmit interferred");
				e.printStackTrace();
			}
		}
		busy = true;
	}

	private static void block(boolean longmode) {
		int wait = 0;
		boolean called = false;
		while (busy) {
			wait++;
			if ((!longmode && wait == 5000) || (longmode && wait == 60000))
				throw new RuntimeException("Waiting too long. Assuming network crash");
			if (longmode && called && wait > 5000) {
				called = true;
				Console.log(LogType.Warning, "ProtocolHandler", "Waiting verry long on Protocol.");
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Console.log(LogType.Error, "ProtocolHandler", "System transmit interferred");
				e.printStackTrace();
			}
		}
		busy = true;
	}

	private static void release() {
		busy = false;
	}

	public static User getUser(int ID) throws PermissionDeninedException, IOException {
		User u = new User();
		u.setID(ID);
		block();
		c.transmit("getUser ".concat(Integer.toString(ID))); //$NON-NLS-1$
		String[] result = decodeAnswer(c.receive());
		release();
		if (result[0].equals("$NOUSER§")) //$NON-NLS-1$
			return null;
		if (result[0].equals("false")) { //$NON-NLS-1$
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
		block();
		c.transmit("getProg ".concat(Integer.toString(ID))); //$NON-NLS-1$
		String[] result = decodeAnswer(c.receive());
		release();
		if (result[0].equals("false")) { //$NON-NLS-1$
			throw new PermissionDeninedException();
		} else if (result[0].equals("NO_DATA") || result[0].equals("NO")) { //$NON-NLS-1$ //$NON-NLS-2$
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
		block();
		c.transmit("getProgBackup ".concat(Integer.toString(ID))); //$NON-NLS-1$
		String[] result = decodeAnswer(c.receive());
		release();
		if (result[0].equals("false")) { //$NON-NLS-1$
			throw new PermissionDeninedException();
		} else if (result[0].equals("NO_DATA") || result[0].equals("NO")) { //$NON-NLS-1$ //$NON-NLS-2$
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
		block();
		c.transmit("isLoginFree " + name); //$NON-NLS-1$
		try {
			String[] result = decodeAnswer(c.receive());
			return Boolean.parseBoolean(result[0]);
		} catch (IOException e) {
			Console.log(LogType.Error, "Protocol", "Connection refused:"); //$NON-NLS-1$ //$NON-NLS-2$
			e.printStackTrace();
			return false;
		} finally {
			release();
		}
	}

	public static boolean moveToBackup(int ID) {
		block();
		c.transmit("mvToBackup " + Integer.toString(ID)); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		release();
		return true;
	}

	public static boolean rmDatabaseEntries(int ID) {
		block();
		c.transmit("rmDB " + Integer.toString(ID)); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			release();
			return false;
		}
		return true;
	}

	public static boolean login(String username, String password) {
		block();
		c.transmit("login ".concat(username).concat(" ") //$NON-NLS-1$ //$NON-NLS-2$
				.concat(Base64Coding.encode(password)));
		try {
			String result = c.receive();
			release();
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
			release();
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
		block();
		c.transmit(sb.toString());
		try {
			return Boolean.valueOf(c.receive());
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"An unexpected exception occurred: " + e.getMessage()); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		} finally {
			release();
		}
	}

	public static boolean addUser(String name, String username, String password, int ID, int workingAge) {
		block();
		c.transmit("addUser " //$NON-NLS-1$
				.concat(Base64Coding.encode(name)).concat(" ") //$NON-NLS-1$
				.concat(Base64Coding.encode(password)).concat(" ") //$NON-NLS-1$
				.concat(Integer.toString(ID).concat(" ") //$NON-NLS-1$
						.concat(Integer.toString(workingAge)))
				.concat(" ") //$NON-NLS-1$
				.concat(username));
		try {
			String a = decodeAnswer(c.receive())[0];
			release();
			if (a.equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
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
		block();
		c.transmit("getUsers"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			int[] rtv = new int[a.length];
			for (int i = 0; i < a.length; i++) {
				rtv[i] = Integer.parseInt(a[i]);
			}
			return rtv;
		} catch (IOException e) {
			release();
			e.printStackTrace();
			return null;
		}
	}

	public static int getIDCount() {
		block();
		c.transmit("getIDCount"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			return Integer.parseInt(a[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release();
		}
		return -1;
	}

	public static boolean save() {
		block();
		c.transmit("save"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void disconnect() {
		try {
			Console.log(LogType.StdOut, "Protocol", "Disconnecting from server");
			block();
			c.transmit("disconnect"); //$NON-NLS-1$
			c.stop();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			release();
			c = null;
			validLogin = false;
			currentUser = null;
		}
	}

	public static boolean assoziateID(int ID, String username) {
		block();
		c.transmit("assoziate " + Integer.toString(ID) + " " + username); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setEditEnableOnServer(boolean flag) {
		block();
		c.transmit("setEditEnable " + Boolean.toString(flag)); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
			e.printStackTrace();
			return false;
		}
		loadEditEnabled();
		return true;
	}

	public static Righttable getRights(int ID) {
		Righttable rt = new Righttable();
		block();
		c.transmit("getRights ".concat(Integer.toString(ID))); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
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
			release();
			e.printStackTrace();
			return null;
		}
		return rt;
	}

	public static boolean loadEditEnabled() {
		block();
		c.transmit("getEditEnabled"); //$NON-NLS-1$
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			// System.out.println(a[0]);
			if (a[0].toLowerCase().equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
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

	public static boolean transmitTable(DayTable d, DayTable o, int ID) {
		boolean success = true;
		for (ParaDate pd : d.getDays().keySet()) {
			if ((o != null && o.getDays().get(o.findCorresponding(pd)).equals(d.getDays().get(pd))) || (o == null))
				if (!setDay(pd, d.getDays().get(pd), ID))
					success = false;
		}
		return success;
	}

	public static boolean transmitTable(DayTable d, DayTable o, int ID, ProgressChangedNotifier pcn) {
		boolean success = true;
		int max = d.getDays().size();
		int current = 1;
		for (ParaDate pd : d.getDays().keySet()) {
			if ((o != null &&
					o.getDays().get(o.findCorresponding(pd))
					.equals(d.getDays().get(pd))) || (o == null))
				if (!setDay(pd, d.getDays().get(pd), ID))
					success = false;
			pcn.progressChanged(1, max, current);
			current++;
		}
		return success;
	}

	public static boolean transmitTable(DayTable d, DayTable o, int ID, ProgressChangedNotifier pcn, boolean fast) {
		if (!fast)
			return transmitTable(d, o, ID, pcn);
		else {
			boolean success = true;
			int max = d.getDays().size();
			int current = 0;
			StringBuilder sb = new StringBuilder();

			for (ParaDate pd : d.getDays().keySet()) {
				if ((o != null && o.getDays().get(o.findCorresponding(pd)).equals(d.getDays().get(pd))) || (o == null)) {
					sb.append("setDay ");
					sb.append(pd.toString());
					sb.append(' ');
					Status s = d.getDays().get(pd);
					sb.append(' ');
					sb.append(Integer.toString(ID));
					sb.append("\n");
				}
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
		block();
		c.transmit("getMessageHash"); //$NON-NLS-1$
		try {
			return c.receive();
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve message hash"); //$NON-NLS-1$
			e.printStackTrace();
			return null;
		} finally {
			release();
		}
	}

	public static boolean changePassword(String newPassword, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append("changePSWD "); //$NON-NLS-1$
		sb.append(id);
		sb.append(' ');
		sb.append(Base64Coding.encode(newPassword));
		block();
		c.transmit(sb.toString());
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
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
		block();
		c.transmit(sb.toString());
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
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
		block();
		c.transmit(sb.toString());
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve critical request answer:"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean destroyDatabase() {
		block();
		c.transmit("destroyAll");
		try {
			String[] a = decodeAnswer(c.receive());
			release();
			if (a[0].equals("false")) //$NON-NLS-1$
				return false;
		} catch (IOException e) {
			release();
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"Failed to recieve critical request answer:"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean isConnectionAviable() {
		return c.isValidConnection();
	}
	
	public static boolean transmitTableAsSingleFrame(DayTable dt){
		StringBuilder sb = new StringBuilder();
		sb.append("setRange ");
		for(ParaDate p : dt.getDays().keySet()){
			Status s = dt.getDays().get(p);
			sb.append(p.toString());
			sb.append('=');
			sb.append(s.toString());
			sb.append(';');
		}
		block();
		c.transmit(sb.toString());
		try {
			return Boolean.valueOf(c.receive());
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"An unexpected exception occurred: " + e.getMessage()); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		} finally {
			release();
		}
	}
	
	public static boolean isMaintaining(){
		block();
		c.transmit("isMaintaining");
		try {
			return Boolean.valueOf(c.receive());
		} catch (IOException e) {
			Console.log(LogType.Error, "ProtocolHandler", //$NON-NLS-1$
					"An unexpected exception occurred: " + e.getMessage()); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		} finally {
			release();
		}
	}

}
