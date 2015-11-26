package org.technikradio.jay_corp.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;

public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1606884573973143322L;
	private static final String COPYRIGHT_TEXT = "© 2014 - 2015 Leon Christopher Dietrich\n\n"
			+ "Entwicklung:  Leon Christopher Dietrich\n" + "Betatester:   Alwin Löffler\n\n"
			+ "Diese Software wird Ihnen unter den Bedingungen der GPLv3 zur \nVerfügung gestellt.";
	private final AboutDialog ownHandle = this;
	private JButton closeButton;
	private JTextArea text;

	public AboutDialog() {
		super();
		setup();
	}

	public AboutDialog(Frame owner) {
		super(owner);
		setup();
	}

	public AboutDialog(Dialog owner) {
		super(owner);
		setup();
	}

	public AboutDialog(Window owner) {
		super(owner);
		setup();
	}

	public AboutDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public AboutDialog(Frame owner, String title) {
		super(owner, title);
		setup();
	}

	public AboutDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		setup();
	}

	public AboutDialog(Dialog owner, String title) {
		super(owner, title);
		setup();
	}

	public AboutDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup();
	}

	public AboutDialog(Window owner, String title) {
		super(owner, title);
		setup();
	}

	public AboutDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public AboutDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup();
	}

	public AboutDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setup();
	}

	public AboutDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public AboutDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup();
	}

	public AboutDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup();
	}

	private void setup() {
		this.setResizable(false);
		this.setLayout(null);
		{
			int posy, posx, width, height;
			posx = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - 250;
			posy = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - 250;
			width = 500;
			height = 500;
			this.setBounds(new Rectangle(posx, posy, width, height));
		}
		closeButton = new JButton("Schließen");
		closeButton.setBounds(425, 425, 70, 40);
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ownHandle.dispose();
			}
		});
		this.add(closeButton);
		text = new JTextArea(10, 40);
		text.setEditable(false);
		text.setCursor(null);
		text.setOpaque(false);
		text.setFocusable(false);
		text.setWrapStyleWord(true);
		text.setLineWrap(true);
		text.setBounds(0, 0, 500, 400);
		text.setText(COPYRIGHT_TEXT);
		this.add(text);
	}

}
