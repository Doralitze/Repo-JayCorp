package org.technikradio.jay_corp.ui.helpers;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.technikradio.jay_corp.ui.Strings;

public class CSVFileFilter extends FileFilter {

	public CSVFileFilter() {
		super();
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		if (getEnding(f.getName()).equals("csv"))
			return true;
		return false;
	}

	private String getEnding(String s) {
		String[] sa = s.split("\\.");
		return sa[sa.length - 1];
	}

	@Override
	public String getDescription() {
		return Strings.getString("CSVFileFilter.0"); //$NON-NLS-1$
	}

}
