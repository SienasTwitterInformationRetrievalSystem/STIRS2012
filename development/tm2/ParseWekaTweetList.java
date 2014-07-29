package tm2;

import java.util.ArrayList;

/**
 * This class will parse the String appropriately in order for Weka, the
 * attributes that are wanted can be called upon and retrieved
 * 
 * @author Karl Appel v1.0
 * @version 6/2012 v1.0
 */
public class ParseWekaTweetList {

	private ArrayList<WekaTweet> wekaTweets;
	@SuppressWarnings("unused")
	private static final String attributes = "url,hashtag,@person,emotes,excessOf?Or!,numbers,relevance";

	public ParseWekaTweetList() {}

	/**
	 * Loads the Weka Tweets into the arraylist
	 */
	public void loadList(ArrayList<WekaTweet> wekaTweets) {
		this.wekaTweets = wekaTweets;
	}

	public void parseTweets() {
		for (int i = 0; i < wekaTweets.size(); i++) {
			@SuppressWarnings("unused")
			WekaTweet currentTweet = wekaTweets.get(i);
			@SuppressWarnings("unused")
			String status = "";
		}
	}
}