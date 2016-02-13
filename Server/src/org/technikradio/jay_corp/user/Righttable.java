/*
JayCorp-Server/Righttable.java
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

public class Righttable {
	private boolean addUserAllowed;
	private boolean editUserAllowed;
	private boolean accessUserInputAllowed;
	private boolean editUserInputAllowed;
	private boolean openCloseEditAllowed;
	private boolean viewOtherSelectionsAllowed;
	private boolean listAllUsersAllowed;
	private boolean getIDCountAllowed;
	public boolean isAddUserAllowed() {
		return addUserAllowed;
	}
	public void setAddUserAllowed(boolean addUserAllowed) {
		this.addUserAllowed = addUserAllowed;
	}
	public boolean isEditUserAllowed() {
		return editUserAllowed;
	}
	public void setEditUserAllowed(boolean editUserAllowed) {
		this.editUserAllowed = editUserAllowed;
	}
	public boolean isAccessUserInputAllowed() {
		return accessUserInputAllowed;
	}
	public void setAccessUserInputAllowed(boolean accessUserInputAllowed) {
		this.accessUserInputAllowed = accessUserInputAllowed;
	}
	public boolean isEditUserInputAllowed() {
		return editUserInputAllowed;
	}
	public void setEditUserInputAllowed(boolean editUserInputAllowed) {
		this.editUserInputAllowed = editUserInputAllowed;
	}
	public boolean isOpenCloseEditAllowed() {
		return openCloseEditAllowed;
	}
	public void setOpenCloseEditAllowed(boolean openCloseEditAllowed) {
		this.openCloseEditAllowed = openCloseEditAllowed;
	}
	public boolean isViewOtherSelectionsAllowed() {
		return viewOtherSelectionsAllowed;
	}
	public void setViewOtherSelectionsAllowed(boolean viewOtherSelectionsAllowed) {
		this.viewOtherSelectionsAllowed = viewOtherSelectionsAllowed;
	}
	public boolean isListAllUsersAllowed() {
		return listAllUsersAllowed;
	}
	public void setListAllUsersAllowed(boolean listAllUsersAllowed) {
		this.listAllUsersAllowed = listAllUsersAllowed;
	}
	public boolean isGetIDCountAllowed() {
		return getIDCountAllowed;
	}
	public void setGetIDCountAllowed(boolean getIDCountAllowed) {
		this.getIDCountAllowed = getIDCountAllowed;
	}
	
}
