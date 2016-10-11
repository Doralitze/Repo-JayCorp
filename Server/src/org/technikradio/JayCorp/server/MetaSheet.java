/*
JayCorp-Server/MetaSheet.java
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
