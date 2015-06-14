package org.technikradio.jay_corp.ui.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class CSVImporter {

	private ProgressIndicator progressIndicator;
	private JFileChooser fileChooser;
	private JComponent parent;
	private File workFile;

	public CSVImporter() {
		progressIndicator = new ProgressIndicator();
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle(Strings.getString("CSVImporter.DialogTitle")); //$NON-NLS-1$
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileHidingEnabled(true);
		CSVFileFilter f = new CSVFileFilter();
		f.setAlwaysAccept(true);
		fileChooser.setFileFilter(f);
	}

	public void setParent(JComponent parent) {
		this.parent = parent;
		progressIndicator.setLocation(parent.getLocation());
	}

	public CSVImporter(JComponent parent) {
		this();
		setParent(parent);
	}

	public void upload() {
		int exitMode = fileChooser.showOpenDialog(parent);
		if (exitMode == JFileChooser.CANCEL_OPTION)
			return;
		else if (exitMode == JFileChooser.ERROR_OPTION)
			Console.log(LogType.Error, this,
					"An unknown error occured doing file choosing operation"); //$NON-NLS-1$
		workFile = fileChooser.getSelectedFile();
		BufferedReader br = null;
		DataInputStream in = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(workFile);
			in = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(in));
			String currentLine = br.readLine();
			while ((currentLine = br.readLine()) != null) {
				String[] s = currentLine.split(";"); //$NON-NLS-1$
				User u = new User();
				u.setName(s[2] + " " + s[1]); //$NON-NLS-1$
				u.setExtraDays(1);
				u.setPassword(Strings.getString("CSVImporter.DefaultNewPassword")); //$NON-NLS-1$
				u.setSelectedDays(new DayTable());
				u.setUsername(s[3]);
				u.setWorkAge(1);
				u.setID(Protocol.getIDCount() + 1);
				Protocol.addUser(u);
			}
			Protocol.save();
		} catch (FileNotFoundException e) {
			Console.log(LogType.Error, this, "The provided file wasn´t found:"); //$NON-NLS-1$
			e.printStackTrace();
			return;
		} catch (IOException e) {
			Console.log(LogType.Error, this, "Couldn´t read provided file:"); //$NON-NLS-1$
			e.printStackTrace();
			return;
		} finally {
			try {
				br.close();
				in.close();
				fis.close();
			} catch (IOException | NullPointerException e) {
				Console.log(LogType.Error, this, "An unknown error occured:"); //$NON-NLS-1$
				e.printStackTrace();
			}

		}

	}
}
