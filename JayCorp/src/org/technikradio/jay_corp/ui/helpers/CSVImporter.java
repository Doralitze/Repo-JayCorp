package org.technikradio.jay_corp.ui.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JComponent;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.Righttable;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class CSVImporter {

	private ProgressIndicator progressIndicator;
	private JComponent parent;
	private File workFile;
	private CSVFileFilter f;

	public CSVImporter() {
		progressIndicator = new ProgressIndicator();
		f = new CSVFileFilter();
		f.setAlwaysAccept(true);
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
		workFile = AdvancedFileInputDialog.showDialog(null, null, f,
				Strings.getString("CSVImporter.DialogTitle"));
		if (workFile == null)
			return;
		// Righttable defaultRT = RightEditFrame.showDialog(new Righttable());
		Righttable defaultRT = new Righttable();
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
				u.setPassword(Strings
						.getString("CSVImporter.DefaultNewPassword")); //$NON-NLS-1$
				u.setSelectedDays(new DayTable());
				u.setUsername(s[3]);
				u.setWorkAge(1);
				u.setID(Protocol.getIDCount() + 1);
				u.setRights(defaultRT);
				Protocol.addUser(u);
				Console.log(LogType.StdOut, this, "Adding user: " + u.getName());
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
