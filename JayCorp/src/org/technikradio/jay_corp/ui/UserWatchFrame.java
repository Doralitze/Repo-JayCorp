package org.technikradio.jay_corp.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class UserWatchFrame extends JDialog {

	private static final long serialVersionUID = -1553588773452490924L;
	private JTable table;
	private final UserWatchFrame ownHandle = this;

	private void setup() {
		this.setLayout(new BorderLayout());
		//Initialize table
		{
			table = new JTable();
			add(new JScrollPane(table.getTableHeader()), BorderLayout.PAGE_START);
			add(new JScrollPane(table), BorderLayout.CENTER);
		}
		//Add buttons
		{
			JPanel buttonPanel = new JPanel();
			JButton okButton = new JButton("OK");
			JButton abortButton = new JButton("Abbrechen");
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			buttonPanel.add(okButton);
			buttonPanel.add(abortButton);
			add(new JScrollPane(buttonPanel), BorderLayout.PAGE_END);
			okButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			abortButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						ownHandle.dispose();
					} catch (Exception e1) {
						Console.log(LogType.Error, ownHandle, "This shouldn't happen.");
						e1.printStackTrace();
					}
				}
				
			});
		}
	}

	public UserWatchFrame() {
		super();
		setup();
	}

	public UserWatchFrame(Dialog owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public UserWatchFrame(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public UserWatchFrame(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public UserWatchFrame(Dialog owner, String title) {
		super(owner, title);
		setup();
	}

	public UserWatchFrame(Dialog owner) {
		super(owner);
		setup();
	}

	public UserWatchFrame(Frame owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public UserWatchFrame(Frame arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
		setup();
	}

	public UserWatchFrame(Frame arg0, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		setup();
	}

	public UserWatchFrame(Frame owner, String title) {
		super(owner, title);
		setup();
	}

	public UserWatchFrame(Frame owner) {
		super(owner);
		setup();
	}

	public UserWatchFrame(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup();
	}

	public UserWatchFrame(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup();
	}

	public UserWatchFrame(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setup();
	}

	public UserWatchFrame(Window owner, String title) {
		super(owner, title);
		setup();
	}

	public UserWatchFrame(Window owner) {
		super(owner);
		setup();
	}

}
