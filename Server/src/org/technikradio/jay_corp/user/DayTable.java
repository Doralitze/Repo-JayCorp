/*
JayCorp-Server/DayTable.java
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

import java.util.Hashtable;

import org.technikradio.task.ID;
import org.technikradio.universal_tools.ParaDate;

public class DayTable {

	public static final Status defaultStatus = Status.normal;

	public enum Status {
		normal, allowed, selected, undefined;
		public final ID ptr = new ID("StatePtr", false);

		public static Status valueOf(int ordinal) throws Exception {
			switch (ordinal) {
			case 0:
				return Status.normal;
			case 1:
				return Status.allowed;
			case 2:
				return Status.selected;
			case 3:
				return Status.undefined;
			default:
				throw new Exception("Invalid status");
			}
		}
	}

	private Hashtable<ParaDate, Status> days;
	private int connectedUser = -1;

	public void setConnectedUser(int connectedUser) {
		this.connectedUser = connectedUser;
	}

	public int getConnectedUser() {
		return connectedUser;
	}

	public Hashtable<ParaDate, Status> getDays() {
		return days;
	}

	public void setDays(Hashtable<ParaDate, Status> days) {
		this.days = days;
	}

	public DayTable() {
		days = new Hashtable<ParaDate, Status>();
	}

	public int getYear() {
		for (ParaDate p : days.keySet())
			return p.getYear();
		return 0;
	}

	@SuppressWarnings("unchecked")
	public DayTable clone() {
		DayTable dt = new DayTable();
		Object o = this.getDays().clone();
		if (o instanceof Hashtable<?, ?>) {
			dt.setDays((Hashtable<ParaDate, Status>) o);
			Hashtable<ParaDate, Status> a = dt.getDays();
			for (ParaDate p : this.getDays().keySet()) {
				a.put(ParaDate.valueOf(p.toString()), Status.valueOf(this.getDays().get(p).toString()));
			}
		} else
			throw new IllegalStateException("Illegal clone operation");
		return dt;
	}

	public ParaDate findCorresponding(ParaDate pd) {
		String key = pd.getMinimalDate();
		for (ParaDate a : getDays().keySet()) {
			if (a.getMinimalDate().equals(key))
				return a;
		}
		return null;
	}
}
