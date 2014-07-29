package tm2;

import stirsx.tm1.URLListGen;

/**
 * Contains static methods which return the length of a Tweet and if it contains
 * a URL
 * 
 * @author Denis Kalic & Matthew Kemmer v1.0
 * @version 7/14/2011 v1.0
 */
public class TweetData {

	/**
	 * Returns the length of a Tweet
	 * 
	 * @return The number of characters in the Tweet
	 */
	public static int length(String tweet) {
		if (tweet == null){
			return 0;
		}

		int length = 0;

		for (int i = 0; i < tweet.length(); i++) {
			if (!Character.isWhitespace(tweet.charAt(i)))
				length++;
		}

		return length;
	}

	/**
	 * Returns if the tweet contains a URL
	 * 
	 * @return 1 if the tweet contains a URL, 0 if not
	 */
	public static boolean url(String checkString) {
		return URLListGen.containsUrl(checkString);
	}
}