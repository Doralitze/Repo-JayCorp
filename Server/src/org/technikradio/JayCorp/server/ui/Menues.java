package org.technikradio.JayCorp.server.ui;

import java.util.Scanner;

import org.technikradio.JayCorp.server.Data;
import org.technikradio.jay_corp.user.Righttable;
import org.technikradio.jay_corp.user.User;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class Menues {

	public static boolean getBoolInput(Scanner s) {
		while (true)
			switch (s.nextLine()) {
			case "n":
			case "N":
			case "no":
			case "No":
				return false;
			case "y":
			case "Y":
			case "j":
			case "J":
			case "yes":
			case "Yes":
			case "ja":
			case "Ja":
				return true;
			default:
				System.out.println("Please type y/n");
			}
	}

	public static void addUser() {
		try {
			int validEnter = 0;
			Scanner s = new Scanner(System.in);
			User u = new User();
			System.out.println("Please type in the following informations");
			System.out.print("Full name: ");
			u.setName(s.nextLine());
			System.out.print("Username: ");
			u.setUsername(s.nextLine());
			System.out.print("Age of work: ");
			u.setWorkAge(Integer.parseInt(s.nextLine()));
			if (u.getWorkAge() < 0) {
				Console.log(LogType.Warning, "AddUserCommand",
						"An working age lower than 0 is reserved for root only! It will be set to 0.");
				u.setWorkAge(0);
			}
			System.out.print("Extra free days: ");
			u.setExtraDays(Integer.parseInt(s.nextLine()));
			Righttable r = new Righttable();
			System.out
					.print("Access other user input allowed (read only)<y/n>: ");
			r.setAccessUserInputAllowed(getBoolInput(s));
			System.out.print("Add new user allowed <y/n>: ");
			r.setAddUserAllowed(getBoolInput(s));
			System.out.print("Edit user properties allowed <y/n>: ");
			r.setEditUserAllowed(getBoolInput(s));
			System.out.print("Edit other user input allowed <y/n>: ");
			r.setEditUserInputAllowed(getBoolInput(s));
			System.out.print("Request number of useres allowed <y/n>: ");
			r.setGetIDCountAllowed(getBoolInput(s));
			System.out.print("List all registered users allowed <y/n>: ");
			r.setListAllUsersAllowed(getBoolInput(s));
			System.out.print("Open / close input mode allowed <y/n>: ");
			r.setOpenCloseEditAllowed(getBoolInput(s));
			System.out.print("Display other user selections allowed <y/n>: ");
			r.setViewOtherSelectionsAllowed(getBoolInput(s));
			u.setRights(r);
			while (validEnter < 5) {
				System.out.println();
				System.out.print("Please enter a password: ");
				String pw1 = s.nextLine();
				System.out.print("Please repeat the password: ");
				String pw2 = s.nextLine();
				if (pw1.equals(pw2)) {
					validEnter = 6;
					u.setPassword(pw1);
				} else {
					validEnter++;
					if (validEnter < 5)
						System.out
								.println("The passwords mismatch. Please enter again");
					else
						System.out.println("The passwords mismatch. Abort.");
				}
			}
			if (validEnter == 6) {
				u.setSelectedDays(Data.getDefaultConfiguration().clone());
				Data.addUser(u);
				Console.log(LogType.Information, "AddUserCommand",
						"Successfully created new User");
			}
		} catch (Exception e) {
			Console.log(LogType.Error, "AddUserCommand",
					"Couldn´t create user:");
		}
	}
}
