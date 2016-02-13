/*
JayCorp-Server/Initiator.java
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

import java.util.Calendar;
import java.util.Hashtable;

import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.Righttable;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;
import org.technikradio.universal_tools.Time;

public class Initiator {

	public static final Status defaultStatus = Status.normal;

	public static DayTable getDefaultDayTable() {
		DayTable dt = new DayTable();
		int year = Calendar.getInstance().get(Calendar.YEAR)
				+ Integer.parseInt(Settings.getString("Settings.addToJear"));
		Console.log(LogType.StdOut, "Database",
				"Creating new default daytable with year: " + year);
		Hashtable<ParaDate, Status> ds = new Hashtable<ParaDate, Status>();
		if (year % 4 == 0) {
			// Schaltjahr: Februar = 29 Tage
			// Januar
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 1);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
			// Februar
			for (short i = 1; i <= 29; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 2);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
			// März
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 3);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
			// April
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 4);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Mai
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 5);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Juni
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 6);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Juli
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 7);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// August
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 8);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// September
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 9);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Oktober
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 10);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// November
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 11);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Dezember
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 12);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
		} else {
			// Januar
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 1);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
			// Februar
			for (short i = 1; i <= 28; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 2);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
			// März
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 3);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
			// April
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 4);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Mai
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 5);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Juni
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 6);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Juli
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 7);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// August
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 8);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// September
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 9);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Oktober
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 10);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// November
			for (short i = 1; i <= 30; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 11);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}// Dezember
			for (short i = 1; i <= 31; i++) {
				ParaDate pd = new ParaDate();
				pd.setDay(i);
				pd.setMonth((short) 12);
				pd.setYear(year);
				Time t = new Time();
				t.setHours((short) 0);
				t.setMillis(0);
				t.setMinutes((short) 0);
				t.setNanos(0);
				t.setSeconds((short) 0);
				t.setInitedFlag();
				pd.setTime(t);
				ds.put(pd, defaultStatus);
			}
		}
		dt.setDays(ds);
		return dt;
	}

	public static User getRootUser() {
		User u = new User();
		Righttable r = u.getRights();
		r.setAccessUserInputAllowed(true);
		r.setAddUserAllowed(true);
		r.setEditUserAllowed(true);
		r.setEditUserInputAllowed(true);
		r.setGetIDCountAllowed(true);
		r.setListAllUsersAllowed(true);
		r.setOpenCloseEditAllowed(true);
		r.setViewOtherSelectionsAllowed(true);
		u.setID(0);
		u.setName("root");
		u.setPassword("password");
		u.setUsername("root");
		u.setWorkAge(-1);
		Console.log(LogType.Warning, "Database",
				"Created superuser 'root' with password 'password'.");
		Console.log(LogType.Warning, "Database",
				"Please change the root password soon!");
		return u;
	}

}
