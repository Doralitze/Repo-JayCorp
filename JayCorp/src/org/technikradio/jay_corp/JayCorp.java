package org.technikradio.jay_corp;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.technikradio.jay_corp.ui.LoginPanel;
import org.technikradio.jay_corp.ui.MainFrame;
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

	public static void main(String[] args) {
		JayCorp splashscreen = new JayCorp();
		splashscreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		splashscreen.setAlwaysOnTop(true);
		splashscreen.setAutoRequestFocus(true);
		splashscreen.setUndecorated(true);
		{
			int posy, posx, width, height;
			posx = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - 250;
			posy = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - 100;
			width = 500;
			height = 200;
			splashscreen.setBounds(new Rectangle(posx, posy, width, height));
		}
		splashscreen.setVisible(true);
		splashscreen.setResizable(false);
		{
			JPanel jp = new JPanel();
			jp.setBounds(0, 0, 500, 200);
			jp.setBackground(Color.DARK_GRAY);
			lp = new LoginPanel();
			lp.setBackground(Color.DARK_GRAY);
			lp.setBounds(0, 0, splashscreen.getWidth(),
					splashscreen.getHeight());
			lp.setVisible(true);
			lp.setParent(splashscreen);
			jp.add(lp);
			splashscreen.add(jp);
			splashscreen.repaint();
		}
		if (setSystemLookAndFeel())
			Console.log(LogType.StdOut, "ClassLoader",
					"Successfully loaded Look_and_feel");
		lp.getValues(true);
		{
			MainFrame mf = new MainFrame();
			mf.setup();
			mf.setVisible(true);
		}
		Console.log(LogType.StdOut, "ClassLoader",
				"Successfully loaded MainFrame");
		splashscreen.dispose();
	}

	private static boolean setSystemLookAndFeel() {
		// set System Look and Feel
		System.setProperty("apple.awt.rendering", "quality");
		System.setProperty("apple.awt.fractionalmetrics", "on");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			Console.log(LogType.Error, "ClassLoader",
					"Error while changing Look_and_feel:");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void exit(int status) {
		System.exit(status);
	}

}
