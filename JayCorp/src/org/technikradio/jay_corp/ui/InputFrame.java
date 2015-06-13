package org.technikradio.jay_corp.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class InputFrame extends JFrame {

	private static final long serialVersionUID = 2809843782504990697L;
	private InputMode im = InputMode.STRING;
	private JLabel helpMessage;
	private JLabel foreMessage;
	private JTextField stringField;
	// private JNumberField intField;
	private JPasswordField passField;

	public enum InputMode {
		PASSWORD, INTEGER, STRING
	}

	public InputFrame() throws HeadlessException {
		super();
		setup();
	}

	public InputFrame(GraphicsConfiguration gc) {
		super(gc);
		setup();
	}

	public InputFrame(String title) throws HeadlessException {
		super(title);
		setup();
	}

	public InputFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		setup();
	}

	private void setup() {
		this.setTitle("Eingabe erwartet");
		helpMessage = new JLabel();
		foreMessage = new JLabel();
		stringField = new JTextField();
		passField = new JPasswordField();
		update();
	}

	private void update() {

	}

	public void setInputMode(InputMode inputMode) {
		this.im = inputMode;
		update();
	}

	public void setForeMessage(String message) {
		foreMessage.setText(message);
	}

	public void setHelpMessage(String message) {
		helpMessage.setText(message);
	}

	public String getInputString() {
		if (im == InputMode.INTEGER)
			throw new RuntimeException("Invalid input type for this function");
		// TODO wait for click interrupt
		switch (im) {
		case PASSWORD:

			break;
		case STRING:

			break;
		}
		return null;
	}

	public int getInputInt() {
		if (im == InputMode.INTEGER)
			throw new RuntimeException("Invalid input type for this function");
		// TODO wait for click interrupt

		return 0;
	}
}
