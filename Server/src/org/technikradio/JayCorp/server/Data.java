/*
JayCorp-Server/Data.java
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

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;

import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;
import org.technikradio.universal_tools.Time;

public class Data {

	private static final boolean crt = Boolean.parseBoolean(Settings.getString("Settings.amdCrt"));

	private static ArrayList<User> users;
	private static DayTable defaultConfiguration;
	private static boolean editEnabled;
	private static File file;

	private static boolean isCurrentlySaving = false;
	private static Thread saveAwaitingThread;

	@XmlRootElement(name = "database")
	private static class Loader {

		// @XmlElement(required = true, name = "defaultConfiguration")
		private DayTable dc;
		private boolean editEnabled;
		private String lastVersion;
		// @XmlElementWrapper(name = "users", required = true)
		// @XmlElement(required = true)
		private ArrayList<User> user;
		private ArrayList<MetaSheet> meta;

		public ArrayList<User> getUsers() {
			return user;
		}

		public void setUsers(ArrayList<User> users) {
			this.user = users;
		}

		public Loader() {
			user = new ArrayList<User>();
		}

		public DayTable getDefaultConfiguration() {
			return dc;
		}

		public void setDefaultConfiguration(DayTable defaultConfiguration) {
			this.dc = defaultConfiguration;
		}

		public boolean isEditEnabled() {
			return editEnabled;
		}

		public void setEditEnabled(boolean editEnabled) {
			this.editEnabled = editEnabled;
		}

		public String getLastVersion() {
			return lastVersion;
		}

		public void setLastVersion(String lastVersion) {
			this.lastVersion = lastVersion;
		}

		public ArrayList<MetaSheet> getMeta() {
			return meta;
		}

		public void setMeta(ArrayList<MetaSheet> meta) {
			this.meta = meta;
		}
	}

	@XmlRootElement(name = "database")
	private static class OldLoader {

		// @XmlElement(required = true, name = "defaultConfiguration")
		private DayTable dc;
		private boolean editEnabled;
		@SuppressWarnings("unused")
		private String lastVersion;
		// @XmlElementWrapper(name = "users", required = true)
		// @XmlElement(required = true)
		private ArrayList<User> user;
		@SuppressWarnings("unused")
		private Hashtable<Integer, MetaSheet> meta;

		public ArrayList<User> getUsers() {
			return user;
		}

		@SuppressWarnings("unused")
		public OldLoader() {
			user = new ArrayList<User>();
		}

		public DayTable getDefaultConfiguration() {
			return dc;
		}

		public boolean isEditEnabled() {
			return editEnabled;
		}
	}

	public static DayTable getDefaultConfiguration() {
		return defaultConfiguration;
	}

	public static void setDefaultConfiguration(DayTable defaultConfiguration) {
		Data.defaultConfiguration = defaultConfiguration;
	}

	public static boolean isEditEnabled() {
		return editEnabled;
	}

	public static void setEditEnabled(boolean editEnabled) {
		Data.editEnabled = editEnabled;
	}

	public static boolean save() {
		return save(file.getAbsolutePath());
	}

	@SuppressWarnings("unchecked")
	private static boolean save(String filepath) {
		if (isCurrentlySaving){
			scheduleSaveLater();
			return true;
		}
		try {
			isCurrentlySaving = true;
			File newFile = new File(filepath + ".part");
			Loader pl = new Loader();
			pl.setUsers((ArrayList<User>) users.clone());
			pl.setEditEnabled(editEnabled);
			pl.setDefaultConfiguration(defaultConfiguration.clone());
			pl.setLastVersion(Server.VERSION);
			JAXB.marshal(pl, newFile);
			File oldfile = new File(filepath);
			if(oldfile.exists()){
				if(!oldfile.delete()){
					Console.log(LogType.Warning, "Database", "Something went wrong while deleting the old file.");
				}
			}
			if(!newFile.renameTo(oldfile)){
				Console.log(LogType.Warning, "Database", "Something went wrong doing part file renaming.");
			}
		} catch (ConcurrentModificationException e){
			Console.log(LogType.Warning, "Database", "Requested to save data while iterating. Is the server overloaded?");
			scheduleSaveLater();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			isCurrentlySaving = false;
		}
		return true;
	}

	private static void scheduleSaveLater() {
		if(saveAwaitingThread != null)
			return;
		saveAwaitingThread = new Thread(new Runnable(){

			@Override
			public void run() {
				saveAwaitingThread.setPriority(Thread.MIN_PRIORITY);
				while(Server.usersConnected()){
					try {
						Thread.sleep(1000 * 60 * 5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				saveAwaitingThread.setPriority(Thread.MAX_PRIORITY);
				saveAwaitingThread = null;
				Data.save();
			}});
		saveAwaitingThread.setDaemon(true);
		saveAwaitingThread.setName("SAVING-SCHEDULE-THREAD");
		saveAwaitingThread.start();
	}

	public static User getUser(String userName) {
		User user = null;
		for (User u : users) {
			if (u.getUsername().equals(userName) || u.getName().equals(userName)) {
				user = u;
				break;
			}
		}
		return user;
	}

	public static User getUser(int ID) {
		User user = null;
		for (User u : users) {
			if (u.getID() == ID) {
				user = u;
				break;
			}
		}
		return user;
	}

	public static boolean addUser(User u) {
		try {
			if (u.getID() == 0)
				u.setID(getLatestID() + 1);
			return users.add(u);
		} catch (Exception e) {
			Console.log(LogType.Error, "Database", "An unexpected error occured:");
			e.printStackTrace();
			return false;
		} finally {
			// checkDatabase();
		}
	}

	public static int getLatestID() {

		int max = 0;
		for (User u : users) {
			if (u.getID() > max)
				max = u.getID();
		}
		return max;
	}

	public static int[] getAllIDs() {
		int[] a = new int[users.size()];
		int i = 0;
		for (User u : users) {
			a[i] = u.getID();
			i++;
		}
		return a;
	}

	public static final void loadDataFile(String file) {
		users = new ArrayList<User>();
		defaultConfiguration = new DayTable();
		File f = new File(file);
		Data.file = f;
		if (!(f.exists() && !f.isDirectory())) {
			Console.log(LogType.Warning, "Database", "provided database not found. creating new one.");
			defaultConfiguration = Initiator.getDefaultDayTable();
			User ru = Initiator.getRootUser();
			users.add(ru);
			ru.setSelectedDays(defaultConfiguration.clone());

			ParaDate[] s = ru.getSelectedDays().getDays().keySet()
					.toArray(new ParaDate[ru.getSelectedDays().getDays().keySet().size()]);
			for (ParaDate pd : s) {
				ru.getSelectedDays().getDays().remove(pd);
				ru.getSelectedDays().getDays().put(pd, Status.allowed);
			}
			save();
			Console.log(LogType.Information, "Database", "Successfully created new Database");
		}
		Loader pl = JAXB.unmarshal(f, Loader.class);
		if (pl == null) {

		} else {
			if (pl.getLastVersion().equals(Server.VERSION)) {
				users = pl.getUsers();
				editEnabled = pl.isEditEnabled();
				defaultConfiguration = pl.getDefaultConfiguration();
			} else {
				Console.log(LogType.StdOut, "Database", "Importing depreached database");
				loadOldDataFile();
			}
		}
		checkDatabase();
	}

	public static final void loadOldDataFile() {
		OldLoader pl = JAXB.unmarshal(Data.file, OldLoader.class);
		if (pl == null) {

		} else {
			users = pl.getUsers();
			editEnabled = pl.isEditEnabled();
			defaultConfiguration = pl.getDefaultConfiguration();
		}
		checkDatabase();
	}

	public static final void destroyDatabase() {
		save(file.getAbsolutePath() + Long.toString(System.currentTimeMillis() / 1000) + ".backup");
		users = new ArrayList<User>();
		defaultConfiguration = new DayTable();
		editEnabled = false;
		Console.log(LogType.Warning, "Database", "Destroying database on request of root. Creating new one.");
		defaultConfiguration = Initiator.getDefaultDayTable();
		User ru = Initiator.getRootUser();
		users.add(ru);
		ru.setSelectedDays(defaultConfiguration.clone());

		ParaDate[] s = ru.getSelectedDays().getDays().keySet()
				.toArray(new ParaDate[ru.getSelectedDays().getDays().keySet().size()]);
		for (ParaDate pd : s) {
			ru.getSelectedDays().getDays().remove(pd);
			ru.getSelectedDays().getDays().put(pd, Status.allowed);
		}
		checkDatabase();
		save();
		Console.log(LogType.Information, "Database", "Successfully created new Database");
	}

	public static int getUserCount() {
		return users.size();
	}

	public static void checkDatabase() {
		Console.log(LogType.Information, "Database", "Checking database.");
		if (users == null) {
			if (crt)
				Console.log(LogType.Warning, "Database", "Creating new user table.");
			users = new ArrayList<User>();
		}
		// perform user double dates checking using multithreading
		final int oldprio = Thread.currentThread().getPriority();
		try {
			final Iterator<User> users;
			//Perform a database check for default configuration
			DayTable dt = getDefaultConfiguration();
			if(dt.getDays().size() > 365){
				setDefaultConfiguration(sortedArrayToList(listToSortedArray(dt), dt.getYear()));
			}
			//Collect users and put them inside a set
			{
				ArrayList<User> list = new ArrayList<User>();
				for (int id = 1; id <= Data.getLatestID(); id++) {
					User _user = Data.getUser(id);
					if (_user != null) {
						list.add(_user);
					}
				}
				users = list.iterator();
			}
			Runnable r = new Runnable() {

				@Override
				public void run() {
					while(users.hasNext()){
						User u = users.next();
						if(u == null)
							break;
						if(!u.isPasswordHashed() && !u.getPassword().equals("password")) { //TODO change to dynamic initial password
							u.setPasswordHashed(true);
							u.setPassword(encrypt(u.getPassword()));
							if(crt)
								Console.log(LogType.Information, "Database", "Hashing password of user " + u.getID());
						}
						DayTable dt = u.getSelectedDays();
						if(dt.getDays().size() > 365){
							u.setSelectedDays(sortedArrayToList(listToSortedArray(dt), dt.getYear()));
						} else {
							if(crt)
								Console.log(LogType.Information, "Database", "Skipping user " + u.getUsername() + " [" + u.getID() + "] because he seams to have legid data.");
						}
					}
				}
			};

			final int cores = Runtime.getRuntime().availableProcessors();
			
			Thread[] threads = new Thread[cores];
			for (int i = 0; i < cores; i++) {
				threads[i] = new Thread(r);
				threads[i].setName("DBUPDATE_THREAD[" + Integer.toHexString(i) + "]");
				threads[i].setPriority(Thread.MAX_PRIORITY);
			}
			Console.log(LogType.StdOut, "Database", "Spawning " + cores + " threads for computing.");
			for (int i = 0; i < cores; i++) {
				threads[i].start();
			}
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			boolean done = false;
			while (!done) {
				Thread.sleep(5);
				done = true;
				for (int i = 0; i < cores; i++) {
					if (threads[i].isAlive())
						done = false;
				}
			}
		} catch (Exception e){
			Console.log(LogType.Error, "Database", "An exception occured inside the db checker thread:");
			e.printStackTrace();
		} finally {
			Thread.currentThread().setPriority(oldprio);
			Console.log(LogType.StdOut, "Database", "Finished data integrity checks.");
		}

	}

	private static Status[][] listToSortedArray(DayTable t) {
		Status[][] sar = new Status[13][32];
		Set<ParaDate> set = t.getDays().keySet();
		for (ParaDate p : set) {
			Status s = t.getDays().get(p);
			sar[p.getMonth()][p.getDay()] = s;
		}
		return sar;
	}

	private static DayTable sortedArrayToList(Status[][] sar, int year) {
		DayTable dt = new DayTable();
		for (short m = 1; m <= 12; m++)
			for (short t = 1; t <= 31; t++) {
				if (sar[m][t] != null) {
					ParaDate pd = new ParaDate();
					pd.setDay(t);
					pd.setMonth(m);
					pd.setYear(year);
					Time tm = new Time();
					tm.setHours((short) 0);
					tm.setMillis(0);
					tm.setMinutes((short) 0);
					tm.setNanos(0);
					tm.setSeconds((short) 0);
					tm.setInitedFlag();
					pd.setTime(tm);
					dt.getDays().put(pd, sar[m][t]);
				}
			}
		return dt;
	}

	public static boolean doesPasswordMatch(int ID, String password) {
		try {
			User um = getUser(ID);
			if(!um.isPasswordHashed()){
				if(um.getPassword().equals(password))
					return true;
			} else {
				if(um.getPassword().equals(encrypt(password)))
					return true;
				}
		} catch (NullPointerException e) {
			return false;
		}
		return false;
	}

	public static void setPasswordOfUser(User u, String newPassword) {
		if(!newPassword.equals("password")) { //TODO read real initial password
			u.setPasswordHashed(false);
			u.setPassword(encrypt(newPassword));
			u.setPasswordHashed(true);
		} else {
			u.setPassword(newPassword);
			u.setPasswordHashed(false);
		}
	}

	private static String encrypt(String password) {
		return password; //TODO change
	}
}
