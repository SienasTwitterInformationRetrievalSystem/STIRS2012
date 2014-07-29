package tm2;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Scrapes the website Listorious for Twitter users who post on a given topic
 * 
 * @authoer Denis Kalic & Matthew Kemmer v1.0
 * @version 7/14/2011 v1.0
 */
public class Listorious {

	/**
	 * Scrapes Listorious for a given topic and returns a list of usernames from
	 * the site
	 * 
	 * @param topic
	 *            The topic term(s) to input to Listorious
	 * 
	 * @return An ArrayList of usernames from the first page of Listorious
	 */
	public static ArrayList<String> getUserNames(String topic) {
		// tweetFollower = new HashMap();
		ArrayList<String> tweetID = new ArrayList<String>();

		// Will search on Listorious for all given topics and extract TweetID's
		if (urlContent(topic, tweetID) > 0) {
			return tweetID;
		} else {
			return null;
		}
	}

	/**
	 * Checks if a user is an expert on a given topic, ie, if they are on the
	 * list of usernames from the Listorious results given the topic.
	 * 
	 * @param topic
	 *            The topic as input to Listorious
	 * @param user
	 *            The username to check against
	 * 
	 * @return True if the user appears on the list of users from Listorious,
	 *         false otherwise
	 */
	public static boolean isExpert(String topic, String user) {
		ArrayList<String> results = getUserNames(topic);

		if (results != null) {
			for (String u : results) {
				if (user.equalsIgnoreCase(u)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Scrapes Listorious with the given topic and adds the returned usernames
	 * to the given list
	 * 
	 * @param topic
	 *            The topic to input to Listorious
	 * @param tweetID
	 *            The list to add usernames to
	 * 
	 * @return 1 if the method runs correctly, -1 if not
	 */
	private static int urlContent(String topic, ArrayList<String> tweetID) {
		// UPDATE INFO: Still needs code to process multiple pages of
		// Listorious?
		try {
			String thisLine;
			String urlContentString = new String();
			URL u = new URL("http://listorious.com/search/people?q=" + topic
					+ "&search=Searchtopic");
			BufferedReader theHTML = new BufferedReader(new InputStreamReader(
					u.openStream()));

			while ((thisLine = theHTML.readLine()) != null) {
				urlContentString = urlContentString + " " + thisLine;
			}

			boolean stillLooking = true;

			while (stillLooking) {
				int index1, index2;
				String tempID = new String();
				index1 = urlContentString.indexOf("username");
				if (index1 != -1) {
					String newSub = urlContentString.substring(index1);
					index2 = newSub.indexOf("><img");
					index1 = newSub.indexOf("username");
					tempID = newSub.substring(index1 + 10, index2 - 1);
					tweetID.add(tempID);
					int followIndex = newSub.indexOf("followers");
					urlContentString = newSub.substring(followIndex);
				} else {
					stillLooking = false;
				}
			}
		} catch (MalformedURLException e) {
			System.err.println(e);
			return -1;
		} catch (IOException e) {
			System.err.println(e);
			return -1;
		}

		return 1;
	}
}