package org.technikradio.jay_corp.ui.helpers;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

public class CSVImporter {

	private ProgressIndicator progressIndicator;
	private JFileChooser fileChooser;
	private JComponent parent;
	private File workFile;

	public CSVImporter() {
		progressIndicator = new ProgressIndicator();
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle("Datei öffnen");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileHidingEnabled(false);
		CSVFileFilter f = new CSVFileFilter();
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

	}

}
