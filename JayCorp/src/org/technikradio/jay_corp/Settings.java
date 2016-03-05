package org.technikradio.jay_corp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class Settings {
	private static final String BUNDLE_NAME = "org.technikradio.jay_corp.ui.settings"; //$NON-NLS-1$

	private static final ResourceBundle FALLBACK_RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	private static final Properties FIRST_VALUES = new Properties();

	static {
		String altPath = Application.getAppInstallPath();
		if (altPath != null) {
			if (altPath.endsWith(File.separator + "JayCorp.jar"))
				altPath = altPath.replace(File.separator + "JayCorp.jar", "");
			try {
				altPath += File.separator + "settings.properties";
				FIRST_VALUES.load(new FileInputStream(altPath));
			} catch (IOException e) {
				Console.log(LogType.Error, "StringManager", "Couldn´t load external strings:");
				if (Boolean.parseBoolean(Settings.getString("AdvancedOutputMode")))
					System.out.println(altPath);
				if(!(e instanceof FileNotFoundException))
				e.printStackTrace();
			}
		}
	}

	public static String getString(String key) {
		try {
			if (FIRST_VALUES == null)
				return FALLBACK_RESOURCE_BUNDLE.getString(key);
			if (FIRST_VALUES.containsKey(key))
				return (String) FIRST_VALUES.get(key);
			else
				return FALLBACK_RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
