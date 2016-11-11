/*
JayCorp-Client/Application.java
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
package org.technikradio.jay_corp;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

public class Application {

	private static final String APP_PATH;
	public static final OperatingSystem OS;
	public static final String OS_VERSION;
	public static final String OS_ARCH;

	public static enum OperatingSystem {
		Windows, OSX, Linux, BSD, UNIX, Solaris, Unknown;

	}

	static {
		String s = null;
		try {
			s = Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (s != null)
			APP_PATH = new File(s).getAbsolutePath();
		else
			APP_PATH = null;

		OS_ARCH = System.getProperty("os.arch");
		OS_VERSION = System.getProperty("os.version");

		String os = System.getProperty("os.name");
		os = os.toLowerCase();
		if (os.indexOf("win") >= 0)
			OS = OperatingSystem.Windows;
		else if (os.indexOf("mac") >= 0)
			OS = OperatingSystem.OSX;
		else if (os.indexOf("nix") >= 0 || os.indexOf("aix") > 0)
			OS = OperatingSystem.UNIX;
		else if (os.indexOf("sunos") >= 0)
			OS = OperatingSystem.Solaris;
		else if (os.indexOf("nux") >= 0)
			OS = OperatingSystem.Linux;
		else if (os.indexOf("bsd") >= 0)
			OS = OperatingSystem.BSD;
		else
			OS = OperatingSystem.Unknown;

	}

	public static String getAppInstallPath() {
		return APP_PATH;
	}

	public static void crash(Throwable e) {
		try{
			JOptionPane.showMessageDialog(null, "An unknown error occured.",
					e.getLocalizedMessage() + "\n\n" + getStrackString(e), JOptionPane.ERROR_MESSAGE);
		} catch(Exception e1) {
			
		}
	}

	private static String getStrackString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] elements = e.getStackTrace();
		for(int i = 0; i < elements.length; i++){
			sb.append(elements[i].toString());
		}
		return sb.toString();
	}

}
