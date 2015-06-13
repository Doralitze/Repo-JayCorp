package org.technikradio.jay_corp;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class MessageStreamHandler {

	public static final String MESSAGE_HASH_VALIDATION_REGEX = "ID:\\{MessageStreamHash\\}\\+TCP:\\{([\\s\\S])+\\}";
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
					"This thread was interrupted doing a critical operation. Expect this data to be corupted!");
			e1.printStackTrace();
		}
		if (!hash.matches(MESSAGE_HASH_VALIDATION_REGEX)) {
			VALID = false;
			return;
		}
		try {
			s = new Socket(Settings.getString("Connection.Address"),
					Integer.parseInt(Settings
							.getString("Connection.MessagePort")));
		} catch (NumberFormatException | IOException e) {
			Console.log(LogType.Error, this,
					"Couldn´t connect to the message stream:");
			e.printStackTrace();
			VALID = false;
			return;
		}
		try {
			in = new Scanner(s.getInputStream());
		} catch (IOException e) {
			Console.log(LogType.Error, this,
					"Couldn´t bind scanner to the message stream:");
			e.printStackTrace();
			VALID = false;
			return;
		}
		try {
			out = new PrintStream(s.getOutputStream());
		} catch (IOException e) {
			Console.log(LogType.Error, this,
					"Couldn´t bind output printer to the message stream:");
			e.printStackTrace();
			VALID = false;
			return;
		}
		out.println(hash);
		String entry = in.nextLine();
		if (!entry.matches("Success")) {
			Console.log(
					LogType.Error,
					this,
					"Couldn´t connect to the message stream: something went wrong on the server side: "
							+ entry);
			VALID = false;
			return;
		}
		VALID = true;
	}

	public void run() {
		if (!VALID)
			throw new RuntimeException(
					"This stream doesn´t have a valid connection!");
		while (running) {
			String line = in.nextLine();
			switch (line) {
			case "disconnect":
				Console.log(LogType.Information, this,
						"Remote host requests disconnect");
				Protocol.disconnect();
				JOptionPane
						.showMessageDialog(
								null,
								"Der Server hat das beenden dieser Verbindung angefordert.",
								"Meldung von " + toString(),
								JOptionPane.ERROR_MESSAGE);
				JayCorp.exit(2);
				break;
			case "reload":
				Console.log(LogType.Information, this,
						"Remote host requests data reload");
				Protocol.collectInformation();
				break;
			case "setDays":
				// Change default free days:
				Console.log(LogType.StdOut, this,
						"Asked client to set extra days");
				break;
			case "changePassword":
				// Change default password:
				Console.log(LogType.StdOut, this,
						"Asked client to change it's password");
				break;
			default:
				Console.log(LogType.Information, this,
						"recieved corupted data: " + line);
			}
		}
		in.close();
		out.close();
		try {
			s.close();
		} catch (IOException e) {
			Console.log(LogType.Error, this,
					"An error occured while closing the message stream connection");
			e.printStackTrace();
		}
	}

	public static void stop() {
		running = false;
	}

	@Override
	public String toString() {
		if (s != null)
			return "MessageStreamHandler:" + s.getInetAddress();
		else
			return "MessageStreamHandler:null";
	}

}
