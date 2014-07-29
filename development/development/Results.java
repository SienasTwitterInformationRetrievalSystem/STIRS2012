package development;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import STIRS.Lucene.RankedTweetList;
import STIRS.Lucene.Tweet;
import STIRS.Lucene.TweetIDComparator;
import STIRS.Lucene.TweetScoreComparator;
import STIRS.QueryProcessor.LuceneQuery;

/**
 * Outputs the Tweet results in the TREC format
 * 
 * @author Matthew Kemmer and Carl Tompkins v1.0
 * @edited Karl Appel (with added comments) v2.0
 * 
 * @version 7/8/2011 v1.0
 * @version 6/20/12 v2.0
 */
public class Results {

	/**
	 * Outputs the results in the TREC format based on a list of Tweet objects
	 * 
	 * @param tweets An ArrayList of Tweets
	 * @param raw boolean to determine whether we want tweets printed out in TREC format
	 * @param output output file extension such as .csv 
	 * @param numHits the number of hits we wanted to get for each topic/query
	 * @param task the TREC task we are requested to get 
	 * @param allResults determines whether we want the max results possible or not
	 * @param maxOutput the maximum output we want for our module overidden by all Results 
	 * @param startTopic the topic we started at minus 1      
	 */
	public static void output(ArrayList<RankedTweetList> tweets, boolean raw,
			String output, String task, boolean allResults, int maxOutputNum,
			int startTopic, String tag, boolean scoreComparator,
			ArrayList<LuceneQuery> queries) {

		// declares the file output name with the correct format
		String fileName = "output." + output;

		if (allResults) {
			// this just initializes the max boundary really high that
			// way we can get all of the results
			maxOutputNum = 100000;
		}
		
		if (task != null && task.equals("adhoc")) {
			maxOutputNum = 10000;
		}

		try {
			// object we use to write out to the file we specified
			FileWriter writer = new FileWriter(fileName);
			BufferedWriter bfwriter = new BufferedWriter(writer);

			// if the data is not TREC format then our output format heading
			// is different and is set appropriately
			if (!raw){
				writer.append("Run\nJudge\nTopic Num,TweetID,Tweet\n");
			}

			// System.out.println("Num Queries: " + tweets.size());
			for (int i = 0; i < tweets.size(); i++) {
				// DEBUG CODE
				// gets the ranked list of tweets for each topic we want to
				// print out
				RankedTweetList rtl = (RankedTweetList) tweets.get(i);
				System.out.println("Query num: " + i);
				System.out.println("Num Tweets: " + rtl.size());

				// gets the list of those tweets and then sorts them
				List<Tweet> sublist = rtl.getRankedList().subList(0,
						Math.min(rtl.size(), maxOutputNum));

				if (scoreComparator) {
					Collections.sort(sublist, Collections
							.reverseOrder(new TweetScoreComparator()));
				} else {
					Collections.sort(sublist,
							Collections.reverseOrder(new TweetIDComparator()));
				}
				
				// if the task is not defined then we print out
				// the default TREC 2011 format
				if (task == null) {
					for (int j = 0; j < Math.min(rtl.size(), maxOutputNum); j++) {
						// System.out.println("Tweet num: " + j);
						Tweet tweet = (rtl.getRankedList()).get(j);
						bfwriter.append(tweet.format(raw));
						bfwriter.newLine();
						bfwriter.flush();
					}
					bfwriter.newLine();

					// otherwise we print out the TREC 2012 format
				} else if (task.equals("adhoc") || task.equals("filtering")) {
					for (int j = 0; j < rtl.size(); j++) {
						// System.out.println("Tweet num: " + j);
						Tweet tweet = (rtl.getRankedList()).get(j);
						tweet.setTopic(Integer.parseInt(queries.get(i)
								.getQueryNum()));
						tweet.setTag(tag);
						bfwriter.append(tweet.format(raw, task));
						bfwriter.newLine();
						bfwriter.flush();
					}

					bfwriter.newLine();
					bfwriter.flush();
				}
			}
			System.out.println("Closing file...");
			bfwriter.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}