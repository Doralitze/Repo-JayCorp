/*
JayCorp-Client/RightEditFrame.java
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
package org.technikradio.jay_corp.ui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import org.technikradio.jay_corp.user.Righttable;

public class RightEditFrame extends JDialog {

	private static final long serialVersionUID = -746998591914486062L;
	private boolean positiveStatus = false;
	private Righttable rt;
	private RightEditFrame ownHandle;
	private JCheckBox addUserAllowedCB;
	private JCheckBox editUserAllowedCB;
	private JCheckBox accessUserInputAllowedCB;
	private JCheckBox editUserInputAllowedCB;
	private JCheckBox openCloseEditAllowedCB;
	private JCheckBox viewOtherSelectionsAllowedCB;
	private JCheckBox listAllUsersAllowedCB;
	private JCheckBox getIDCountAllowedCB;
	private JButton okButton;
	private JButton cancleButton;

	public RightEditFrame() throws HeadlessException {
		super();
		setup();
	}

	public RightEditFrame(Righttable rt) throws HeadlessException {
		super();
		setup();
		this.setRighttable(rt);
	}

	private void setup() {
		ownHandle = this;
		this.setLayout(null);
		this.setSize(400, 300);
		this.setTitle(Strings.getString("RightEditFrame.Title")); //$NON-NLS-1$
		addUserAllowedCB = new JCheckBox();
		editUserAllowedCB = new JCheckBox();
		accessUserInputAllowedCB = new JCheckBox();
		editUserInputAllowedCB = new JCheckBox();
		openCloseEditAllowedCB = new JCheckBox();
		viewOtherSelectionsAllowedCB = new JCheckBox();
		listAllUsersAllowedCB = new JCheckBox();
		getIDCountAllowedCB = new JCheckBox();
		okButton = new JButton();
		cancleButton = new JButton();
		addUserAllowedCB.setText(Strings.getString("RightEditFrame.AddUser")); //$NON-NLS-1$
		editUserAllowedCB.setText(Strings.getString("RightEditFrame.EditUser")); //$NON-NLS-1$
		accessUserInputAllowedCB.setText(Strings
				.getString("RightEditFrame.AccessUserInput")); //$NON-NLS-1$
		editUserInputAllowedCB.setText(Strings
				.getString("RightEditFrame.EditUserInput")); //$NON-NLS-1$
		openCloseEditAllowedCB.setText(Strings
				.getString("RightEditFrame.OpenCloseEdit")); //$NON-NLS-1$
		viewOtherSelectionsAllowedCB.setText(Strings
				.getString("RightEditFrame.ViewOtherSelections")); //$NON-NLS-1$
		listAllUsersAllowedCB.setText(Strings
				.getString("RightEditFrame.ListAllUsers")); //$NON-NLS-1$
		getIDCountAllowedCB.setText(Strings
				.getString("RightEditFrame.GetIDCount")); //$NON-NLS-1$
		okButton.setText(Strings.getString("RightEditFrame.OK")); //$NON-NLS-1$
		cancleButton.setText(Strings.getString("RightEditFrame.Cancle")); //$NON-NLS-1$
		addUserAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.AddUserToolTip")); //$NON-NLS-1$
		editUserAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.EditUserToolTip")); //$NON-NLS-1$
		accessUserInputAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.AccessUserInputToolTip")); //$NON-NLS-1$
		editUserInputAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.EditUserInputToolTip")); //$NON-NLS-1$
		openCloseEditAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.OpenCloseEditToolTip")); //$NON-NLS-1$
		viewOtherSelectionsAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.DisplayOtherSelectionsToolTip")); //$NON-NLS-1$
		listAllUsersAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.ListUsersToolTip")); //$NON-NLS-1$
		getIDCountAllowedCB.setToolTipText(Strings
				.getString("RightEditFrame.GetIDCountToolTip")); //$NON-NLS-1$
		okButton.setToolTipText(Strings.getString("RightEditFrame.OKToolTip")); //$NON-NLS-1$
		cancleButton.setToolTipText(Strings.getString("RightEditFrame.20")); //$NON-NLS-1$
		addUserAllowedCB.setSize(new Dimension(this.getWidth() - 10, 15));
		editUserAllowedCB.setSize(new Dimension(this.getWidth() - 10, 15));
		accessUserInputAllowedCB
				.setSize(new Dimension(this.getWidth() - 10, 15));
		editUserInputAllowedCB.setSize(new Dimension(this.getWidth() - 10, 15));
		openCloseEditAllowedCB.setSize(new Dimension(this.getWidth() - 10, 15));
		viewOtherSelectionsAllowedCB.setSize(new Dimension(
				this.getWidth() - 10, 15));
		listAllUsersAllowedCB.setSize(new Dimension(this.getWidth() - 10, 15));
		getIDCountAllowedCB.setSize(new Dimension(this.getWidth() - 10, 15));
		okButton.setSize(new Dimension(190, 25));
		cancleButton.setSize(new Dimension(190, 25));
		addUserAllowedCB.setLocation(5, 5);
		editUserAllowedCB.setLocation(5, 25);
		accessUserInputAllowedCB.setLocation(5, 45);
		editUserInputAllowedCB.setLocation(5, 65);
		openCloseEditAllowedCB.setLocation(5, 85);
		viewOtherSelectionsAllowedCB.setLocation(5, 105);
		listAllUsersAllowedCB.setLocation(5, 125);
		getIDCountAllowedCB.setLocation(5, 145);
		okButton.setLocation(5, 230);
		cancleButton.setLocation(205, 230);
		this.getContentPane().add(addUserAllowedCB);
		this.getContentPane().add(editUserAllowedCB);
		this.getContentPane().add(accessUserInputAllowedCB);
		this.getContentPane().add(editUserInputAllowedCB);
		this.getContentPane().add(openCloseEditAllowedCB);
		this.getContentPane().add(viewOtherSelectionsAllowedCB);
		this.getContentPane().add(listAllUsersAllowedCB);
		this.getContentPane().add(getIDCountAllowedCB);
		this.getContentPane().add(okButton);
		this.getContentPane().add(cancleButton);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				positiveStatus = true;
				ownHandle.setVisible(false);
			}
		});
		cancleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ownHandle.setVisible(false);
			}
		});
	}

	private void loadTable() {
		addUserAllowedCB.setSelected(rt.isAddUserAllowed());
		editUserAllowedCB.setSelected(rt.isEditUserAllowed());
		accessUserInputAllowedCB.setSelected(rt.isAccessUserInputAllowed());
		editUserInputAllowedCB.setSelected(rt.isEditUserAllowed());
		openCloseEditAllowedCB.setSelected(rt.isOpenCloseEditAllowed());
		viewOtherSelectionsAllowedCB.setSelected(rt
				.isViewOtherSelectionsAllowed());
		listAllUsersAllowedCB.setSelected(rt.isListAllUsersAllowed());
		getIDCountAllowedCB.setSelected(rt.isGetIDCountAllowed());
	}

	private void setTable() {
		rt.setAddUserAllowed(addUserAllowedCB.isSelected());
		rt.setEditUserAllowed(editUserAllowedCB.isSelected());
		rt.setAccessUserInputAllowed(accessUserInputAllowedCB.isSelected());
		rt.setEditUserInputAllowed(editUserInputAllowedCB.isSelected());
		rt.setOpenCloseEditAllowed(openCloseEditAllowedCB.isSelected());
		rt.setViewOtherSelectionsAllowed(viewOtherSelectionsAllowedCB
				.isSelected());
		rt.setListAllUsersAllowed(listAllUsersAllowedCB.isSelected());
		rt.setGetIDCountAllowed(getIDCountAllowedCB.isSelected());
	}

	public Righttable getRighttable() {
		setTable();
		return rt;
	}

	public void setRighttable(Righttable rt) {
		this.rt = rt;
		loadTable();
		repaint();
	}

	public static byte getRight(Righttable rt) {
		int b = 0;
		if (rt.isAddUserAllowed())
			b = b | 128;
		if (rt.isEditUserAllowed())
			b = b | 64;
		if (rt.isAccessUserInputAllowed())
			b = b | 32;
		if (rt.isEditUserInputAllowed())
			b = b | 16;
		if (rt.isOpenCloseEditAllowed())
			b = b | 8;
		if (rt.isViewOtherSelectionsAllowed())
			b = b | 4;
		if (rt.isListAllUsersAllowed())
			b = b | 2;
		if (rt.isGetIDCountAllowed())
			b = b | 1;
		return (byte) b;
	}

	public static Righttable showDialog(Righttable rt) {
		RightEditFrame ref = new RightEditFrame(rt);
		Righttable nrt;
		ref.setVisible(true);
		while (ref.isVisible())
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return rt;
			}
		ref.dispose();
		if (ref.positiveStatus)
			nrt = ref.getRighttable();
		else
			nrt = rt;
		ref.dispose();
		return nrt;
	}

}
