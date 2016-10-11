/*
JayCorp-Server/Application.java
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

import java.io.File;
import java.net.URISyntaxException;

public class Application {

	private static final String APP_PATH;

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
	}

	public static String getAppInstallPath() {
		return APP_PATH;
	}

}
