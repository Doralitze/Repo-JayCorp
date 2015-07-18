package org.technikradio.jay_corp;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.technikradio.jay_corp.ui.Strings;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class MessageStreamHandler {

	public static final String MESSAGE_HASH_VALIDATION_REGEX = "ID:\\{MessageStreamHash\\}\\+TCP:\\{([\\s\\S])+\\}"; //$NON-NLS-1$
	public final boolean VALID;

	private Socket s;
	private Scanner in;
	private PrintStream out;

	private static boolean running = true;

	public MessageStreamHandler(String hash) {
		try {
			// Wait for Server to catch up
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			Console.log(
					LogType.Error,
					this,
					"This thread was interrupted doing a critical operation. Expect this data to be corupted!"); //$NON-NLS-1$
			e1.printStackTrace();
		}
		if (!hash.matches(MESSAGE_HASH_VALIDATION_REGEX)) {
			VALID = false;
			return;
		}
		try {
			s = new Socket(Settings.getString("Connection.Address"), //$NON-NLS-1$
					Integer.parseInt(Settings
							.getString("Connection.MessagePort"))); //$NON-NLS-1$
		} catch (NumberFormatException | IOException e) {
			Console.log(LogType.Error, this,
					"Couldn´t connect to the message stream:"); //$NON-NLS-1$
			e.printStackTrace();
			VALID = false;
			return;
		}
		try {
			in = new Scanner(s.getInputStream());
		} catch (IOException e) {
			Console.log(LogType.Error, this,
					"Couldn´t bind scanner to the message stream:"); //$NON-NLS-1$
			e.printStackTrace();
			VALID = false;
			return;
		}
		try {
			out = new PrintStream(s.getOutputStream());
		} catch (IOException e) {
			Console.log(LogType.Error, this,
					"Couldn´t bind output printer to the message stream:"); //$NON-NLS-1$
			e.printStackTrace();
			VALID = false;
			return;
		}
		out.println(hash);
		String entry = in.nextLine();
		if (!entry.matches("Success")) { //$NON-NLS-1$
			Console.log(
					LogType.Error,
					this,
					"Couldn´t connect to the message stream: something went wrong on the server side: " //$NON-NLS-1$
							+ entry);
			VALID = false;
			return;
		}
		VALID = true;
	}

	public void run() {
		if (!VALID)
			throw new RuntimeException(
					"This stream doesn´t have a valid connection!"); //$NON-NLS-1$
		while (running) {
			try {
				String line = in.nextLine();
				switch (line) {
				case "disconnect": //$NON-NLS-1$
					Console.log(LogType.Information, this,
							"Remote host requests disconnect"); //$NON-NLS-1$
					Protocol.disconnect();
					JOptionPane
							.showMessageDialog(
									null,
									Strings.getString("MessageStreamHandler.OnExitMessage"), //$NON-NLS-1$
									Strings.getString("MessageStreamHandler.MessageHeaderExit") + toString(), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
					JayCorp.exit(2);
					break;
				case "reload": //$NON-NLS-1$
					Console.log(LogType.Information, this,
							"Remote host requests data reload"); //$NON-NLS-1$
					Protocol.collectInformation();
					break;
				case "setDays": //$NON-NLS-1$
					// Change default free days:
					Console.log(LogType.StdOut, this,
							"Asked client to set extra days"); //$NON-NLS-1$
					JOptionPane
							.showMessageDialog(
									null,
									Strings.getString("MessageStreamHandler.OnDaysMessage"), //$NON-NLS-1$
									Strings.getString("MessageStreamHandler.MessageHeaderDays") + toString(), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
					break;
				case "changePassword": //$NON-NLS-1$
					// Change default password:
					Console.log(LogType.StdOut, this,
							"Asked client to change it's password"); //$NON-NLS-1$
					JOptionPane
							.showMessageDialog(
									null,
									Strings.getString("MessageStreamHandler.OnPSWDMessage"), //$NON-NLS-1$
									Strings.getString("MessageStreamHandler.MessageHeaderDays") + toString(), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
					break;
				default:
					Console.log(LogType.Information, this,
							"recieved corupted data: " + line); //$NON-NLS-1$
				}
			} catch (NoSuchElementException e) {
				Console.log(LogType.Error, this, "Scanner broke it's lines");
				e.printStackTrace();
			}
		}
		in.close();
		out.close();
		try {
			s.close();
		} catch (IOException e) {
			Console.log(LogType.Error, this,
					"An error occured while closing the message stream connection"); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

	public static void stop() {
		running = false;
	}

	@Override
	public String toString() {
		if (s != null)
			return "MessageStreamHandler:" + s.getInetAddress(); //$NON-NLS-1$
		else
			return "MessageStreamHandler:null"; //$NON-NLS-1$
	}

}
