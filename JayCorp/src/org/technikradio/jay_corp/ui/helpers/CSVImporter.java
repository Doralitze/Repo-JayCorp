/*
JayCorp-Client/CSVImporter.java
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
package org.technikradio.jay_corp.ui.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.Settings;
import org.technikradio.jay_corp.ui.RightEditFrame;
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
		progressIndicator.setLocation(this.parent.getLocation());
	}

	public CSVImporter(JComponent parent) {
		this();
		setParent(parent);
	}

	public void upload() {
		workFile = AdvancedFileInputDialog.showDialog(null, null, f, Strings.getString("CSVImporter.DialogTitle")); //$NON-NLS-1$
		if (workFile == null)
			return;
		Righttable defaultRT = RightEditFrame.showDialog(new Righttable());
		// Righttable defaultRT = new Righttable();
		progressIndicator.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		progressIndicator.setValv(0, 100, 1);
		progressIndicator.setInfoLabelText(Strings.getString("CSVImporter.UploadHint")); //$NON-NLS-1$
		progressIndicator.setVisible(true);
		BufferedReader br = null;
		DataInputStream in = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(workFile);
			in = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(in, Settings.getString("Importing.FileEncoding")));
			String currentLine = br.readLine();
			int i = 0;
			ArrayList<User> users = new ArrayList<User>();
			while ((currentLine = br.readLine()) != null) {
				if(!currentLine.equals("") && !currentLine.equals(";;;")){
					if (i != 0) {
						String[] s = currentLine.split(";"); //$NON-NLS-1$
						User u = new User();
						for(int j = 0; j < s.length; j++){
							s[j] = s[j].replace("ü", "ue").replace("ö", "oe").replace("ä", "ae")
											 	 .replace("Ü", "Ue").replace("Ö", "Oe").replace("Ä", "Ae")
											   .replace("ß", "ss");
											 }
						u.setName(s[2] + " " + s[1]); //$NON-NLS-1$
						u.setExtraDays(1);
						u.setPassword(Strings.getString("CSVImporter.DefaultNewPassword")); //$NON-NLS-1$
						u.setSelectedDays(new DayTable());
						u.setUsername(s[3]);
						u.setWorkAge(1);
						u.setID(Protocol.getIDCount());
						u.setRights(defaultRT);
						users.add(u);
					}
					i++;
				}
			}
			
			for(int ij = 0; ij < users.size(); ij++){
				User u = users.get(ij);
				Protocol.addUser(u);
				Console.log(LogType.StdOut, this, "Adding user: " + u.getName()); //$NON-NLS-1$
				progressIndicator.setValv(0, users.size() - 1, ij);
			}
			if(Boolean.parseBoolean(Settings.getString("PerformDBUpdateAfterUserAdd")))
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
				progressIndicator.setVisible(false);
			} catch (NullPointerException e) {
				Console.log(LogType.Error, this, "An unknown error occured:"); //$NON-NLS-1$
				e.printStackTrace();
			}
			try {
				br.close();
				in.close();
				fis.close();
			} catch (IOException | NullPointerException e) {
				Console.log(LogType.Error, this, "An unknown error occured:"); //$NON-NLS-1$
				e.printStackTrace();
			}
			Console.log(LogType.StdOut, this, "Done uploading file"); //$NON-NLS-1$
		}

	}
}
