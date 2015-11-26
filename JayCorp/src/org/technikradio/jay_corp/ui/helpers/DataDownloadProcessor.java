package org.technikradio.jay_corp.ui.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.Settings;
import org.technikradio.jay_corp.ui.FixedOptionPane;
import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;

public class DataDownloadProcessor {

	private static boolean smartSearch;
	private static boolean richOutput;

	private ProgressIndicator progressIndicator;
	private JFileChooser fileChooser;
	private JFrame parent;
	private File workFile;

	static {
		smartSearch = Boolean.parseBoolean(Settings.getString("DataDownloader.SmartSearch")); //$NON-NLS-1$
		Console.log(LogType.Information, "DataDownloadProcessor", //$NON-NLS-1$
				"SmartSearchEngine enabled: " + Boolean.toString(smartSearch)); //$NON-NLS-1$
		richOutput = Boolean.parseBoolean(Settings.getString("DataDownloader.RichOutput")); //$NON-NLS-1$
		Console.log(LogType.Information, "DataDownloadProcessor", //$NON-NLS-1$
				"rich output enabled: " + Boolean.toString(richOutput)); //$NON-NLS-1$
	}

	public DataDownloadProcessor() {
		progressIndicator = new ProgressIndicator();
		fileChooser = new JFileChooser();
		// fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle(Strings.getString("DataDownloadProcessor.FileChooserDialog")); //$NON-NLS-1$
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileHidingEnabled(false);
		CSVFileFilter f = new CSVFileFilter();
		fileChooser.setFileFilter(f);
	}

	public DataDownloadProcessor(JFrame parent) {
		this();
		setParent(parent);
		// progressIndicator = new ProgressIndicator(parent);
	}

	public void setParent(JFrame parent) {
		this.parent = parent;
		progressIndicator.setLocation(parent.getLocation());
	}

	public void download() {
		Console.log(LogType.StdOut, this, "Showing file selection dialog");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		int exitMode = fileChooser.showSaveDialog(parent);
		// int exitMode = fileChooser.showSaveDialog(null);
		if (exitMode == JFileChooser.CANCEL_OPTION)
			return;
		else if (exitMode == JFileChooser.ERROR_OPTION)
			Console.log(LogType.Error, this, "An unknown error occured doing file choosing operation"); //$NON-NLS-1$
		workFile = fileChooser.getSelectedFile();
		Console.log(LogType.Error, this, "Loading file...");
		if (!workFile.canWrite() && workFile.exists()) {
			Console.log(LogType.Error, this, "Unable to write File"); //$NON-NLS-1$
			FixedOptionPane.showFixedOptionDialog(parent,
					Strings.getString("DataDownloadProcessor.FileOpenErrorMessage1"), //$NON-NLS-1$
					Strings.getString("DataDownloadProcessor.FileOpenErrorMessage2"), FixedOptionPane.OK_OPTION, //$NON-NLS-1$
					FixedOptionPane.ERROR_MESSAGE, null, null, null);
			return;
		}
		FileWriter f = null;
		try {
			f = new FileWriter(workFile);
		} catch (IOException e) {
			Console.log(LogType.Error, this, "Unable to write File:"); //$NON-NLS-1$
			e.printStackTrace();
			FixedOptionPane.showFixedOptionDialog(parent,
					Strings.getString("DataDownloadProcessor.FileOpenErrorMessage3"), //$NON-NLS-1$
					Strings.getString("DataDownloadProcessor.FileOpenErrorMessage4"), FixedOptionPane.OK_OPTION, //$NON-NLS-1$
					FixedOptionPane.ERROR_MESSAGE, null, null, null);
			return;
		}
		try {
			progressIndicator.setVisible(true);
			int maxUsers = Protocol.getIDCount();
			f.append(Strings.getString("DataDownloadProcessor.FileInitial") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 1; i < maxUsers; i++) {
				progressIndicator.setValv(0, maxUsers * 3, i * 3);
				User selected = Protocol.getUser(i);
				if (selected != null) {
					progressIndicator.setInfoLabelText(Strings.getString("DataDownloadProcessor.InfoMessageInitial") //$NON-NLS-1$
							+ selected.getName());
					DayTable ttt = Protocol.getProgress(selected.getID());
					int year = ttt.getYear();
					int selectedDays = 0;
					Status[][] t = listToSortedArray(ttt);
					progressIndicator.setValv(0, maxUsers * 3, (i * 3) + 1);
					ArrayList<String> freeList = new ArrayList<String>();
					ParaDate firstFound = null, lastValid = null;
					/*
					 * for (ParaDate p : t.getDays().keySet()) { Status s =
					 * t.getDays().get(p); if (s == Status.selected) { if
					 * (firstFound == null) firstFound = p; lastValid = p; }
					 * else { if (lastValid != null) { StringBuilder sb = new
					 * StringBuilder(); sb.append(firstFound.getMinimalDate());
					 * if (firstFound != lastValid) { sb.append(Strings
					 * .getString("DataDownloadProcessor.DateSeperator"));
					 * //$NON-NLS-1$ sb.append(lastValid.getMinimalDate()); }
					 * firstFound = null; lastValid = null; boolean clean =
					 * true; for (String comp : freeList) { if
					 * (comp.equals(sb.toString())) clean = false; } if (clean)
					 * freeList.add(sb.toString()); } } }
					 */
					for (short m = 1; m <= 12; m++)
						for (short d = 1; d <= 31; d++) {
							if (t[m][d] != null)
								switch (t[m][d]) {
								case undefined:
								case allowed:
								case normal:
									if (firstFound != null) {
										StringBuilder sb = new StringBuilder();
										sb.append(firstFound.getMinimalDate());
										if (firstFound != lastValid) {
											sb.append(Strings.getString("DataDownloadProcessor.DateSeperator")); //$NON-NLS-1$
											sb.append(lastValid.getMinimalDate());
										}
										freeList.add(sb.toString());
										firstFound = null;
										lastValid = null;
									}
									break;
								case selected:
									selectedDays++;
									if (smartSearch) {
										ParaDate pd = new ParaDate();
										pd.setDay(d);
										pd.setMonth(m);
										pd.setYear(year);
										if (firstFound == null) {
											firstFound = pd;
										}
										lastValid = pd;
										if (richOutput)
											Console.log(LogType.StdOut, this, "found: " //$NON-NLS-1$
													+ pd.getMinimalDate());
										break;
									} else {
										ParaDate pd = new ParaDate();
										pd.setDay(d);
										pd.setMonth(m);
										pd.setYear(year);
										freeList.add(pd.getMinimalDate());
										if (richOutput)
											Console.log(LogType.StdOut, this, "found: " //$NON-NLS-1$
													+ pd.getMinimalDate());
										break;
									}
								}
						}
					progressIndicator.setValv(0, maxUsers * 3, (i * 3) + 2);
					StringBuilder sb = new StringBuilder();
					sb.append(selected.getUsername());
					sb.append(';');
					sb.append(selected.getName());
					sb.append(';');
					sb.append(Integer.toString(selectedDays));
					sb.append(';');
					for (String s : freeList) {
						sb.append(s);
						sb.append(Settings.getString("DaysCutter"));
					}
					sb.append("\n"); //$NON-NLS-1$
					f.append(sb.toString());
					f.flush();
				}
			}
		} catch (Exception e) {
			Console.log(LogType.Error, this, "An unknown exception occured:"); //$NON-NLS-1$
			e.printStackTrace();
			FixedOptionPane.showFixedOptionDialog(parent,
					Strings.getString("DataDownloadProcessor.FileOpenErrorMessage5"), //$NON-NLS-1$
					Strings.getString("DataDownloadProcessor.FileOpenErrorMessage6"), FixedOptionPane.OK_OPTION, //$NON-NLS-1$
					FixedOptionPane.ERROR_MESSAGE, null, null, null);
			return;
		} finally {
			try {
				f.close();
			} catch (IOException e) {
				Console.log(LogType.Error, this, "Unable to close FileWriter:"); //$NON-NLS-1$
				e.printStackTrace();
			}
			progressIndicator.setVisible(false);
			progressIndicator.dispose();
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DataDownloadProcessor"); //$NON-NLS-1$
		if (parent != null)
			sb.append(":" + parent.toString()); //$NON-NLS-1$
		if (workFile != null)
			sb.append(';' + workFile.getAbsolutePath());
		return sb.toString();
	}

	private Status[][] listToSortedArray(DayTable t) {
		Status[][] sar = new Status[13][32];
		for (short m = 1; m <= 12; m++)
			for (short d = 1; d <= 31; d++)
				sar[m][d] = Status.undefined;
		for (ParaDate p : t.getDays().keySet()) {
			Status s = t.getDays().get(p);
			sar[p.getMonth()][p.getDay()] = s;
		}
		return sar;
	}

}
