package stirsx.util;

import java.util.*;

import STIRS.Lucene.RankedTweetList;
import STIRS.Lucene.Tweet;
import STIRS.QueryProcessor.LuceneQuery;

public class TimeFilter {
	public ArrayList<RankedTweetList> filter(ArrayList<RankedTweetList> rtl,
			ArrayList<LuceneQuery> queries) {
		for (int i = 0; i < rtl.size(); i++) {
			RankedTweetList currList = rtl.get(i);
			long timeBound = queries.get(i).getTweetTime();

			for (int j = 0; j < currList.size(); j++) {
				Tweet currTweet = currList.getTweet(j);

				if (Long.parseLong(currTweet.getTweetID()) > timeBound) {
					currList.remove(j);
					j--;
				}
			}
		}

		return rtl;
	}
}