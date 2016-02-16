/*
JayCorp-Client/JayCorp.java
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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.technikradio.jay_corp.ui.LoginPanel;
import org.technikradio.jay_corp.ui.MainFrame;
import org.technikradio.jay_corp.ui.SetupFrame;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

/**
 * The splashscreen and startup class
 * 
 * @author Doralitze
 */
public class JayCorp extends JFrame {
	private static final long serialVersionUID = 7315888172557781013L;
	private static LoginPanel lp;
	private static String[] argv;

	public static void main(String[] args) {
		JayCorp splashscreen = new JayCorp();
		splashscreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		splashscreen.setAlwaysOnTop(true);
		splashscreen.setAutoRequestFocus(true);
		splashscreen.setUndecorated(true);
		setSCFrameBounds(splashscreen);
		splashscreen.setVisible(true);
		splashscreen.setResizable(false);
		if(splashscreen.getWidth() > 500 || splashscreen.getHeight() > 200)
			setSCFrameBounds(splashscreen);
		if(SplashScreen.getSplashScreen() != null)
			SplashScreen.getSplashScreen().close();
		argv = args;
		{
			JPanel jp = new JPanel();
			jp.setBounds(0, 0, 500, 200);
			jp.setBackground(Color.DARK_GRAY);
			lp = new LoginPanel();
			lp.setBackground(Color.DARK_GRAY);
			lp.setBounds(0, 0, splashscreen.getWidth(), splashscreen.getHeight());
			lp.setVisible(true);
			lp.setParent(splashscreen);
			jp.add(lp);
			splashscreen.add(jp);
			splashscreen.repaint();
		}
		if (setSystemLookAndFeel())
			Console.log(LogType.StdOut, "ClassLoader", //$NON-NLS-1$
					"Successfully loaded Look_and_feel"); //$NON-NLS-1$
		lp.getValues(true);
		{
			if (Boolean.parseBoolean(Settings.getString("runSetup")))
				if (Protocol.getCurrentUser() != null) {
					if (Protocol.getCurrentUser().getPassword()
							.equals(Strings.getString("CSVImporter.DefaultNewPassword"))) {
						SetupFrame stf = new SetupFrame();
						splashscreen.setVisible(false);
						stf.showDialog();
						splashscreen.setVisible(true);
						exit(0, true);
					}
				} else
					exit(15);
			MainFrame mf = new MainFrame();
			mf.setup();
			mf.setVisible(true);
		}
		Console.log(LogType.StdOut, "ClassLoader", //$NON-NLS-1$
				"Successfully loaded MainFrame"); //$NON-NLS-1$
		splashscreen.dispose();

	}

	private static boolean setSystemLookAndFeel() {
		// set System Look and Feel
		System.setProperty("apple.awt.rendering", "quality"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("apple.awt.fractionalmetrics", "on"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("apple.laf.useScreenMenuBar", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			Console.log(LogType.Error, "ClassLoader", //$NON-NLS-1$
					"Error while changing Look_and_feel:"); //$NON-NLS-1$
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void exit(int status) {
		if (status != 0)
			Protocol.disconnect();
		System.exit(status);
	}

	public static void exit(int status, boolean restart) {
		if (!restart)
			exit(status);
		else
			try {
				if (status != 0)
					Protocol.disconnect();
				restartApplication(status);
			} catch (URISyntaxException e) {
				Console.log(LogType.Error, "ShutdownService", "Unable to restart application:"); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			} catch (IOException e) {
				Console.log(LogType.Error, "ShutdownService", "Unable to restart application:"); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
	}

	private static void restartApplication(int status) throws URISyntaxException, IOException {
		/*
		 * final String javaBin = System.getProperty("java.home") +
		 * File.separator + "bin" + File.separator //$NON-NLS-1$ //$NON-NLS-2$ +
		 * Settings.getString("JayCorp.JavaCommand"); //$NON-NLS-1$ final File
		 * currentJar = new
		 * File(JayCorp.class.getProtectionDomain().getCodeSource().getLocation(
		 * ).toURI());
		 * 
		 * is it a jar file? if (!currentJar.getName().endsWith(".jar"))
		 * //$NON-NLS-1$ { Console.log(LogType.Error, "ShutdownService",
		 * "Could not restart app: " + currentJar.getName() +
		 * "is not a jar file"); return; }
		 * 
		 * Build command: java -jar application.jar final ArrayList<String>
		 * command = new ArrayList<String>(); command.add(javaBin);
		 * command.add("-jar"); //$NON-NLS-1$ command.add(currentJar.getPath());
		 * 
		 * final ProcessBuilder builder = new ProcessBuilder(command);
		 * builder.start(); Console.log(LogType.StdOut, "ShutdownService",
		 * "Reloading application"); exit(status);
		 */
		StringBuilder cmd = new StringBuilder();
		cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			cmd.append(jvmArg + " "); //$NON-NLS-1$
		}
		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		cmd.append(JayCorp.class.getName()).append(" "); //$NON-NLS-1$
		for (String arg : argv) {
			cmd.append(arg).append(" "); //$NON-NLS-1$
		}
		Runtime.getRuntime().exec(cmd.toString());
		exit(status + 1000);
	}
	
	private static void setSCFrameBounds(JFrame sc){
		int posy, posx, width, height;
		posx = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - 250;
		posy = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - 100;
		width = 500;
		height = 200;
		sc.setBounds(new Rectangle(posx, posy, width, height));
	}

}
