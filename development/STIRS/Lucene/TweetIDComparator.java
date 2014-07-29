package STIRS.Lucene;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * A TweetIDComparator compares Tweets based on tweetID. Tweets with a lower
 * tweetID come before Tweets with a higher tweetID.
 * <p>
 * 
 * To sort a collection of Tweets on tweetID:
 * <code>Collections.sort(tweets, new TweetIDComparator());</code>
 * <p>
 * 
 * @author David Purcell
 */
public class TweetIDComparator implements Comparator<Tweet> {

	public int compare(Tweet tweetA, Tweet tweetB) {
		return new BigInteger(tweetA.getTweetID()).compareTo(new BigInteger(
				tweetB.getTweetID()));
	}
}