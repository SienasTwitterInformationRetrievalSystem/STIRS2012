package tm2;

import java.util.*;
import java.io.*;

/**
 * Finds the attributes (Hashtag, @person, emotes, excess?/!, numbers) in the
 * tweet
 * 
 * @author Lauren Mathews v1.0
 * @version 6/27/12 v1.0
 */
public class ProcessAttributes {

	private String tweet;
	private String hashtag;
	private String person;
	private String emotes;
	private double excessOfMarks;
	private double numbers;
	private long tweetID;

	public ProcessAttributes(String t, long tweetID) {
		this.tweet = t;
		this.tweetID = tweetID;
	}

	public void getEmotes(boolean urlThere, String tweet) throws IOException {
		String tweetCheck = "";

		// originally made emoteCollectiong.txt first run through
		BufferedReader bfr2 = new BufferedReader(new FileReader(
				"emoteCollection.txt"));

		// Keeps track if the emote is found in tweet
		boolean emoteThere = false;

		// Goes through each line of the emoteCollection to see if
		// the current tweet line contains one of the most used emotes
		String lineInput = bfr2.readLine();

		while (lineInput != null) {

			// Splits the emote checker between those that contain urls or not
			// If the tweet does contain a url, it finds the url
			// and only checks for emotes in the tweet without the url
			// We are doing this because the emote slash is effecting the emotes
			if (urlThere) {
				// Tokenizes the tweet; loops through the tweet until url is
				// located
				String foundURL = tokenizesTweet(tweet);

				// Takes care of those tweets that starts with url
				if (tweet.startsWith(foundURL) && tweet.indexOf(" ") != -1) {
					tweetCheck = tweet.substring(tweet.indexOf(" "),
							tweet.length());

					while (tweetCheck.contains("http://")
							|| tweetCheck.contains("https://")
							|| tweetCheck.contains("www.")) {
						String temp = tweetCheck;
						tweetCheck = temp.substring(temp.indexOf(" "),
								temp.length());
					}

					if (tweetCheck.contains(lineInput)) {
						emotes = "Yes";
						emoteThere = true;
						break;
					}
					// Takes care of those tweets that only have a url, and thus
					// no emote
				} else if (tweet.startsWith(foundURL)
						&& tweet.indexOf(" ") == -1) {
					emotes = "No";
					emoteThere = true;
					break;
					// Takes care of those tweets that contain a tweet somewhere
					// in tweet
				} else if (!foundURL.equals("")) {
					int newIndex = 0;
					newIndex = tweet.indexOf(foundURL) + foundURL.length();
					tweetCheck = tweet.substring(0, tweet.indexOf(foundURL))
							+ tweet.substring(newIndex, tweet.length());

					while (tweetCheck.contains("http://")
							|| tweetCheck.contains("https://")
							|| tweetCheck.contains("www.")) {
						String temp = tweetCheck;
						String newFoundUrl = tokenizesTweet(temp);
						newIndex = temp.indexOf(newFoundUrl)
								+ newFoundUrl.length();
						tweetCheck = temp.substring(0,
								temp.indexOf(newFoundUrl))
								+ temp.substring(newIndex, temp.length());
					}

					if (tweetCheck.contains(lineInput)) {
						emotes = "Yes";
						emoteThere = true;
						break;
					}
				}
				// If there is no url, checks through line for emotes
			} else if (!urlThere) {
				if (tweet.contains(lineInput)) {
					emotes = "Yes";
					emoteThere = true;
					break;
				}
			}
			lineInput = bfr2.readLine();
		}

		// If no emote is ever found through the above loop, writes "No"
		if (!emoteThere) {
			emotes = "No";
		}
		bfr2.close();
	}

	public void getExcessOfMarks(String tweetCheck) {
		double excessMarks = 0;
		for (int i = 0; i < tweetCheck.length() - 1; i++) {
			String currentLetter = tweetCheck.substring(i, i + 1);
			if (currentLetter.equals("!") || currentLetter.equals("?")) {
				excessMarks++;
			}
		}
		double excessPercent = excessMarks / tweetCheck.length();
		excessOfMarks = excessPercent;
	}

	public void getNumbers(String tweetCheck) {
		double numberInTweet = 0;

		for (int i = 0; i < tweetCheck.length(); i++) {
			char currentLetter = tweetCheck.charAt(i);

			for (int j = 48; j < 58; j++) {
				if (currentLetter == j) {
					numberInTweet++;
				}
			}
		}

		// Writes percent of tweet that is numbers
		double numberPercent = numberInTweet / tweetCheck.length();
		numbers = numberPercent;
	}

	public String tokenizesTweet(String tweet) {
		StringTokenizer sTURLtweet = new StringTokenizer(tweet, " ");
		String foundURL = "";

		while (sTURLtweet.hasMoreTokens()) {
			String findUrlInTweet = sTURLtweet.nextToken();

			if (findUrlInTweet.startsWith("http://")
					|| findUrlInTweet.startsWith("https://")
					|| findUrlInTweet.startsWith("www.")) {
				foundURL = findUrlInTweet;
				return foundURL;
			} else if (findUrlInTweet.contains("http://")
					|| findUrlInTweet.contains("https://")
					|| findUrlInTweet.contains("www.")) {
				int urlIndexOf = findUrlInTweet.indexOf("http://");

				if (urlIndexOf == -1) {
					urlIndexOf = findUrlInTweet.indexOf("https://");

					if (urlIndexOf == -1) {
						urlIndexOf = findUrlInTweet.indexOf("www.");
					}
				}

				foundURL = findUrlInTweet.substring(urlIndexOf,
						findUrlInTweet.length());
				return foundURL;
			}
		}

		return foundURL;
	}

	public WekaTweet processWekaTweet() {
		String tweetCheck = "";
		boolean urlThere = false;

		if (tweet.contains("http://") || tweet.contains("https://")
				|| tweet.contains("www.")) {
			urlThere = true;
		}

		String url = (urlThere) ? "Yes" : "No";

		if (tweet.equals("null")) {
			WekaTweet wt = new WekaTweet(tweetID, "null");
			return wt;
		} else {
			hashtag = (tweet.startsWith("#") || tweet.contains(" #")) ? "Yes"
					: "No";
			person = (tweet.startsWith("@") || tweet.contains(" @")) ? "Yes"
					: "No";
			try {
				getEmotes(urlThere, tweet);
			} catch (IOException ioe) {
				System.out
						.println("IOException in ProcessAtrributes.java class!");
			}

			if (urlThere) {
				// Tokenizes the tweet; loops through the tweet until url is
				// located
				String foundURL = tokenizesTweet(tweet);

				// Takes care of those tweets that starts with a url
				if (tweet.startsWith(foundURL) && tweet.indexOf(" ") != -1) {
					tweetCheck = tweet.substring(tweet.indexOf(" "),
							tweet.length());
					while (tweetCheck.contains("http://")
							|| tweetCheck.contains("https://")
							|| tweetCheck.contains("www.")) {
						String temp = tweetCheck;
						tweetCheck = temp.substring(temp.indexOf(" "),
								temp.length());
					}
					getExcessOfMarks(tweetCheck);
					getNumbers(tweetCheck);
				} else if (tweet.startsWith(foundURL)
						&& tweet.indexOf(" ") == -1) {
					excessOfMarks = 0.0;
					numbers = 0.0;
					// Takes care of those tweets that have IT somewhere in
					// tweet
				} else if (!foundURL.equals("")) {
					int newIndex = 0;
					newIndex = tweet.indexOf(foundURL) + foundURL.length();
					tweetCheck = tweet.substring(0, tweet.indexOf(foundURL))
							+ tweet.substring(newIndex, tweet.length());

					while (tweetCheck.contains("http://")
							|| tweetCheck.contains("https://")
							|| tweetCheck.contains("www.")) {
						String temp = tweetCheck;
						String newFoundUrl = tokenizesTweet(temp);
						newIndex = temp.indexOf(newFoundUrl)
								+ newFoundUrl.length();
						tweetCheck = temp.substring(0,
								temp.indexOf(newFoundUrl))
								+ temp.substring(newIndex, temp.length());
					}

					getExcessOfMarks(tweetCheck);
					getNumbers(tweetCheck);
				}
				// If the tweet doesn't have a url, just searches and finds all
				// OF IT
			} else if (!urlThere) {
				getExcessOfMarks(tweet);
				getNumbers(tweet);
			}
			WekaTweet wt = new WekaTweet(tweetID, url, hashtag, person, emotes,
					excessOfMarks, numbers);
			return wt;
		}
	}
}