package org.technikradio.jay_corp.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class UserWatchFrame extends JDialog {

	private static final long serialVersionUID = -1553588773452490924L;
	private JTable table;
	private final UserWatchFrame ownHandle = this;
	private String[][] originalData;

	private void setup(String[] head, String[][] data) {
		originalData = data.clone();
		this.setLayout(new BorderLayout());
		//Initialize table
		{
			table = new JTable(data, head);
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
					//TODO implement data synchronization
					try {
						ownHandle.dispose();
					} catch (Exception e1) {
						Console.log(LogType.Error, ownHandle, "This shouldn't happen.");
						e1.printStackTrace();
						JOptionPane.showMessageDialog(ownHandle, "An unknown exception occured: \n\n" + e1.getLocalizedMessage() + "\n" + e1.getStackTrace().toString());
					}
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
						JOptionPane.showMessageDialog(ownHandle, "An unknown exception occured: \n\n" + e1.getLocalizedMessage() + "\n" + e1.getStackTrace().toString());
					}
				}
				
			});
		}
		{
			int posy, posx, width, height;
			Insets s = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
			posx = 0 + s.left;
			posy = 0 + s.top;
			width = (Toolkit.getDefaultToolkit().getScreenSize().width - s.right) / 3;
			height = (Toolkit.getDefaultToolkit().getScreenSize().height - s.bottom) / 3;
			this.setBounds(new Rectangle(posx, posy, width, height));
			this.setLocation(posx, posy);
		}
	}

	public UserWatchFrame(String[] head, String[][] data) {
		super();
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, boolean modal) {
		super(owner, modal);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, String title) {
		super(owner, title);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, JDialog owner) {
		super(owner);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame owner, boolean modal) {
		super(owner, modal);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame arg0, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame owner, String title) {
		super(owner, title);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame owner) {
		super(owner);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, String title) {
		super(owner, title);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner) {
		super(owner);
		setup(head, data);
	}

}
