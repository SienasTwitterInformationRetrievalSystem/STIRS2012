package tm2;

import java.io.*;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * A database that contains information of Twitter users
 * 
 * @author Matthew Kemmer v1.0
 * @version 7/14/2011 v1.0
 */
public class UserDatabase {
	private Hashtable<String, User> users;

	/**
	 * Loads in the user list, which is expected to be in the same folder
	 */
	public UserDatabase() {
		users = new Hashtable<String, User>();
		fillUsers();
	}

	/**
	 * Fills the users from the text file containing user information
	 */
	private void fillUsers() {
		try {
			Scanner s = new Scanner(new File("twitterDataAllUsers.txt"));

			while (s.hasNextLine()) {
				String[] line = s.nextLine().split(",");

				if (line.length == 7) {
					String name = line[0].toLowerCase();
					int numStatuses = Integer.parseInt(line[1]);
					long date = Long.parseLong(line[2]);
					int list = Integer.parseInt(line[3]);
					int fol = Integer.parseInt(line[4]);
					int fr = Integer.parseInt(line[5]);
					boolean celeb = line[6].equalsIgnoreCase("true");

					User u = new User(name, numStatuses, date, list, fol, fr,
							celeb);

					users.put(line[0], u);
				}
				s.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("twitterDataAllUsers.txt not found");
		}
	}

	/**
	 * Returns a User object given a name, if the user is in the database
	 * 
	 * @param n
	 *            The name of the user, not case sensitive
	 * 
	 * @return A User object that contains information about the user if they
	 *         are in the database, null otherwise
	 */
	public User getUser(String n) {
		if (n == null) {
			return null;
		}

		return users.get(n.toLowerCase());
	}
}