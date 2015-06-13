package org.technikradio.JayCorp.server;

import java.util.Hashtable;

public class MetaSheet {

	private Hashtable<String, String> entrys;
	private int assoziatedUser = -1;

	public MetaSheet() {
		entrys = new Hashtable<String, String>();
	}

	public MetaSheet(int user) {
		setAssoziatedUser(user);
		entrys = new Hashtable<String, String>();
	}

	public Hashtable<String, String> getEntrys() {
		return entrys;
	}

	public void setEntrys(Hashtable<String, String> entrys) {
		this.entrys = entrys;
	}

	public boolean setValue(String key, String value) {
		String old = entrys.put(key, value);
		if (old != null)
			if (old.isEmpty())
				return true;
			else
				return false;
		else
			return true;
	}

	public String getValue(String key) {
		return entrys.get(key);
	}

	public int getAssoziatedUser() {
		if (assoziatedUser == -1)
			throw new RuntimeException("No user set");
		return assoziatedUser;
	}

	public void setAssoziatedUser(int assoziatedUser) {
		this.assoziatedUser = assoziatedUser;
	}

}
