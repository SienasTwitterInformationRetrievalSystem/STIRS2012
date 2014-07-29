package STIRS.Lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.math.BigInteger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import STIRS.QueryProcessor.LuceneQuery;

/**
 * The TwitterSearch class queries a user-specified index and returns ranked
 * results.
 * 
 * @author Carl Tompkins v1.0
 * @edited Matt Kemmer v2.0
 * @edited Karl Appel and Lauren Mathews v3.0
 * 
 * @version 6/2011 v1.0
 * @version 6/6/12 v3.0
 */
public class LuceneSearch {

	// list of lucene queries
	ArrayList<LuceneQuery> queryList;
	ArrayList<String> returnVals = null;

	// initializes logger
	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	// index location for lucene search
	final String INDEX_LOC = "";
	String index = INDEX_LOC;

	// this is the field inside our index where we want our Lucene search on
	// for example the tweet is in the "status" field and we want to use
	// that indexed field to find the relevant tweets
	String field = "status";

	// determines whether we want a NIST format(true) or if we
	// want to display the tweets as they are(false) when we
	// print them out
	boolean raw = true;

	// number of hits that are returned from our search
	int hitsReturned = 1000;

	// which query number that we have
	int queryNum;

	/**
	 * Gets number of hits we got from our search
	 * 
	 * @return hitReturned number of hits from our search
	 */
	public int getHitsReturned() {
		return hitsReturned;
	}

	/**
	 * Sets the hits returned for a search
	 * 
	 * @param hitsReturned
	 *            number of hits returned
	 */
	public void setHitsReturned(int hitsReturned) {
		this.hitsReturned = hitsReturned;
	}

	// how many times you want to repeat the search
	int repeat = 0;

	/**
	 * Constructor for LuceneSearch
	 * 
	 * @param index
	 *            Which index you want to use
	 * @param queryList
	 *            The list of queries
	 */
	public LuceneSearch(Logger logger, String index,
			ArrayList<LuceneQuery> queryList, ArrayList<String> returnVals,
			boolean raw) {
		this.index += index;
		this.logger = logger;
		this.queryList = queryList;
		this.returnVals = returnVals;
		this.raw = raw;

	}

	/**
	 * Constructor for LuceneSearch
	 * 
	 * @param index
	 *            Which index you want to use
	 * @param repeat
	 *            Whether you want to repeat the search multiple times (do the
	 *            same search twice, three times, etc.)
	 * @param queryList
	 *            The list of queries
	 */
	public LuceneSearch(String index, int repeat,
			ArrayList<LuceneQuery> queryList) {
		this.index += index;
		this.queryList = queryList;
		this.repeat = repeat;

	}

	/**
	 * Constructor for LuceneSearch
	 * 
	 * @param index
	 *            Which index you want to use
	 * @param hitsReturned
	 *            The number of hits to return
	 * @param repeat
	 *            Whether you want to repeat the search multiple times (do the
	 *            same search twice, three times, etc.)
	 * @param queryList
	 *            The list of queries
	 */
	public LuceneSearch(String index, int hitsReturned, int repeat,
			ArrayList<LuceneQuery> queryList) {
		this.index += index;
		this.hitsReturned = hitsReturned;
		this.queryList = queryList;
		this.repeat = repeat;

	}

	/**
	 * Constructor for LuceneSearch
	 * 
	 * @param index
	 *            Which index you want to use
	 * @param field
	 *            which field you want to search by (i.e. status, username,
	 *            etc.)
	 * @param raw
	 *            Whether you want to return raw Lucene output
	 * @param hitsReturned
	 *            The number of hits to return
	 * @param queryList
	 *            The list of queries
	 */
	public LuceneSearch(String index, String field, boolean raw,
			int hitsReturned, ArrayList<LuceneQuery> queryList) {
		this.index += index;
		this.field = field;
		this.raw = raw;
		this.hitsReturned = hitsReturned;
		this.queryList = queryList;

	}

	/**
	 * search() uses Lucene to find relevant tweets
	 */
	public ArrayList<RankedTweetList> search() throws CorruptIndexException,
			IOException, ParseException {
		ArrayList<RankedTweetList> results = new ArrayList<RankedTweetList>();
		IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(
				index)));
		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_31);
		QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);

		for (int i = 0; i < queryList.size(); i++) {
			String query = queryList.get(i).getQuery();
			query = query.trim();

			if (query.length() == 0) {
				break;
			}

			Query luceneQuery = null;

			if (field.equals("tweetID")) {
				luceneQuery = new TermQuery(new Term(field, query));
			} else {
				luceneQuery = parser.parse(query);
			}

			ArrayList<Tweet> tweets = doPagingSearch(i + 1, searcher,
					luceneQuery, hitsReturned, raw, false);
			RankedTweetList rtl = new RankedTweetList(tweets);
			results.add(rtl);
		}

		searcher.close();
		return results;
	}

	/**
	 * Does a search given a task and a queryNum
	 * 
	 * 
	 * @param queryNum
	 *            The current overall query you want this to be (most likely
	 *            used if you do muliple Lucene Searches)
	 * @param task
	 *            the current system task that is required
	 * @return the ArrayList of ranked tweet list
	 */
	public ArrayList<RankedTweetList> search(int queryNum, String task)
			throws CorruptIndexException, IOException, ParseException {
		if (queryList.size() > 1) {
			logger.warning("Search only meant for 1 query but "
					+ "there are multiple queries in this search");

		}

		ArrayList<RankedTweetList> results = new ArrayList<RankedTweetList>();
		this.queryNum = queryNum;
		IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(
				index)));
		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_31);
		QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);

		for (int i = 0; i < queryList.size(); i++) {
			String query = queryList.get(i).getQuery();
			query = query.trim();

			if (query.length() == 0) {
				break;
			}

			Query luceneQuery = null;
			if (field.equals("tweetID")) {
				luceneQuery = new TermQuery(new Term(field, query));
			} else{
				luceneQuery = parser.parse(query);
			}

			ArrayList<Tweet> tweets = doPagingSearch(i + 1, searcher,
					luceneQuery, hitsReturned, raw, false, task);
			RankedTweetList rtl = new RankedTweetList(tweets);
			results.add(rtl);
		}
		
		searcher.close();
		return results;
	}

	/**
	 * This demonstrates a typical paging search scenario, where the search
	 * engine presents pages of size n to the user. The user can then go to the
	 * next page if interested in the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results
	 * are collected to fill 5 result pages. If the user wants to page beyond
	 * this limit, then the query is executed another time and all hits are
	 * collected.
	 * 
	 * 
	 * @param topicNum
	 *            topic number
	 * @param searcher
	 *            Lucene searcher object
	 * @param query
	 *            current query
	 * @param hitsReturned
	 *            number of hits we want to return
	 * @param raw
	 *            the format we have
	 * @param interactive
	 *            we don't need to worry about this
	 * @return arraylist of tweets which are goind to be ranked
	 */
	public ArrayList<Tweet> doPagingSearch(int topicNum,
			IndexSearcher searcher, Query query, int hitsReturned, boolean raw,
			boolean interactive) throws IOException {

		ArrayList<Tweet> tempList = new ArrayList<Tweet>();

		// Gets the results for our topic
		TopDocs results = searcher.search(query, hitsReturned);
		ScoreDoc[] hits = results.scoreDocs;

		// for the hits we get we make a new tweet object out of them and get
		// their score
		hits = results.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document doc = searcher.doc(hits[i].doc);
			
			if (raw) {
				tempList.add(new Tweet(doc.get("tweetID"), doc.get("status"),
						doc.get("username"), doc.get("date"), "MB0" + topicNum,
						hits[i].doc, i + 1, hits[i].score));
			} else {
				tempList.add(new Tweet(doc.get("tweetID"), doc.get("status"),
						doc.get("username"), doc.get("date"),
						"MB00" + topicNum, hits[i].doc, i + 1, hits[i].score));
			}

		}
		
		return tempList;
	}

	/**
	 * Same as other doPagingSearch method however this time it is with a trec
	 * task in mind
	 */

	public ArrayList<Tweet> doPagingSearch(int topicNum,
			IndexSearcher searcher, Query query, int hitsReturned, boolean raw,
			boolean interactive, String task) throws IOException {

		ArrayList<Tweet> tempList = new ArrayList<Tweet>();

		TopDocs results = searcher.search(query, hitsReturned);
		ScoreDoc[] hits = results.scoreDocs;

		hits = results.scoreDocs;

		// gets the hits for the adhoc task
		if (task.equals("adhoc")) {

			for (int i = 0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);

				// depending on the format we have it work
				if (raw) {
					tempList.add(new Tweet(doc.get("tweetID"), doc
							.get("status"), doc.get("username"), doc
							.get("date"), "MB0" + queryNum, hits[i].doc, i + 1,
							hits[i].score));
				} else {
					tempList.add(new Tweet(doc.get("tweetID"), doc
							.get("status"), doc.get("username"), doc
							.get("date"), "MB00" + queryNum, hits[i].doc,
							i + 1, hits[i].score));
				}

			}

		}

		// for the filtering task
		if (task.equals("filtering")) {
			boolean taskPerformed = true;
			
			for (int i = 0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);
				LuceneQuery lucQuery = queryList.get(topicNum - 1);
				String date = doc.get("tweetID");
				
				if ((new BigInteger(date)).compareTo(new BigInteger(lucQuery
						.getNewestTweetTime().toString())) <= 0
						&& (new BigInteger(date)).compareTo(new BigInteger(
								lucQuery.getTweetTime().toString())) >= 0
						&& !doc.get("status").trim().toLowerCase()
								.startsWith("rt")) {
					if (raw) {
						tempList.add(new Tweet(doc.get("tweetID"), doc
								.get("status"), doc.get("username"), doc
								.get("date"), "MB0" + queryNum, hits[i].doc,
								i + 1, hits[i].score));
					} else {
						tempList.add(new Tweet(doc.get("tweetID"), doc
								.get("status"), doc.get("username"), doc
								.get("date"), "MB00" + queryNum, hits[i].doc,
								i + 1, hits[i].score));
					}
				}else if ((new BigInteger(date)).compareTo(new BigInteger(
						lucQuery.getNewestTweetTime().toString())) <= 0
						&& (new BigInteger(date)).compareTo(new BigInteger(
								lucQuery.getTweetTime().toString())) >= 0
						&& doc.get("status").trim().toLowerCase()
								.startsWith("rt")) {

				}else {
					taskPerformed = false;
				}
			}
			
			System.out.println("Tasked Performed Correctly = " + taskPerformed);
		}
		
		return tempList;
	}
}