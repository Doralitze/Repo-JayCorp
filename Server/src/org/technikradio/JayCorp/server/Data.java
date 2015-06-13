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
	private static ArrayList<User> users;
	private static Hashtable<User, MetaSheet> meta;
	private static DayTable defaultConfiguration;
	private static boolean editEnabled;
	private static File file;

	@XmlRootElement(name = "database")
	private static class Loader {

		// @XmlElement(required = true, name = "defaultConfiguration")
		private DayTable dc;
		private boolean editEnabled;
		private String lastVersion;
		// @XmlElementWrapper(name = "users", required = true)
		// @XmlElement(required = true)
		private ArrayList<User> user;
		private Hashtable<User, MetaSheet> meta;

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

		public Hashtable<User, MetaSheet> getMeta() {
			return meta;
		}

		public void setMeta(Hashtable<User, MetaSheet> meta) {
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

		public ArrayList<User> getUsers() {
			return user;
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
			if (u.getUsername().equals(userName)
					|| u.getName().equals(userName)) {
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
			Console.log(LogType.Error, "Database",
					"An unexpected error occured:");
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
			Console.log(LogType.Warning, "Database",
					"provided database not found. creating new one.");
			defaultConfiguration = Initiator.getDefaultDayTable();
			User ru = Initiator.getRootUser();
			users.add(ru);
			ru.setSelectedDays(defaultConfiguration.clone());

			ParaDate[] s = ru
					.getSelectedDays()
					.getDays()
					.keySet()
					.toArray(
							new ParaDate[ru.getSelectedDays().getDays()
									.keySet().size()]);
			for (ParaDate pd : s) {
				ru.getSelectedDays().getDays().remove(pd);
				ru.getSelectedDays().getDays().put(pd, Status.allowed);
			}
			save();
			Console.log(LogType.Information, "Database",
					"Successfully created new Database");
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
				Console.log(LogType.StdOut, "Database",
						"Importing depreached database");
				loadOldDataFile(file);
			}
		}
		checkDatabase();
	}

	public static final void loadOldDataFile(String file) {
		users = new ArrayList<User>();
		defaultConfiguration = new DayTable();
		File f = new File(file);
		Data.file = f;
		OldLoader pl = JAXB.unmarshal(f, OldLoader.class);
		if (pl == null) {

		} else {
			users = pl.getUsers();
			editEnabled = pl.isEditEnabled();
			defaultConfiguration = pl.getDefaultConfiguration();
		}
	}

	public static int getUserCount() {
		return users.size();
	}

	public static void checkDatabase() {
		Console.log(LogType.StdOut, "Database",
				"Checking database for corrupted data");
		boolean ok = true;
		if (meta == null) {
			meta = new Hashtable<User, MetaSheet>();
		}
		for (User u : users) {
			if (!meta.contains(u))
				ok = false;
			else {
				MetaSheet s = meta.get(u);
				if (s.getAssoziatedUser() != u.getID())
					ok = false;
			}
		}
		postInit();
		if (!ok)
			repairDatabase();
	}

	private static void repairDatabase() {
		Console.log(LogType.Warning, "Database",
				"Some corrupted data was found. The system now trys to correct it.");
		for (User u : users) {
			if (!meta.contains(u)) {
				Console.log(LogType.Information, "Database",
						"Adding missing meta data for user " + u.getName());
				MetaSheet s = new MetaSheet(u.getID());
				MetaReg.setDefaultMetaData(s);
			} else {
				Console.log(LogType.Information, "Database",
						"Rerouting incorrect placed data of " + u.getName());
				MetaSheet s = meta.get(u);
				if (u.getID() != s.getAssoziatedUser()) {
					meta.remove(u);
					meta.put(getUser(s.getAssoziatedUser()), s);
				}
			}
		}
	}

	public static int setEntry(User target, String key, String value) {
		int success = 0;
		MetaSheet s = meta.get(target);
		if (s != null) {
			success++;
			if (s.setValue(key, value))
				success++;
		}
		return success;
	}

	public static String getEntry(User target, String key) {
		MetaSheet s = meta.get(target);
		if (s == null)
			return "";
		return s.getValue(key);
	}

	private static void postInit() {
		if (users == null)
			users = new ArrayList<User>();
	}
}
