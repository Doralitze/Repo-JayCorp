package org.technikradio.jay_corp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class Connection {

	private static final String SERVER_ADDRESS = Settings
			.getString("Connection.Address"); //$NON-NLS-1$
	private static final int SERVER_PORT = Integer.parseInt(Settings
			.getString("Connection.Port")); //$NON-NLS-1$
	private Socket socket = null;
	private BufferedReader br = null;
	private PrintStream ps = null;
	private boolean connectionEstablished = false;

	public Connection() {
		try {
			socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			ps = new PrintStream(socket.getOutputStream());
			connectionEstablished = true;
		} catch (IOException e) {
			Console.log(LogType.Error, this, e.toString());
		}
	}

	public void transmit(String s) {
		if (connectionEstablished)
			ps.println(s);
		else {
			Console.log(LogType.Error, this, "No connection established yet."); //$NON-NLS-1$
		}
	}

	public String receive() throws IOException {
		if (connectionEstablished) {
			String s = br.readLine();
			// System.out.println(s);
			return s;
		} else {
			Console.log(LogType.Error, this, "No connection established yet."); //$NON-NLS-1$
			return "null"; //$NON-NLS-1$
		}
	}

	public void stop() throws IOException {
		ps.close();
		br.close();
		socket.close();
	}

	@Override
	public String toString() {
		return "ServerConnection"; //$NON-NLS-1$
	}

	public boolean isValidConnection() {
		return connectionEstablished;
	}
}
