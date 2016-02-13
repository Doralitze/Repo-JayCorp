/*
JayCorp-Client/ProgressIndicator.java
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
package org.technikradio.jay_corp.ui.helpers;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.technikradio.jay_corp.ui.Strings;

public class ProgressIndicator extends JDialog {

	private JProgressBar pb;
	private JLabel infoLabel;

	private static final long serialVersionUID = -4545977169805276112L;

	public ProgressIndicator() throws HeadlessException {
		super();
		setup();
	}

	public ProgressIndicator(Frame parent) throws HeadlessException {
		super(parent);
		setup();
	}

	private void setup() {
		pb = new JProgressBar();
		pb.setMaximum(100);
		pb.setMinimum(0);
		pb.setSize(340, 25);
		infoLabel = new JLabel();
		infoLabel.setText(Strings.getString("ProgressIndicator.DefaultLabelText")); //$NON-NLS-1$
		this.setTitle(Strings.getString("ProgressIndicator.Title")); //$NON-NLS-1$
		this.add(pb);
		this.add(infoLabel);
		this.setSize(350, 100);
		if (this.getOwner() != null) {
			Window mf = this.getOwner();
			int x = mf.getLocation().x + ((mf.getWidth() / 2) - this.getWidth() / 2);
			int y = mf.getLocation().y + ((mf.getHeight() / 2) - this.getHeight() / 2);
			this.setLocation(x, y);
		}
		this.setResizable(false);
	}

	public void setProgress(int percent) {
		if (percent > 100 || percent < 0)
			throw new RuntimeException("Invalid percentage value recieved"); //$NON-NLS-1$
		pb.setValue(percent);
	}

	public void setValv(int min, int max, int prog) {
		if ((prog > max) || (prog < min))
			throw new RuntimeException(
					"Invalid percentage value recieved: min -> " + min + ", max -> " + max + ", value -> " + prog); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pb.setMaximum(max);
		pb.setMinimum(min);
		pb.setValue(prog);
	}

	public void setInfoLabelText(String text) {
		infoLabel.setText(text);
	}

}
