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
import java.util.Hashtable;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;

import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;

public class Data {

	private static final boolean crt = Boolean.parseBoolean(Settings.getString("Settings.amdCrt"));

	private static ArrayList<User> users;
	private static ArrayList<MetaSheet> meta;
	private static DayTable defaultConfiguration;
	private static boolean editEnabled;
	private static File file;
	
	private static boolean isCurrentlySaving = false;

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
		private String lastVersion;
		// @XmlElementWrapper(name = "users", required = true)
		// @XmlElement(required = true)
		private ArrayList<User> user;
		private Hashtable<Integer, MetaSheet> meta;

		public ArrayList<User> getUsers() {
			return user;
		}

		public void setUsers(ArrayList<User> users) {
			this.user = users;
		}

		public OldLoader() {
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

		public Hashtable<Integer, MetaSheet> getMeta() {
			return meta;
		}

		public void setMeta(Hashtable<Integer, MetaSheet> meta) {
			this.meta = meta;
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
		if(isCurrentlySaving)
			return true;
		try {
			isCurrentlySaving = true;
			Loader pl = new Loader();
			pl.setUsers(users);
			pl.setEditEnabled(editEnabled);
			pl.setDefaultConfiguration(defaultConfiguration);
			pl.setLastVersion(Server.VERSION);
			pl.setMeta(meta);
			JAXB.marshal(pl, file);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			isCurrentlySaving = false;
		}
		return true;
	}

	private static boolean save(String file) {
		try {
			Loader pl = new Loader();
			pl.setUsers(users);
			pl.setEditEnabled(editEnabled);
			pl.setDefaultConfiguration(defaultConfiguration);
			pl.setLastVersion(Server.VERSION);
			pl.setMeta(meta);
			JAXB.marshal(pl, file);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
			checkDatabase();
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
				meta = pl.getMeta();
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
				meta = new ArrayList<MetaSheet>();
		}
		checkDatabase();
	}

	public static final void destroyDatabase() {
		save(file.getAbsolutePath() + Long.toString(System.currentTimeMillis() / 1000) + ".backup");
		users = new ArrayList<User>();
		defaultConfiguration = new DayTable();
		meta = null;
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
		if (meta == null) {
			meta = new ArrayList<MetaSheet>();
		}
		boolean ok = true;
		for (User u : users) {
			try {
				if (meta.get(u.getID()) == null)
					ok = false;
				else {
					MetaSheet s = meta.get(u.getID());
					if (s.getAssoziatedUser() != u.getID())
						ok = false;
				}
			} catch (Exception e) {
				ok = false;
			}
		}
		postInit();
		if (!ok)
			repairDatabase();
	}

	private static void repairDatabase() {
		if (crt)
			Console.log(LogType.Warning, "Database",
					"Some corrupted data was found. The system now trys to correct it.");
		for (User u : users) {
			try {
				MetaSheet ms = meta.get(u.getID());
				if (ms == null) {
					if (crt)
						Console.log(LogType.Information, "Database", "Adding missing meta data for user " + u.getName());
					MetaSheet s = new MetaSheet(u.getID());
					MetaReg.setDefaultMetaData(s);
					meta.add(u.getID(), s);
				} else {
					if (crt)
						Console.log(LogType.Information, "Database", "Rerouting incorrect placed data of " + u.getName());
					if (u.getID() != ms.getAssoziatedUser()) {
						meta.remove(ms);
						meta.add(getUser(ms.getAssoziatedUser()).getID(), ms);
					}
				}
			} catch (NullPointerException e) {
				if (crt)
					Console.log(LogType.Information, "Database", "Adding missing meta data for user " + u.getName());
				MetaSheet s = new MetaSheet(u.getID());
				MetaReg.setDefaultMetaData(s);
				meta.add(u.getID(), s);
			}
		}
	}

	public static int setEntry(User target, String key, String value) {
		int success = 0;
		MetaSheet s = meta.get(target.getID());
		if (s != null) {
			success++;
			if (s.setValue(key, value))
				success++;
		}
		return success;
	}

	public static String getEntry(User target, String key) {
		MetaSheet s = meta.get(target.getID());
		if (s == null)
			return "";
		return s.getValue(key);
	}

	private static void postInit() {
		if (users == null)
			users = new ArrayList<User>();
	}
}
