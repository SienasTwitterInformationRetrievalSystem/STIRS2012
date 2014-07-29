package stirsx.tm1;

import STIRS.Lucene.LuceneSearch;
import STIRS.Lucene.RankedTweetList;
import STIRS.Lucene.Tweet;
import STIRS.QueryProcessor.LuceneQuery;
import stirsx.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

/**
 * The RankedJoin class combines two ranked lists of tweets into a single ranked
 * list.
 * 
 * @author David Purcell v1.0
 * @edited Karl Appel v2.0
 * 
 * @version 6/2011 v1.0
 * @version 6/2012 v2.0
 */
public final class RankedJoin implements Module {

	// The penalty applied to tweet IDs that do not have a high ranking URL.
	private static final float DEFAULT_NON_URL_PENALTY = 0.0f;
	private int hitNum;
	private static final float DEFAULT_RELIABLE_WEBSITE_BONUS = 0.0f;

	// determines whether the hashSet was read in correctly
	boolean success = true;

	private String task;
	private ArrayList<String> urlIndexPaths;

	/**
	 * The bonus given to tweet IDs that have high raking tweet content and URL
	 * content.
	 */
	private static final float DEFAULT_INTERSECT_BONUS = 0.2f;

	/**
	 * The path to the Lucene index of tweet content.
	 */
	private static final String DEFAULT_TWEET_INDEX = "/compLing/index/englishCorpusIndex";

	/**
	 * The path to the Lucene index of URL content.
	 */
	private static final String DEFAULT_URL_INDEX = "/compLing/home/kappel/completeURLIndex";

	@SuppressWarnings("unused")
	private int returnValue;
	private final String urlIndexFile;
	private float nonUrlPenalty;
	private float intersectBonus;

	/**
	 * Create a RankedJoin object.
	 * <p>
	 * Use the {@link #getResults(java.util.List, java.util.List)} method to
	 * retrieve results.
	 * 
	 * @see #getResults(java.util.List, java.util.List)
	 */
	public RankedJoin() {
		nonUrlPenalty = DEFAULT_NON_URL_PENALTY;
		intersectBonus = DEFAULT_INTERSECT_BONUS;
		urlIndexFile = DEFAULT_URL_INDEX;
		this.returnValue = 1500;
		this.hitNum = 1500;
	}

	/**
	 * Create a RankedJoin object by specifying the path to the URL content
	 * index.
	 * 
	 * @param urlIndex
	 *            The path to the URL index.
	 * @see #getResults(java.util.List, java.util.List)
	 */
	public RankedJoin(String urlIndex) {
		nonUrlPenalty = DEFAULT_NON_URL_PENALTY;
		intersectBonus = DEFAULT_INTERSECT_BONUS;
		this.urlIndexFile = urlIndex;
		this.returnValue = 1000;
		this.hitNum = 30;
	}

	public RankedJoin(ArrayList<String> urlIndexPaths, String task,
			int returnValue, int hitNum) {
		this.urlIndexPaths = urlIndexPaths;
		this.task = task;
		this.urlIndexFile = null;
		this.returnValue = returnValue;
		this.hitNum = hitNum;
	}

	public List<RankedTweetList> getResults(List<LuceneQuery> queries,
			List<RankedTweetList> rankedTweetLists) {

		List<RankedTweetList> rankedUrlLists = new ArrayList<RankedTweetList>();

		if (urlIndexPaths == null) {
			System.out.println("Simple Search");

			// query url index
			LuceneSearch urlSearch = new LuceneSearch(
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME), urlIndexFile,
					(ArrayList<LuceneQuery>) queries, null, true);

			urlSearch.setHitsReturned(hitNum);

			try {
				rankedUrlLists = urlSearch.search();
			} catch (CorruptIndexException ex) {
				Log.exception(ex);
				return null;
			} catch (IOException ex) {
				Log.exception(ex);
				return null;
			} catch (ParseException ex) {
				Log.exception(ex);
				return null;
			}
		} else {
			System.out.println("You asked for a task");
			System.out.println("The url index size" + urlIndexPaths.size());
			System.out.println("Queries Size = " + queries.size());

			for (int i = 0; i < queries.size(); i++) {

				ArrayList<LuceneQuery> tempQuery = new ArrayList<LuceneQuery>();
				ArrayList<RankedTweetList> tempList = new ArrayList<RankedTweetList>();
				tempQuery.add(queries.get(i));

				// query url index
				LuceneSearch urlSearch = new LuceneSearch(
						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME),
						urlIndexPaths.get(i),
						(ArrayList<LuceneQuery>) tempQuery, null, true);

				urlSearch.setHitsReturned(hitNum);

				try {
					tempList = urlSearch.search(i + 1, task);
				} catch (CorruptIndexException ex) {
					Log.exception(ex);
					return null;
				} catch (IOException ex) {
					Log.exception(ex);
					return null;
				} catch (ParseException ex) {
					Log.exception(ex);
					return null;
				}

				// tempList = filter.filterList(tempList, startTime, endTime);
				rankedUrlLists.addAll(tempList);
			}
		}

		List<RankedTweetList> results = new ArrayList<RankedTweetList>();

		for (int i = 0; i < queries.size(); i++) {
			// ensure origional tweet status
			RankedTweetList joinedList = rankedJoin(rankedTweetLists.get(i),
					rankedUrlLists.get(i));

			// search by id
			List<LuceneQuery> idQueryList = new ArrayList<LuceneQuery>();
			for (int j = 0; j < joinedList.size(); j++) {
				idQueryList.add(new LuceneQuery(i + "", joinedList.getTweet(j)
						.getTweetID(), "placeholderTime", Long.MAX_VALUE));
			}

			// search by ID
			LuceneSearch searchById = new LuceneSearch(DEFAULT_TWEET_INDEX,
					"tweetID", true, 1, (ArrayList<LuceneQuery>) idQueryList);
			searchById.setHitsReturned(1);

			List<RankedTweetList> idList;
			try {
				idList = searchById.search();
			} catch (CorruptIndexException ex) {
				Log.exception(ex);
				return null;
			} catch (IOException ex) {
				Log.exception(ex);
				return null;
			} catch (ParseException ex) {
				Log.exception(ex);
				return null;
			}

			for (int j = 0; j < joinedList.getRankedList().size(); j++) {
				if (!idList.get(j).getRankedList().isEmpty()) {
					joinedList.getTweet(j).setStatus(
							idList.get(j).getTweet(0).getStatus());
				} else {
					// remove retweet
					joinedList.getRankedList().remove(j);
					idList.remove(j);
					j--;
				}
			}
			results.add(joinedList);
		}
		return results;
	}

	/**
	 * Joins two ranked lists of tweets into a single ranked list. The first
	 * list must contain the rankings for tweet content, and the second list
	 * must contain the rankings for URL content.
	 * 
	 * @param tweetList
	 *            A list of tweets from a tweet content query.
	 * @param urlList
	 *            A list of tweets from a URL content query.
	 * @return A ranked list that is the combination of the input lists.
	 */
	private RankedTweetList rankedJoin(RankedTweetList tweetList,
			RankedTweetList urlList) {
		// remove duplicates
		Set<Tweet> tweetSet = new HashSet<Tweet>();
		tweetSet.addAll(tweetList.getRankedList());
		tweetSet.addAll(urlList.getRankedList());

		// create a joined list containing all unique tweets
		List<Tweet> joinedList = new ArrayList<Tweet>(tweetSet.size());
		joinedList.addAll(tweetSet);

		// url boost
		final float urlBoost = 0f;

		// normalizes the scores before combining list
		for (int i = 1; i < tweetList.size(); i++) {
			Tweet t = tweetList.getTweet(i);
			t.setScore(t.getScore() / tweetList.getTweet(0).getScore());
		}

		tweetList.getTweet(0).setScore(1.0f);

		// combining score before joining list
		for (int i = 1; i < urlList.size(); i++) {
			Tweet t = urlList.getTweet(i);
			t.setScore(t.getScore() / urlList.getTweet(0).getScore());
		}

		urlList.getTweet(0).setScore(1.0f);

		// calculate the score of every tweet ID
		for (Tweet tweet : joinedList) {

			int tweetIndex = tweetList.getRankedList().indexOf(tweet);
			int urlIndex = urlList.getRankedList().indexOf(tweet);

			// if the tweet is in the url list it is likely very good
			if (urlIndex != -1) {
				tweet.setScore(urlList.getTweet(urlIndex).getScore() + urlBoost);
				// increase score if there are multiple ranked URLs in
				// the same tweet
				for (int index = urlIndex + 1; index < urlList.size(); index++) {
					Tweet other = urlList.getTweet(index);
					if (tweet.equals(other)) {
						tweet.setScore(tweet.getScore() + other.getScore()
								+ urlBoost);
					}
				}

				// if the tweet ID is in the reliable list

				if (tweetIndex != -1) {
					tweet.setScore(tweet.getScore()
							+ tweetList.getTweet(tweetIndex).getScore()
							+ DEFAULT_RELIABLE_WEBSITE_BONUS);
				}

				// intersection bonus for tweets with tweet and url content
				if (tweetIndex != -1)
					tweet.setScore(tweet.getScore()
							+ tweetList.getTweet(tweetIndex).getScore()
							+ intersectBonus);
			}
			// if it is only in the tweet content list it is likely less
			// informative
			else if (tweetIndex != -1) {
				tweet.setScore(tweetList.getTweet(tweetIndex).getScore()
						+ nonUrlPenalty);
			}
		}

		Collections.sort(joinedList);
		for (int i = 0; i < joinedList.size(); i++)
			joinedList.get(i).setRank(i + 1);

		// creates a new ranked list of tweets with the amount of tweets
		// requested
		return new RankedTweetList(new ArrayList<Tweet>(joinedList.subList(0,
				Math.min(joinedList.size(), hitNum))));
	}

	public ArrayList<LuceneQuery> queryPowerSet(ArrayList<LuceneQuery> queries) {
		return queries;
	}
}