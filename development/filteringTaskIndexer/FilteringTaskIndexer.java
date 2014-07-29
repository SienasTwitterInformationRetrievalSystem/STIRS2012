package filteringTaskIndexer;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import STIRS.Lucene.TwitterIndexer;
import STIRS.QueryProcessor.LuceneQuery;
import STIRS.QueryProcessor.QueryProcessor;
import stirsx.tm1.TwitterIndexerUrls;

public class FilteringTaskIndexer {
	private final static Logger LOGGER = Logger
			.getLogger(FilteringTaskIndexer.class.getName());

	public static void main(String args[]) throws Exception {
		ArrayList<TweetIDComparable> tweetAL = new ArrayList<TweetIDComparable>();
		boolean url = false;

		BufferedReader bfr = new BufferedReader(new FileReader(args[0])); // englishCorpus
		if (args[2].equals("true")) {
			url = true;
		}

		String inputLine = bfr.readLine();
		while (inputLine != null) {
			StringTokenizer sT = new StringTokenizer(inputLine, "\t");

			String tweetID = sT.nextToken();
			String username = sT.nextToken();
			sT.nextToken();
			String dateTime = sT.nextToken();
			String tweet = sT.nextToken();

			TweetIDComparable tic = new TweetIDComparable(tweetID, tweet,
					username, dateTime, "0", 0, 0, 0, inputLine);

			tweetAL.add(tic);
			inputLine = bfr.readLine();
		}

		bfr.close();

		QueryProcessor processor = new QueryProcessor(new File(args[1]),
				LOGGER, "filtering");

		ArrayList<LuceneQuery> queries = processor.getSanitizedQueries();

		for (int i = 0; i < queries.size(); i++) {
			BufferedWriter bfw = new BufferedWriter(new FileWriter("temp.txt"));
			LuceneQuery currentQuery = queries.get(i);
			Long firstTime = currentQuery.getTweetTime();
			TweetIDComparable firstTimeTweet = new TweetIDComparable(
					Long.toString(firstTime), null, null, null, "0", 0, 0, 0,
					null);
			Long endTime = currentQuery.getNewestTweetTime();
			TweetIDComparable endTimeTweet = new TweetIDComparable(
					Long.toString(endTime), null, null, null, "0", 0, 0, 0,
					null);
			int firstIndex = BinarySearch.binarySearchMin(tweetAL,
					firstTimeTweet, 0, tweetAL.size());
			int endIndex = BinarySearch.binarySearchMax(tweetAL, endTimeTweet,
					0, tweetAL.size());

			for (int j = firstIndex; j < endIndex; j++) {
				TweetIDComparable curTweet = tweetAL.get(j);
				bfw.write(curTweet.getRawTweetInfo());
				bfw.newLine();
				bfw.flush();
			}
			
			bfw.close();
			
			if (!url) {
				new TwitterIndexer("/home/gituser/indexes/" + firstTime + "TO"
						+ endTime, true, "temp.txt");
			} else {
				new TwitterIndexerUrls("/home/gituser/indexes/" + firstTime
						+ "TO" + endTime, true, "temp.txt");
			}
		}
	}
}