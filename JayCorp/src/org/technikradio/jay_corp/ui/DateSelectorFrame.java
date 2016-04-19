package org.technikradio.jay_corp.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdatepicker.DateModel;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;
import org.technikradio.universal_tools.ParaDate;

public class DateSelectorFrame extends JDialog {

	private static final long serialVersionUID = -7225136057274801871L;
	private int fixedYear = 0;
	private DateSelectorFrame ownHandle = this;
	private JPanel selectorPanel;
	private ArrayList<JDatePicker> pickers;
	private ArrayList<ParaDate> results;

	public DateSelectorFrame() {
		super();
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
		pickers = new ArrayList<JDatePicker>();
		this.setResizable(true);
		this.setLayout(new BorderLayout());
		JPanel closePanel = new JPanel();
		{
			JButton abortButton = new JButton();
			abortButton.setText("Abbrechen");
			abortButton.setToolTipText("Hiermit brechen Sie ihre Auswahl ab.");
			abortButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					clear();
					ownHandle.dispose();
				}
			});
			closePanel.add(abortButton);
		}
		{
			JButton submitButton = new JButton();
			submitButton.setText("Bestätigen");
			submitButton.setToolTipText("Hiermit bestätigen Sie ihre eingabe.");
			submitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					collectResults();
					ownHandle.dispose();
				}
			});
			closePanel.add(submitButton);
		}
		selectorPanel = new JPanel();
		selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
		this.add(closePanel, BorderLayout.PAGE_END);
		this.add(new JScrollPane(selectorPanel), BorderLayout.CENTER);
		this.add(new JLabel("Bitte füllen Sie diese Felder aus:\n"), BorderLayout.PAGE_START);
	}

	private void clear() {
		results = new ArrayList<ParaDate>();
	}

	private void collectResults() {
		results = new ArrayList<ParaDate>();
		for (JDatePicker p : pickers) {
			DateModel<?> d = p.getModel();
			ParaDate pd = new ParaDate();
			pd.setDay((short) d.getDay());
			pd.setMonth((short) d.getMonth());
			if (fixedYear != 0)
				pd.setYear(d.getYear());
			else
				pd.setYear(fixedYear);
		}
	}

	public void addPicker(String text) {
		JPanel c = new JPanel();
		JLabel lb = new JLabel(text);
		UtilDateModel model = new UtilDateModel();
		if (fixedYear != 0)
			model.setDate(2016, 0, 1);
		else
			model.setDate(fixedYear, 0, 1);
		model.setSelected(true);
		JDatePicker p = new JDatePicker(model);
		if (fixedYear != 0)
			p.setShowYearButtons(false);
		c.setLayout(new FlowLayout());
		c.add(lb);
		c.add(p);
		pickers.add(p);
		selectorPanel.add(c, Component.LEFT_ALIGNMENT);
		this.repaint();
	}

	public ParaDate[] getResults() {
		if (results != null)
			return (ParaDate[]) results.toArray();
		else
			return null;
	}

	/**
	 * @return the fixed year
	 */
	public int getFixedYear() {
		return fixedYear;
	}

	/**
	 * @param fixed
	 *            year the fixed year to set
	 */
	public void setFixedYear(int fixedYear) {
		this.fixedYear = fixedYear;
	}

	/**
	 * This static Method generates a dialog for you and returnes an array
	 * containing the dates.
	 * 
	 * @param The
	 *            labels to display before the datepickers
	 * @return The selected dates in order
	 */
	public static ParaDate[] query(String[] labels) {
		return query(labels, null);

	}

	/**
	 * This static Method generates a dialog for you and returnes an array
	 * containing the dates.
	 * 
	 * @param The
	 *            labels to display before the datepickers
	 * @param The
	 *            parent frame of the resulting dialoug
	 * @return The selected dates in order
	 */
	public static ParaDate[] query(String[] labels, Frame parent) {
		return query(labels, parent, "DateSelector", 0);
	}

	/**
	 * This static Method generates a dialog for you and returnes an array
	 * containing the dates.
	 * 
	 * @param The
	 *            labels to display before the datepickers
	 * @param The
	 *            parent frame of the resulting dialoug
	 * @param The
	 *            title of the resulting dialoug
	 * @return The selected dates in order
	 */
	public static ParaDate[] query(String[] labels, Frame parent, String title, int fixedYear) {
		DateSelectorFrame f = new DateSelectorFrame(parent, title);
		f.setFixedYear(fixedYear);
		for(String s : labels){
			f.addPicker(s);
		}
		f.setVisible(true);
		while(f.isVisible());
		return f.getResults();
	}

}
