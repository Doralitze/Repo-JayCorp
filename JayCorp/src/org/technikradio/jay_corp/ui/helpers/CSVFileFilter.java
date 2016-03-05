/*
JayCorp-Client/CSVFileFilter.java
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

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.technikradio.jay_corp.ui.Strings;

public class CSVFileFilter extends FileFilter {

	boolean alt = false;

	public CSVFileFilter() {
		super();
	}

	@Override
	public boolean accept(File f) {
		if (alt)
			return true;
		if (f.isDirectory())
			return true;
		if (getEnding(f.getName()).equals("csv")) //$NON-NLS-1$
			return true;
		return false;
	}

	private String getEnding(String s) {
		String[] sa = s.split("\\."); //$NON-NLS-1$
		return sa[sa.length - 1];
	}

	@Override
	public String getDescription() {
		return Strings.getString("CSVFileFilter.0"); //$NON-NLS-1$
	}

	public void setAlwaysAccept(boolean b) {
		alt = b;
	}

}
