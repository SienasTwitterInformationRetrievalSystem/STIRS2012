package stirsx.util;

import java.util.ArrayList;

import STIRS.Lucene.RankedTweetList;
import STIRS.Lucene.Tweet;

/**
 * This class normalizes a set of ranked tweet list so there scores are between
 * 0 and 1(inclusive). This is done by dividing the highest score for each tweet
 * with each current tweet.
 * 
 * @author Karl Appel v1.0
 * @version 6/2012
 */
public class Normalizer {

	public Normalizer() {}

	/**
	 * Method that initiates the normalization of the tweets
	 * 
	 * @param rankedTweetLists
	 *            lists before normalization
	 * @return rankedTweetLists lists after normalization
	 */
	public ArrayList<RankedTweetList> normalize(
			ArrayList<RankedTweetList> rankedTweetLists) {

		// each list will be normalized individually
		for (int i = 0; i < rankedTweetLists.size(); i++) {

			RankedTweetList currentList = rankedTweetLists.get(i);

			// gets the first tweet score and uses that as
			// a constant to divide with the other scores
			Tweet firstTweet = currentList.getTweet(0);
			float normalizedConstant = firstTweet.getScore();

			// sets the score to 1.0
			firstTweet.setScore((float) 1.0);

			// for each tweet the tweet scores are normalized as described in
			// class
			// description
			for (int j = 1; j < currentList.size(); j++) {
				Tweet currentTweet = currentList.getTweet(j);
				currentTweet.setScore(currentTweet.getScore()
						/ normalizedConstant);
			}
		}

		// returns rankedtweetlist with normalized scores
		return rankedTweetLists;
	}
}