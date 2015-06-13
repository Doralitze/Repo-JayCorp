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
}
