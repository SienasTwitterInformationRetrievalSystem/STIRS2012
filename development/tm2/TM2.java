package tm2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;

import STIRS.Lucene.RankedTweetList;
import stirsx.tm1.Module;
import STIRS.QueryProcessor.LuceneQuery;

/**
 * Twitter Module 2, which uses Weka and data retrieved from the Twitter API to
 * rank tweets
 * 
 * @author Denis Kalic & Matthew Kemmer v1.0
 * @version 7/18/2011 v1.0
 */
public class TM2 implements Module {

	/**
	 * Takes in a list of tweets, and ranks them based on the Weka decision tree
	 * and other data about the tweet and from Twitter.
	 * 
	 * @param queries
	 *            A list of LuceneQuery objects which correspond to the TREC
	 *            queries
	 * @param tweetList
	 *            A list of RankedTweetList objects, which have the Lucene
	 *            results for each query
	 * @results The same RankedTweetList that was taken in, but modified to
	 *          reflect the TM2 results
	 */
	public List<RankedTweetList> getResults(List<LuceneQuery> queries,
			List<RankedTweetList> tweetList) {

		// this is a list of ranked tweets that will be returned
		ArrayList<RankedTweetList> returnedRankedTweets = new ArrayList<RankedTweetList>();

		// goes through each ranked tweet list and makes a new instance of
		// a weka tweet which has more information about a tweet than
		// a regular tweet for relevance purposes
		for (int i = 0; i < tweetList.size(); i++) {

			// Prints out information for user
			System.out.println("The number of tweets for query" + i
					+ " equals " + tweetList.get(i).size());

			// Gets current ranked tweet list
			RankedTweetList rankedTweet = tweetList.get(i);
			ArrayList<WekaTweet> wekaTweetList = new ArrayList<WekaTweet>();

			// each tweet a new weka tweet will be created
			for (int x = 0; x < rankedTweet.size(); x++) {
				STIRS.Lucene.Tweet singleTweet = rankedTweet.getTweet(x);

				// tweet information/content
				String currentStatus = singleTweet.getStatus();

				// tweet ID
				long tweeterID = Long.parseLong(singleTweet.getTweetID());

				// New instance of the weka processor
				ProcessAttributes processer = new ProcessAttributes(
						currentStatus, tweeterID);

				// processes weka tweet
				WekaTweet wekaTweet = processer.processWekaTweet();
				wekaTweetList.add(wekaTweet);
			}
			
			try {
				// creates the training set for the tweets, this is done once
				// for each topic
				createTrainingSet(wekaTweetList);

				// we created a csv file when we initializes the code above so
				// we now are
				// converting it to a format weka likes
				new ARFFCreator("trainingSet/tweetSet.csv",
						"trainingSet/tweetSet.arff");
			} catch (Exception e) {
				System.out.println("ERROR");
			}
			
			// Creates a new instance of the weka object
			Weka w = new Weka();

			// gives an error if weka is not loaded correctly
			// and returns the list as such
			if (!w.loadedCorrectly()) {
				System.out.println("Weka was not loaded correctly");
				return tweetList;
			} else {
				System.out.println("WEKA HAS LOADED CORRECTLY");
			}

			// runs weka
			w.run(wekaTweetList);

			// returns the original list
			ArrayList<STIRS.Lucene.Tweet> originalList = rankedTweet
					.getRankedList();

			// resets the score of the original tweet after it has been
			// through weka
			for (int y = 0; y < originalList.size(); y++) {
				// matches the original tweet with its respective weka tweet
				STIRS.Lucene.Tweet originalTweet = originalList.get(y);
				WekaTweet relevantTweet = wekaTweetList.get(y);

				// gets the score and changes it to a float
				double currScore = relevantTweet.getScore();
				float floatScore = (float) currScore;

				// changes the original tweet's score
				originalTweet.setScore(floatScore * originalTweet.getScore());
			}

			// sorts the list
			Collections.sort(originalList);

			// adds the new list to a arraylist of to be returned ranked
			// tweet list
			returnedRankedTweets.add(new RankedTweetList(originalList));
		}

		// returns the ranked tweet lists
		return returnedRankedTweets;
	}

	/**
	 * Creates the trainingSet.csv file of the current attributes for WEKA use.
	 * 
	 * @param testSet
	 *            Contains the attributes in ArrayList form, to be converted.
	 */
	public void createTrainingSet(ArrayList<WekaTweet> testSet)
			throws IOException {
		System.out.println("Creating Training Set");
		BufferedWriter bfw = new BufferedWriter(new FileWriter(
				"trainingSet/tweetSet.csv"));

		// gets the attributes to read
		bfw.write(testSet.get(0).attributes());

		// writes each line to a file using csv format
		for (int i = 0; i < testSet.size(); i++) {
			bfw.newLine();
			WekaTweet currentTweet = testSet.get(i);
			bfw.write(currentTweet.toString());
			bfw.flush();
		}
		
		bfw.close();
	}
}