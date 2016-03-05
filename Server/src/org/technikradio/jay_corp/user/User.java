/*
JayCorp-Server/User.java
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
package org.technikradio.jay_corp.user;

public class User {
	private int ID;
	private int workAge;
	private int extraDays;
	private String password;
	private String name;
	private String username;
	private Righttable rights;
	private DayTable selectedDays;
	private DayTable backup;

	public User() {
		rights = new Righttable();
		selectedDays = new DayTable();
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getWorkAge() {
		return workAge;
	}

	public void setWorkAge(int workAge) {
		this.workAge = workAge;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Righttable getRights() {
		return rights;
	}

	public void setRights(Righttable rights) {
		this.rights = rights;
	}

	public DayTable getSelectedDays() {
		return selectedDays;
	}

	public void setSelectedDays(DayTable selectedDays) {
		this.selectedDays = selectedDays;
	}

	public int getExtraDays() {
		return extraDays;
	}

	public void setExtraDays(int extraDays) {
		this.extraDays = extraDays;
	}

	public DayTable getBackup() {
		return backup;
	}

	public void setBackup(DayTable backup) {
		this.backup = backup;
	}
}
