package tm2;

import java.util.ArrayList;

/**
 * Stores information about a Tweet and the User who posted it
 * 
 * @author Matthew Kemmer v.1.0
 * @author Lauren Mathews v.2.0
 * @version 6/2011 v1.0
 * @version 6/25/12 v2.0
 */
public class WekaTweet {
	private User user;
	@SuppressWarnings("unused")
	private int length;
	private String url;
	@SuppressWarnings("unused")
	private String status;
	private double retweetPercentile;
	private boolean relevant;
	private String attributesCSVFormat;
	private double wekaScore;
	@SuppressWarnings("unused")
	private long tweetID;

	private String hashtag;
	private String person;
	private String emotes;
	private double excessOfMarks;
	private double numbers;

	/**
	 * Creates a Tweet object with the User who posted it and info about the
	 * Tweet and user
	 * 
	 * @param u
	 *            The User object that the Tweet is tied to
	 * @param i
	 *            The Tweet ID
	 * @param l
	 *            The length of the tweet
	 * @param url
	 *            True if the tweet contains a URL, false otherwise
	 */
	public WekaTweet(User u, long i, int l, String url, String status) {
		user = u;
		length = l;
		this.url = url;
		this.status = status;
		tweetID = i;
	}

	/**
	 * Creates a second Tweet object with info about the Tweet
	 * 
	 * @param h
	 *            Is true/false if the tweet contains a hashtag
	 * @param p
	 *            Is true/false if the tweet contains an @person
	 * @param e
	 *            Is true/false if the tweet contains emotes
	 * @param eM
	 *            Amount of ?/! in the tweet
	 * @param n
	 *            Amount of numbers (0-9) in the tweet
	 */
	public WekaTweet(long i, String u, String h, String p, String e, double eM,
			double n) {
		hashtag = h;
		person = p;
		emotes = e;
		excessOfMarks = eM;
		numbers = n;
		tweetID = i;
		url = u;
	}

	/**
	 * Initializes a weka constructor
	 * 
	 * @param Long
	 *            i the tweet ID
	 **/
	public WekaTweet(long i, String indicator) {
		hashtag = null;
		person = null;
		emotes = null;
		excessOfMarks = 0;
		numbers = 0;
		tweetID = i;
		url = null;
	}

	/**
	 * @return user person who sent tweet
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Gets the retweet percentage
	 */
	public double getRetweetPercentile() {
		return retweetPercentile;
	}

	/**
	 * Sets the retweet percentage
	 */
	public void setRetweetPercentile(double rp) {
		retweetPercentile = rp;
	}

	/**
	 * Makes a String that has the attributes you want formatted for the
	 * creation of a csv file
	 * 
	 * @param attributes
	 */
	public void setAttributes(ArrayList<String> attributes) {
		for (int i = 0; i < attributes.size(); i++) {
			// adds on each attribute as specified
			attributesCSVFormat = attributesCSVFormat + attributes.get(i);

			// this is to avoid adding a last field that is blank
			// when we create our csv file
			if (!(i == attributes.size() - 1)) {
				attributesCSVFormat += ",";
			}
		}
	}

	/**
	 * Returns if the Tweet is relevant to the topic it came from
	 * 
	 * @return True if the Tweet is relevant, false otherwise
	 */
	public boolean isRelevant() {
		return relevant;
	}

	/**
	 * Sets a score for a particular tweet
	 */
	public void setScore(double wekaScore) {
		this.wekaScore = wekaScore;
	}

	/**
	 * Returns the score given to the tweet by weka
	 * 
	 * @return wekaScore the score weka gave a tweet when using the attributes
	 *         to determine relevance
	 */
	public double getScore() {
		return wekaScore;

	}

	/**
	 * Sets the relevance of this Tweet
	 * 
	 * @param r
	 *            True if this Tweet is relevant, false otherwise
	 */
	public void setRelevance(boolean r) {
		relevant = r;
	}

	/**
	 * Returns the attributes of our tweets
	 * 
	 * @return the csv format in string of the attributes
	 */
	public String attributes() {
		return "url" + "," + "hashtag" + "," + "person" + "," + "emotes" + ","
				+ "excessOfMarks" + "," + "numbers" + "," + "relevant";
	}

	/**
	 * Returns the actual attributes and their number
	 * 
	 * @return attributes the actual attributes
	 */

	public String toString() {
		return url + "," + hashtag + "," + person + "," + emotes + ","
				+ excessOfMarks + "," + numbers + "," + relevant;
	}
}