package STIRS.Lucene;

import java.util.*;

/**
 * Working with classes Tweet and RankedTweetList, it "ups" the score of the
 * tweet if it has more information then the query, based on percentage.
 * 
 * @author Lauren Mathews
 * @version 7/6/12
 */
public class rescoreTweets {
	public static RankedTweetList rescore(RankedTweetList list, String query) {
		// Reads in tweet, tweetID, score and query throughout the list
		for (int i = 0; i < list.size(); i++) {
			Tweet currentTweet = list.getTweet(i);
			String tweetContent = currentTweet.getStatus();
			float score = currentTweet.getScore();
			String ID = currentTweet.getTweetID();
			// Calls below method to adjust score if needed
			float newScore = newScore(ID, tweetContent, score, query);
			currentTweet.setScore(newScore);
		}

		return list;
	}

	public static float newScore(String tweetID, String tweet, float curScore,
			String query) {
		// Fixes the tweet, for later use (to find it the whole tweet is just a
		// url or not)
		tweet = tweet.trim();

		// Creates a temp tweet, tweetCheck, that will be the tweet we use to
		// compare with
		String tweetCheck = tweet;

		// If the url is in tweet, makes tweetCheck the tweet without the url
		// If the tweet is just a url, returns the usual score.
		if ((tweet.contains("http://") || tweet.contains("https://") || tweet
				.contains("www.")) && tweet.indexOf(" ") != -1) {
			tweetCheck = tweet.substring(0, tweet.lastIndexOf(" "));
		} else if ((tweet.contains("http://") || tweet.contains("https://") || tweet
				.contains("www.")) && tweet.indexOf(" ") == -1) {
			return curScore;
		}

		// Sets both tweet(Check) and query to lowercase and trims for
		// comparison purposes below
		tweetCheck = tweetCheck.toLowerCase().trim();
		query = query.toLowerCase().trim();

		// Goes through the tweet and removes all non alphabet/number/space
		// values
		for (int i = 0; i < tweetCheck.length() - 1; i++) {
			char curChar = tweetCheck.charAt(i);
			String curWord = tweetCheck.substring(i, i + 1);
			// 48-57 (0-9); 97-122 (a-z)
			if (curChar < 97 || curChar > 122) {
				if (curChar < 48 || curChar > 57) {
					if (curChar != 32) {
						tweetCheck = tweetCheck.replace(curWord, "");
					}
				}
			}
		}

		// A counter the counts how many of the query's words are in the tweet
		double count = 0;

		// While loop: Goes through every tweet word and checks to see if it
		// matches a query word
		StringTokenizer sTT = new StringTokenizer(tweetCheck, " ");
		while (sTT.hasMoreTokens()) {
			String inputLine = sTT.nextToken();

			StringTokenizer sTQ = new StringTokenizer(query, " ");

			while (sTQ.hasMoreTokens()) {

				String lineInput = sTQ.nextToken();

				if (lineInput.equals(inputLine)) {
					count++;
					break;
				}
			}
		}

		// Creates a percent of how much of the tweet is the query
		double percent = count / tweetCheck.length() * 1000;

		// If 30% or more of the tweetCheck is made up of query, it returns
		// normal score
		// (because it resembles the query more then giving more information
		// then the query)
		if (percent > 30.0) {
			return curScore;
		} else {
			return (float) (curScore * 1.6);
		}
	}
}