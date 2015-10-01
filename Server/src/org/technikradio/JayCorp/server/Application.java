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
