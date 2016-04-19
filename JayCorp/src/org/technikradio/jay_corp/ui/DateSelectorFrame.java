package org.technikradio.jay_corp.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.technikradio.universal_tools.ParaDate;

public class DateSelectorFrame extends JDialog {

	
	private static final long serialVersionUID = -7225136057274801871L;
	private DateSelectorFrame ownHandle = this;
	private JPanel selectorPanel;
	//private ArrayList<JXDatePicker> pickers;

	public DateSelectorFrame() {
		setup();
	}

	public DateSelectorFrame(Frame owner) {
		super(owner);
		setup();
	}

	public DateSelectorFrame(Dialog owner) {
		super(owner);
		setup();
	}

	public DateSelectorFrame(Window owner) {
		super(owner);
		setup();
	}

	public DateSelectorFrame(Frame owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public DateSelectorFrame(Frame owner, String title) {
		super(owner, title);
		setup();
	}

	public DateSelectorFrame(Dialog owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public DateSelectorFrame(Dialog owner, String title) {
		super(owner, title);
		setup();
	}

	public DateSelectorFrame(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup();
	}

	public DateSelectorFrame(Window owner, String title) {
		super(owner, title);
		setup();
	}

	public DateSelectorFrame(Frame arg0, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		setup();
	}

	public DateSelectorFrame(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public DateSelectorFrame(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setup();
	}

	public DateSelectorFrame(Frame arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
		setup();
	}

	public DateSelectorFrame(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public DateSelectorFrame(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup();
	}

	private void setup() {
		this.setResizable(true);
		this.setLayout(new BorderLayout());
		JPanel closePanel = new JPanel();
		{
			JButton abortButton = new JButton();
			abortButton.setText("Abbrechen");
			abortButton.setToolTipText("Hiermit brechen Sie ihre Auswahl ab.");
			abortButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					clear();
					ownHandle.dispose();
				}});
			closePanel.add(abortButton);
		}
		{
			JButton submitButton = new JButton();
			submitButton.setText("Bestätigen");
			submitButton.setToolTipText("Hiermit bestätigen Sie ihre eingabe.");
			submitButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ownHandle.dispose();
				}});
			closePanel.add(submitButton);
		}
		selectorPanel = new JPanel();
		this.add(closePanel, BorderLayout.PAGE_END);
		this.add(selectorPanel, BorderLayout.CENTER);
		this.add(new JLabel("Bitte füllen Sie diese Felder aus:\n"), BorderLayout.PAGE_START);
	}
	
	private void clear(){
		
	}

	/**
	 * This static Method generates a dialog for you and returnes an array containing the dates.
	 * @param The labels to display before the datepickers
	 * @return The selected dates in order
	 */
	public static ParaDate[] query(String[] labels) {
		return query(labels, null);
		
	}
	
	/**
	 * This static Method generates a dialog for you and returnes an array containing the dates.
	 * @param The labels to display before the datepickers
	 * @param The parent frame of the resulting dialoug
	 * @return The selected dates in order
	 */
	public static ParaDate[] query(String[] labels, Frame parent) {
		return query(labels, parent, "DateSelector");
	}
	
	/**
	 * This static Method generates a dialog for you and returnes an array containing the dates.
	 * @param The labels to display before the datepickers
	 * @param The parent frame of the resulting dialoug
	 * @param The title of the resulting dialoug
	 * @return The selected dates in order
	 */
	public static ParaDate[] query(String[] labels, Frame parent, String title) {
		DateSelectorFrame f = new DateSelectorFrame(parent, title);
		return null;
	}

}
