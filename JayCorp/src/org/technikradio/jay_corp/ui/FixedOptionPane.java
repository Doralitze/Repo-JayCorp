/*
JayCorp-Client/FixedOptionPane.java
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

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class FixedOptionPane extends JOptionPane {

	private static final long serialVersionUID = -162117355793997197L;

	public FixedOptionPane() {
		super();
		setup();
	}

	public FixedOptionPane(Object message) {
		super(message);
		setup();
	}

	public FixedOptionPane(Object message, int messageType) {
		super(message, messageType);
		setup();
	}

	public FixedOptionPane(Object message, int messageType, int optionType) {
		super(message, messageType, optionType);
		setup();
	}

	public FixedOptionPane(Object message, int messageType, int optionType,
			Icon icon) {
		super(message, messageType, optionType, icon);
		setup();
	}

	public FixedOptionPane(Object message, int messageType, int optionType,
			Icon icon, Object[] options) {
		super(message, messageType, optionType, icon, options);
		setup();
	}

	public FixedOptionPane(Object message, int messageType, int optionType,
			Icon icon, Object[] options, Object initialValue) {
		super(message, messageType, optionType, icon, options, initialValue);
		setup();
	}

	private void setup() {
		this.getRootPane().putClientProperty("apple.awt.documentModalSheet", //$NON-NLS-1$
				Boolean.TRUE);
	}

	@SuppressWarnings("deprecation")
	public static int showFixedOptionDialog(Component parentComponent,
			Object message, String title, int optionType, int messageType,
			Icon icon, Object[] options, Object initialValue)
			throws HeadlessException {

		JOptionPane pane = new JOptionPane(message, messageType, optionType,
				icon, options, initialValue);

		pane.setInitialValue(initialValue);
		pane.setComponentOrientation(((parentComponent == null) ? getRootFrame()
				: parentComponent).getComponentOrientation());

		JDialog dialog = pane.createDialog(parentComponent, title);
		pane.getRootPane().putClientProperty("apple.awt.documentModalSheet", //$NON-NLS-1$
				Boolean.TRUE);
		dialog.getRootPane().putClientProperty("apple.awt.documentModalSheet", //$NON-NLS-1$
				Boolean.TRUE);
		dialog.setModal(true);
		pane.selectInitialValue();
		dialog.show();
		dialog.dispose();

		Object selectedValue = pane.getValue();

		if (selectedValue == null)
			return CLOSED_OPTION;
		if (options == null) {
			if (selectedValue instanceof Integer)
				return ((Integer) selectedValue).intValue();
			return CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if (options[counter].equals(selectedValue))
				return counter;
		}
		return CLOSED_OPTION;
	}

}
