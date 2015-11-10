package org.technikradio.jay_corp;

import java.io.File;
import java.net.URISyntaxException;

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

}
