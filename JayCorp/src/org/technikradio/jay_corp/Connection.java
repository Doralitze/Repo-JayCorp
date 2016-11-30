/*
JayCorp-Client/Connection.java
Copyright (C) 2015-2016  Leon C. Dietrich (Doralitze)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
			Console.log(LogType.StdOut, this, "Trying to conntect to "
									+ SERVER_ADDRESS + ":" + SERVER_PORT);
			socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			ps = new PrintStream(socket.getOutputStream(), true, "UTF-8");
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
