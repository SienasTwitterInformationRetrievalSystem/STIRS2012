package stirsx.tm1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

import STIRS.Lucene.LuceneSearch;
import STIRS.Lucene.RankedTweetList;
import STIRS.Lucene.Tweet;
import STIRS.QueryProcessor.LuceneQuery;

import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * This is Twitter Module 1 which will determine the relevance of a tweet by
 * determining if the content of the associated urls within a tweet are relevant
 * to the queries it takes in
 * 
 * @author Karl Appel v1.0
 * @date 7/2011 v1.0
 */
public class TM1URLOnly implements Module {

	// FUTURE IDEA WE CAN USE
	// private static SlangDictionary slangWord = new SlangDictionary();

	// private static int tweetNum = 1;
	static WordNetDatabase database;

	/**
	 * For now this class executes the module using below methods by calling a
	 * LuceneQuery object of queries which can then be used to create a
	 * LuceneSearch object. This can then be used to create a ranked tweet list
	 * and each individual tweet in that list can be modified for the better.
	 * 
	 * @param queries
	 *            , index path , (test #) any String
	 */
	public static void main(String[] args) throws CorruptIndexException,
			IOException, Exception {

		PrintWriter results = new PrintWriter(
				new File(
						"C:\\Users\\Karl\\Documents\\Twitter\\results\\baselineHomemadeTopics\\thirdTest\\"
								+ args[2]
								+ "results"
								+ args[4]
								+ " "
								+ args[0]
								+ ".txt"));
		String DEFAULT_TWEET_INDEX = "C:\\Users\\Karl\\Documents\\Twitter\\englishCorpusIndex";
		PrintWriter resultsTime = new PrintWriter(
				new File(
						"C:\\Users\\Karl\\Documents\\Twitter\\results\\baselineHomemadeTopics\\thirdTest\\"
								+ args[2]
								+ "TimeResults"
								+ args[4]
								+ " "
								+ args[0] + ".txt"));

		// arraylist of queries just in case there are more then one query you
		// want to do this for
		ArrayList<LuceneQuery> queries = new ArrayList<LuceneQuery>();

		// we don't want to return any returnValues. Also if nullList is null
		// then raw must be true
		ArrayList<String> nullList = null;

		// adds the query to the list. This can be modified for more queries
		queries.add(new LuceneQuery("1", args[0], "1", Long.parseLong(args[3])));

		// index location
		String indexPath = args[1];

		// takes in logger, index path, list of queries, return vals, raw data
		// if you want it returned
		LuceneSearch luceneSearch = new LuceneSearch(Logger.getLogger(""),
				indexPath, queries, nullList, true);
		luceneSearch.setHitsReturned(1000);

		// ranked list of tweets from query or queries you want back
		RankedTweetList rankedTweets = luceneSearch.search().get(0);

		// gets the information on a specfic tweet in this case the first tweet
		System.out.println(rankedTweets.size());

		for (int count = 0; count < rankedTweets.size(); count++) {
			// get info on particular tweet
			Tweet tweetInfo = rankedTweets.getTweet(count);

			// get information on that tweet
			float score = tweetInfo.getScore();
			String status = tweetInfo.getStatus();

			// bases the score on wordcount
			float newScore = wordCountScore(status, score);

			// set to the new score
			tweetInfo.setScore(newScore);
		}

		// sorts the list out in order of score from greatest to least
		Collections.sort(rankedTweets.getRankedList());

		List<LuceneQuery> idQueryList = new ArrayList<LuceneQuery>();
		for (int i = 0; i < rankedTweets.size(); i++) {
			idQueryList.add(new LuceneQuery(i + "", rankedTweets.getTweet(i)
					.getTweetID(), "placeholderTime", Long.MAX_VALUE));
		}

		// does a search by ID with the other corpus so that we can get the
		// actual tweet
		// of the urlcontent we are processing and not just the information from
		// the url page

		LuceneSearch searchById = new LuceneSearch(DEFAULT_TWEET_INDEX,
				"tweetID", true, 1, (ArrayList<LuceneQuery>) idQueryList);

		List<RankedTweetList> idList = searchById.search();

		// sets the rank of each tweet
		for (int i = 0; i < rankedTweets.size(); i++) {
			rankedTweets.getTweet(i).setRank(i + 1);
		}

		int count = 0;
		int emptyTweets = 0;
		int tweetNum = 0;

		// gets 30 top tweets however because retweets are not filtered in the
		// URLContent Corpus
		// we have to skip over RTs that may be found in URL but not in RT
		// filtered corpus

		while (count < (30 + emptyTweets)) {
			System.out.println("Processing Outer whileLoop");
			results.println(tweetNum + 1 + ". "
					+ rankedTweets.getTweet(count).getTweetID() + " "
					+ rankedTweets.getTweet(count).getScore());
			System.out.println(rankedTweets.getTweet(count).getTweetID());
			Tweet theTweet = null;

			if (idList.get(count).size() != 0) {

				theTweet = idList.get(count).getTweet(0);
				results.println(theTweet);
				results.println(rankedTweets.getTweet(count).getDate());
				resultsTime.println(tweetNum + 1 + ". "
						+ rankedTweets.getTweet(count).getTweetID() + " "
						+ rankedTweets.getTweet(count).getScore());
				resultsTime.println(theTweet);
				resultsTime.println(rankedTweets.getTweet(count).getDate());

				resultsTime.flush();
				results.flush();

				tweetNum++;
			} else {
				emptyTweets++;
			}
			count++;
		}
		resultsTime.close();
		results.close();
	}

	/**
	 * If a website uses add language then the weighting of this site will be
	 * less than others with more formal and correct writing
	 */
	public static float texteseFilter(String TweetID, String status, float score) {

		StringTokenizer tokenizer = new StringTokenizer(status);

		@SuppressWarnings("unused")
		String checkWord = tokenizer.nextToken();

		while (tokenizer.hasMoreTokens()) {
			checkWord = tokenizer.nextToken();
		}
		return score;
	}

	/**
	 * breaks the query or any string into individual words
	 * 
	 * @return arraylist of individual words in the query term or input string
	 */
	public static ArrayList<String> queryTokenizer(String query) {
		StringTokenizer tokenizer = new StringTokenizer(query);
		ArrayList<String> queryWords = new ArrayList<String>();

		String queryWord;
		while (tokenizer.hasMoreTokens()) {
			queryWord = tokenizer.nextToken();
			queryWord = queryWord.toLowerCase();
			queryWords.add(queryWord);

		}
		return queryWords;
	}

	public List<RankedTweetList> getResults(List<LuceneQuery> queriesList,
			List<RankedTweetList> rankedTweetLists) {

		String optionFile = "/compLing/home/kappel/option/optionFile.txt";

		try {
			BufferedReader bPos = new BufferedReader(new InputStreamReader(
					new FileInputStream(optionFile)));
			String experiment = bPos.readLine();

			if (experiment.equals("1")) {
				rankedTweetLists = getResultsOriginal(queriesList,
						rankedTweetLists);
			} else if (experiment.equals("2")) {
				rankedTweetLists = getResultsExperiment2(queriesList,
						rankedTweetLists);
			} else if (experiment.equals("3")) {
				rankedTweetLists = getResultsExperiment3(queriesList,
						rankedTweetLists);
			}

			bPos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rankedTweetLists;
	}

	public List<RankedTweetList> getResultsExperiment3(
			List<LuceneQuery> queriesList, List<RankedTweetList> rankedTweetList)
			throws IOException {

		String DEFAULT_TWEET_INDEX = "/compLing/index/newEnglishCorpusIndex";
		String EXCEPTION_FILE = "/compLing/home/kappel/experiments/experiment3/punctuationList.txt";
		List<RankedTweetList> newRankedTweetList = new ArrayList<RankedTweetList>();

		// holds the bad punctuation list
		ArrayList<String> exceptionList = new ArrayList<String>();

		BufferedReader exceptionReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(EXCEPTION_FILE)));

		// puts words into exception list
		String currentLine = exceptionReader.readLine();

		while (currentLine != null) {
			exceptionList.add(currentLine);
			currentLine = exceptionReader.readLine();
		}

		exceptionReader.close();

		// arraylist of queries just in case there are more then one query you
		// want to do this for
		ArrayList<LuceneQuery> queries = new ArrayList<LuceneQuery>();
		queries.addAll(queriesList);

		// we don't want to return any returnValues * if nullList is null then
		// raw must be true
		ArrayList<String> nullList = null;

		// takes in logger, index path, list of queries, return vals, raw data
		// if you want it returned
		LuceneSearch luceneSearch = new LuceneSearch(Logger.getLogger(""),
				DEFAULT_TWEET_INDEX, queries, nullList, true);

		// TAKE OUT FOR EXPERIMENT1
		luceneSearch.setHitsReturned(1000);

		List<RankedTweetList> rankedTweetsList = null;
		try {
			rankedTweetsList = luceneSearch.search();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ranked list of tweets from query or queries you want back
		for (int currentQuery = 0; currentQuery < queries.size(); currentQuery++) {
			RankedTweetList rankedTweets = rankedTweetsList.get(currentQuery);

			for (int count = 0; count < rankedTweets.size(); count++) {
				// get info on particular tweet
				Tweet tweetInfo = rankedTweets.getTweet(count);

				// get information on that tweet
				float score = tweetInfo.getScore();
				String status = tweetInfo.getStatus();

				score = Punctuation(score, status, exceptionList);

				// set to the new score
				tweetInfo.setScore(score);
			}

			rankedTweets = new RankedTweetList(new ArrayList<Tweet>(
					((new HashSet<Tweet>(rankedTweets.getRankedList())))));

			Collections.sort(rankedTweets.getRankedList());

			for (int i = 0; i < rankedTweets.getRankedList().size(); i++) {
				rankedTweets.getTweet(i).setRank(i + 1);
			}

			newRankedTweetList.add(rankedTweets);
		}

		return newRankedTweetList;
	}

	public List<RankedTweetList> getResultsExperiment2(
			List<LuceneQuery> queriesList,
			List<RankedTweetList> rankedTweetLists) throws IOException {

		// whether we want to run the Reliability or Traffic Filter
		String option = null;
		String optionDirectory = "/compLing/home/kappel/option/optionFile.txt";

		BufferedReader optionReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(optionDirectory)));
		optionReader.readLine();
		option = optionReader.readLine();

		String DEFAULT_TWEET_INDEX = "/compLing/index/newEnglishCorpusIndex";
		String URL_INDEX_PATH = "/compLing/home/kappel/completeURLIndex";
		String EXCEPTION_LIST = null;

		if (option.equals("b")) {
			EXCEPTION_LIST = "/compLing/home/kappel/experiments/experiment2/trafficFilterExceptionList.txt";
		}

		if (option.equals("a")) {
			EXCEPTION_LIST = "/compLing/home/kappel/experiments/experiment2/reliabilityFilterExceptionList.txt";
		}

		List<RankedTweetList> newRankedTweetList = new ArrayList<RankedTweetList>();

		ArrayList<String> exceptionList = new ArrayList<String>();
		BufferedReader exceptionReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(EXCEPTION_LIST)));

		String currentException = exceptionReader.readLine();

		while (currentException != null) {
			exceptionList.add(currentException);
			currentException = exceptionReader.readLine();
		}

		// arraylist of queries just in case there are more then one query you
		// want to do this for
		ArrayList<LuceneQuery> queries = new ArrayList<LuceneQuery>();
		queries.addAll(queriesList);

		// we don't want to return any returnValues * if nullList is null then
		// raw must be true
		ArrayList<String> nullList = null;

		// takes in logger, index path, list of queries, return vals, raw data
		// if you want it returned
		LuceneSearch luceneSearch = new LuceneSearch(Logger.getLogger(""),
				URL_INDEX_PATH, queries, nullList, true);

		luceneSearch.setHitsReturned(1000);

		List<RankedTweetList> rankedTweetsList = null;
		try {
			rankedTweetsList = luceneSearch.search();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ranked list of tweets from query or queries you want back
		for (int currentQuery = 0; currentQuery < queries.size(); currentQuery++) {
			RankedTweetList rankedTweets = rankedTweetsList.get(currentQuery);
			List<Tweet> newRankedTweets = new ArrayList<Tweet>();

			for (int count = 0; count < rankedTweets.size(); count++) {
				// get info on particular tweet
				Tweet tweetInfo = rankedTweets.getTweet(count);

				// get information on that tweet
				float score = tweetInfo.getScore();
				String status = tweetInfo.getStatus();

				if (option.equals("b")) {
					score = TrafficFilter(score, status, exceptionList);
				}

				if (option.equals("a")) {
					score = ReliabilityFilter(score, status, exceptionList);
				}

				score = wordCountScore(status, score);
				// set to the new score
				tweetInfo.setScore(score);
			}

			rankedTweets = new RankedTweetList(new ArrayList<Tweet>(
					((new HashSet<Tweet>(rankedTweets.getRankedList())))));

			Collections.sort(rankedTweets.getRankedList());

			List<LuceneQuery> idQueryList = new ArrayList<LuceneQuery>();

			for (int i = 0; i < rankedTweets.size(); i++) {
				idQueryList.add(new LuceneQuery(currentQuery + "", rankedTweets
						.getTweet(i).getTweetID(), "placeholderTime",
						Long.MAX_VALUE));

			}

			LuceneSearch searchById = new LuceneSearch(DEFAULT_TWEET_INDEX,
					"tweetID", true, 1, (ArrayList<LuceneQuery>) idQueryList);

			List<RankedTweetList> idList = null;
			try {
				idList = searchById.search();
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (int i = 0; i < rankedTweets.getRankedList().size(); i++) {
				if (idList.get(i).getRankedList().size() != 0) {
					idList.get(i).getTweet(0).setRank(i + 1);

					rankedTweets.getTweet(i).setStatus(
							idList.get(i).getTweet(0).getStatus());
					newRankedTweets.add(rankedTweets.getTweet(i));
				} else {
					rankedTweets.getRankedList().remove(i);
					idList.remove(i);
					i--;
				}
			}

			for (int i = 0; i < rankedTweets.getRankedList().size(); i++) {
				rankedTweets.getTweet(i).setRank(i + 1);
			}

			// must delete for experiment 1;
			newRankedTweetList.add(new RankedTweetList(
					(ArrayList<Tweet>) newRankedTweets));
		}

		optionReader.close();
		exceptionReader.close();

		return newRankedTweetList;
	}

	public List<RankedTweetList> getResultsOriginal(
			List<LuceneQuery> queriesList,
			List<RankedTweetList> rankedTweetLists) {

		String DEFAULT_TWEET_INDEX = "/compLing/index/newEnglishCorpusIndex";
		String URL_INDEX_PATH = "/compLing/home/kappel/completeURLIndex";
		List<RankedTweetList> newRankedTweetList = new ArrayList<RankedTweetList>();

		// arraylist of queries just in case there are more then one query you
		// want to do this for
		ArrayList<LuceneQuery> queries = new ArrayList<LuceneQuery>();
		queries.addAll(queriesList);

		// we don't want to return any returnValues * if nullList is null then
		// raw must be true
		ArrayList<String> nullList = null;

		// takes in logger, index path, list of queries, return vals, raw data
		// if you want it returned
		LuceneSearch luceneSearch = new LuceneSearch(Logger.getLogger(""),
				URL_INDEX_PATH, queries, nullList, true);

		// TAKE OUT FOR EXPERIMENT1
		luceneSearch.setHitsReturned(1000);

		List<RankedTweetList> rankedTweetsList = null;
		try {
			rankedTweetsList = luceneSearch.search();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ranked list of tweets from query or queries you want back
		for (int currentQuery = 0; currentQuery < queries.size(); currentQuery++) {
			RankedTweetList rankedTweets = rankedTweetsList.get(currentQuery);
			List<Tweet> newRankedTweets = new ArrayList<Tweet>();
			// System.out.println("Ranked Tweet size = " + rankedTweets.size());
			// gets the information on a specfic tweet in this case the first
			// tweet

			String[] tokenizedQueryArray;

			tokenizedQueryArray = queries.get(currentQuery).getQueryTerms();
			ArrayList<String> tokenizedQueryArrayList = new ArrayList<String>();
			String tempString;

			for (int queryTerm = 0; queryTerm < tokenizedQueryArray.length; queryTerm++) {
				tempString = tokenizedQueryArray[queryTerm];
				tokenizedQueryArrayList.add(tempString);
			}

			for (int count = 0; count < rankedTweets.size(); count++) {
				// get info on particular tweet
				Tweet tweetInfo = rankedTweets.getTweet(count);

				// get information on that tweet
				float score = tweetInfo.getScore();
				String status = tweetInfo.getStatus();

				// all query terms included in the content of urls
				float newScore = wordCountScore(status, score);

				// set to the new score
				tweetInfo.setScore(newScore);

			}

			rankedTweets = new RankedTweetList(new ArrayList<Tweet>(
					((new HashSet<Tweet>(rankedTweets.getRankedList())))));

			Collections.sort(rankedTweets.getRankedList());

			List<LuceneQuery> idQueryList = new ArrayList<LuceneQuery>();

			for (int i = 0; i < rankedTweets.size(); i++) {
				idQueryList.add(new LuceneQuery(currentQuery + "", rankedTweets
						.getTweet(i).getTweetID(), "placeholderTime",
						Long.MAX_VALUE));

			}

			LuceneSearch searchById = new LuceneSearch(DEFAULT_TWEET_INDEX,
					"tweetID", true, 1, (ArrayList<LuceneQuery>) idQueryList);

			List<RankedTweetList> idList = null;
			try {
				idList = searchById.search();
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (int i = 0; i < rankedTweets.getRankedList().size(); i++) {
				if (idList.get(i).getRankedList().size() != 0) {
					idList.get(i).getTweet(0).setRank(i + 1);

					rankedTweets.getTweet(i).setStatus(
							idList.get(i).getTweet(0).getStatus());
					newRankedTweets.add(rankedTweets.getTweet(i));
				} else {
					rankedTweets.getRankedList().remove(i);
					idList.remove(i);
					i--;
				}
			}

			for (int i = 0; i < rankedTweets.getRankedList().size(); i++) {
				rankedTweets.getTweet(i).setRank(i + 1);
			}

			newRankedTweetList.add(new RankedTweetList(
					(ArrayList<Tweet>) newRankedTweets));
		}

		return newRankedTweetList;
	}

	/**
	 * Counts the amount of words in a string
	 */
	public static int getWordCount(String status) {
		StringTokenizer statusTokenizer = new StringTokenizer(status);
		int numWords = statusTokenizer.countTokens();

		return numWords;
	}

	/**
	 * Counts the amount of words in the status, aka url page, and returns the
	 * newScore
	 */
	public static float wordCountScore(String status, float score) {
		int wordCount;

		// anything below this word count gets a penalty
		int PENALTY_NUM = 50;

		// anything above this gets a bonus
		int BONUS_NUM = 600;

		// gets the amount of words in the status
		wordCount = getWordCount(status);

		if (wordCount < PENALTY_NUM) {
			score = (float) (score * .3);
		} else if (wordCount > BONUS_NUM) {
			score = (float) (score * 1.5);
		}

		// new score returned
		return score;
	}

	public List<Tweet> experiment1(List<Tweet> rankedTweets,
			ArrayList<LuceneQuery> query) throws CorruptIndexException,
			IOException, ParseException {

		String corpusPath = "C:\\Users\\Karl\\Documents\\Twitter\\results\\experiment1Corpus.txt";
		String indexPath = "C:\\Users\\Karl\\Documents\\Twitter\\results";
		PrintWriter tempTweetFile = new PrintWriter(new File(corpusPath));

		for (int i = 0; i < rankedTweets.size(); i++) {
			tempTweetFile.println(rankedTweets.get(i));
			tempTweetFile.flush();
		}

		tempTweetFile.close();

		String[] args = new String[4];
		args[0] = "-index";
		args[1] = indexPath;
		args[2] = "-docs";
		args[3] = corpusPath;

		STIRS.Lucene.TwitterIndexer.main(args);
		LuceneSearch luceneSearch = new LuceneSearch(Logger.getLogger(""),
				indexPath, query, null, true);
		luceneSearch.setHitsReturned(1000);
		RankedTweetList rankedTweetList = luceneSearch.search().get(0);
		rankedTweets.clear();

		for (int i = 0; i < rankedTweetList.getRankedList().size(); i++) {
			rankedTweets.add(rankedTweetList.getTweet(i));
		}

		return rankedTweets;
	}

	public float Punctuation(float score, String tweet,
			ArrayList<String> exceptionList) {

		for (int i = 0; i < exceptionList.size(); i++) {
			String currentException = exceptionList.get(i);

			if (tweet.contains(currentException)) {
				score = (float) (score * 0);
			}
		}

		return score;
	}

	public float TrafficFilter(float score, String url,
			ArrayList<String> exceptionList) {

		// goes through each exception and if url contains it then score is
		// increased
		for (int i = 0; i < exceptionList.size(); i++) {
			String tempException = exceptionList.get(i);

			if (url.contains(tempException)) {
				// if exception found we change the score
				score = (float) (score * 1.5);
			}
		}

		return score;
	}

	public float ReliabilityFilter(float score, String url,
			ArrayList<String> exceptionList) {

		// goes through each exception and if url contains it then score is
		// increased
		for (int i = 0; i < exceptionList.size(); i++) {
			String tempException = exceptionList.get(i);

			if (url.contains(tempException)) {
				// if exception found we change the score
				score = (float) (score * 1.1);
			}
		}
		return score;
	}
}