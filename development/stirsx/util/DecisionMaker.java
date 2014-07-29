package stirsx.util;

import java.util.*;

import STIRS.Lucene.Tweet;

/**
 * Takes in an arraylist of tweet. Goes through the arraylist and gets the
 * score. If the score is greater than or equal to 1 then you change the
 * decision using the decision method to yes. Otherwise you set it to no.
 * Returns the arraylists of tweets.
 */
public class DecisionMaker {
	public static ArrayList<Tweet> makeDecision(ArrayList<Tweet> tweetCAL) {
		for (int i = 0; i < tweetCAL.size(); i++) {
			Tweet curTweet = tweetCAL.get(i);
			Float curScore = curTweet.getScore();
			
			if (curScore < 1.0) {
				curTweet.setDecision("No");
			} else {
				curTweet.setDecision("Yes");
			}
		}
		return tweetCAL;
	}
}