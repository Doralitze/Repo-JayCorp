package org.technikradio.jay_corp.ui.helpers;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class AdvancedFileInputDialog extends JDialog {

	private static final long serialVersionUID = 8975995288510019150L;

	private JFileChooser fileChooser;
	private JButton browseButton;
	private JButton okButton;
	private JButton abortButton;
	private JTextField pathText;
	private WorkState found = WorkState.notSelected;

	private enum WorkState {
		notSelected, found, aborted
	}

	public AdvancedFileInputDialog() {
		setup();
	}

	public AdvancedFileInputDialog(Frame owner) {
		super(owner);
		setup();
	}

	public AdvancedFileInputDialog(Dialog owner) {
		super(owner);
		setup();
	}

	public AdvancedFileInputDialog(Window owner) {
		super(owner);
		setup();
	}

	public AdvancedFileInputDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public AdvancedFileInputDialog(Frame owner, String title) {
		super(owner, title);
		setup();
	}

	public AdvancedFileInputDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public AdvancedFileInputDialog(Dialog owner, String title) {
		super(owner, title);
		setup();
	}

	public AdvancedFileInputDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup();
	}

	public AdvancedFileInputDialog(Window owner, String title) {
		super(owner, title);
		setup();
	}

	public AdvancedFileInputDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public AdvancedFileInputDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public AdvancedFileInputDialog(Window owner, String title,
			ModalityType modalityType) {
		super(owner, title, modalityType);
		setup();
	}

	public AdvancedFileInputDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public AdvancedFileInputDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public AdvancedFileInputDialog(Window owner, String title,
			ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup();
	}

	private void setup() {
		// For debug purposes
		// this.setVisible(true);

		final AdvancedFileInputDialog owner = this;

		this.setResizable(false);
		this.setSize(800, 125);

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileHidingEnabled(true);
		fileChooser.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.BrowserToolTip")); //$NON-NLS-1$

		okButton = new JButton(
				Strings.getString("AdvancedFileInputDialog.OKText")); //$NON-NLS-1$
		okButton.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.OKToolTip")); //$NON-NLS-1$
		okButton.setLocation(630, 70);
		okButton.setSize(150, 30);
		abortButton = new JButton(
				Strings.getString("AdvancedFileInputDialog.AbortText")); //$NON-NLS-1$
		abortButton.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.AbortToolTip")); //$NON-NLS-1$
		abortButton.setSize(150, 30);
		abortButton.setLocation(470, 70);
		browseButton = new JButton(
				Strings.getString("AdvancedFileInputDialog.0")); //$NON-NLS-1$
		browseButton.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.BrowseToolTip")); //$NON-NLS-1$
		browseButton.setLocation(630, 30);
		browseButton.setSize(150, 30);
		pathText = new JTextField();
		pathText.setToolTipText(Strings
				.getString("AdvancedFileInputDialog.TextFieldToolTip")); //$NON-NLS-1$
		pathText.setSize(610, 30);
		pathText.setLocation(10, 30);

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				found = WorkState.found;
			}
		});

		abortButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				found = WorkState.aborted;
			}
		});

		browseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(pathText.getText().equals("") || pathText.getText() == null)) //$NON-NLS-1$
					fileChooser.setSelectedFile(new File(pathText.getText()));
				int exitMode = fileChooser.showOpenDialog(owner);
				if (exitMode == JFileChooser.CANCEL_OPTION)
					return;
				else if (exitMode == JFileChooser.ERROR_OPTION)
					Console.log(LogType.Error, this,
							"An unknown error occured doing file choosing operation"); //$NON-NLS-1$
				pathText.setText(fileChooser.getSelectedFile()
						.getAbsolutePath());
			}
		});

		okButton.setVisible(true);
		abortButton.setVisible(true);
		browseButton.setVisible(true);
		pathText.setVisible(true);

		this.add(okButton);
		this.add(abortButton);
		this.add(browseButton);
		this.add(pathText);

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				// owner.setVisible(true);
			}
		});

		// Temporary:
		{
			if (!(pathText.getText().equals("") || pathText.getText() == null)) //$NON-NLS-1$
				fileChooser.setSelectedFile(new File(pathText.getText()));
			int exitMode = fileChooser.showOpenDialog(owner);
			if (exitMode == JFileChooser.CANCEL_OPTION)
				return;
			else if (exitMode == JFileChooser.ERROR_OPTION)
				Console.log(LogType.Error, this,
						"An unknown error occured doing file choosing operation"); //$NON-NLS-1$
			pathText.setText(fileChooser.getSelectedFile().getAbsolutePath());
			found = WorkState.found;
		}
		this.invalidate();
	}

	public void addFilter(FileFilter f) {
		fileChooser.addChoosableFileFilter(f);
	}

	public void setTitle(String title) {
		try {
			if (title == null || title.equals("")) //$NON-NLS-1$
				return;
			// this.setTitle(title);
			fileChooser.setDialogTitle(title);
		} catch (Exception e) {
			;
		}
	}

	public File getSelectedFile() {
		return new File(pathText.getText());
	}

	public void setFile(String f) {
		pathText.setText(f);
	}

	/*
	 * @Override public String toString() { return "AdvancedFileInputDialog";
	 * //$NON-NLS-1$ }
	 */
	@Override
	public void update(Graphics g) {
		super.update(g);
		browseButton.paint(g);
		okButton.paint(g);
		abortButton.paint(g);
		pathText.paint(g);
	}

	public static File showDialog(Frame owner, String startFile, FileFilter f,
			String title) {
		AdvancedFileInputDialog afid = new AdvancedFileInputDialog(owner);
		if (startFile != null)
			afid.setFile(startFile);
		afid.addFilter(f);
		afid.setTitle(title);
		int lastPrio = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(1);
		while (afid.found == WorkState.notSelected) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Console.log(LogType.Warning, "AdvancedFileInputDialog", //$NON-NLS-1$
						"The process of choosing a file was interrupted"); //$NON-NLS-1$
				e.printStackTrace();
				Thread.currentThread().interrupt();
				return null;
			}
		}
		Thread.currentThread().setPriority(lastPrio);
		afid.setVisible(false);
		switch (afid.found) {
		case aborted:
			return null;
		case found:
			return afid.getSelectedFile();
		case notSelected:
			throw new RuntimeException("Something went wrong..."); //$NON-NLS-1$
		default:
			throw new RuntimeException("Something went wrong..."); //$NON-NLS-1$
		}
	}

}
