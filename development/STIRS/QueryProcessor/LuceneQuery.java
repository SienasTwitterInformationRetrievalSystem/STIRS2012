package STIRS.QueryProcessor;

import java.util.Arrays;

/**
 * A class that contains a converted query in the Lucene Format, with extra
 * information from the original query.
 * 
 * @author Matthew Kemmer v1.0
 * @author Karl Appel v2.0
 * 
 * @version 6/6/11 v1.0
 * @version 6/2012 v2.0
 */
public class LuceneQuery implements Comparable<LuceneQuery> {
	private String queryNum;
	private String query;
	private String[] queryTerms;
	private String queryTime;
	private long tweetTime;
	private long boundaryTime;
	private String trecTask;
	private Long oldestTweetTime = null;
	private Long newestTweetTime = null;

	/**
	 * Creates a LuceneQuery object
	 * 
	 * @param n
	 *            The query number, within the <num> tag
	 * @param q
	 *            The query, within the <title> tag
	 * @param qt
	 *            The time the query was issued, within the <querytime> tag
	 * @param tt
	 *            The timestamp of the closeset tweet to this query, within the
	 *            <querytweettime> tag
	 * @param bt
	 *            The oldest or newest tweet time that our results can contain,
	 *            given the task.
	 * @param task
	 *            Given TREC 2012 task; adhoc or filtering
	 */
	public LuceneQuery(String n, String q, String qt, Long tt) {
		if (n.startsWith("Number")) {
			n = n.substring(8);
		}
		
		queryNum = n.trim();
		query = q.trim();
		query = query.trim();
		queryTime = qt.trim();
		tweetTime = tt;
		fillQueryTerms();
	}

	public LuceneQuery(String n, String q, String qt, Long tt, Long bt,
			String task) {
		System.out.println("In Lucene Query");
		if (n.startsWith("Number")) {
			n = n.substring(8);
		}

		queryNum = n.trim();
		query = q.trim();
		query = query.trim();
		queryTime = qt.trim();
		tweetTime = tt;
		trecTask = task;

		System.out.println(queryNum);
		System.out.println(query);
		System.out.println(queryTime);
		System.out.println(tweetTime);
		System.out.println(trecTask);

		if (task.equals("adhoc")) {
			oldestTweetTime = bt;
		} else if (task.equals("filtering")) {
			newestTweetTime = bt;
		}
		fillQueryTerms();
	}

	public void setQuery(String q) {
		query = q;
		fillQueryTerms();
	}

	/**
	 * Fills an array with each query term
	 */
	private void fillQueryTerms() {
		query = query.trim();
		queryTerms = query.split(" ");

		for (int i = 0; i < queryTerms.length; i++) {
			queryTerms[i] = queryTerms[i].trim();
			queryTerms[i] = queryTerms[i].replaceAll(",", "");
		}
	}

	/**
	 * Returns an array of each query term
	 * 
	 * @return A size n array, with each index containing one of the n query
	 *         terms
	 */
	public String[] getQueryTerms() {
		return queryTerms;
	}

	/**
	 * @return A string of the entire query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Returns the timestamp of the closest tweet
	 */
	public Long getTweetTime() {
		return tweetTime;
	}

	public String getQueryNum() {
		return queryNum.substring(2);
	}

	/**
	 * Returns the time the query was issued
	 * 
	 * @return A string of the time and date
	 */
	public String getQueryTime() {
		return queryTime;
	}

	public Long getNewestTweetTime() {
		return newestTweetTime;
	}

	public Long getOldestTweetTime() {
		return oldestTweetTime;
	}

	public String toString() {
		return "LuceneQuery [queryNum=" + queryNum + ", query=" + query
				+ ", queryTerms=" + Arrays.toString(queryTerms)
				+ ", queryTime=" + queryTime + ", tweetTime=" + tweetTime
				+ ", boundaryTime=" + boundaryTime + "]";
	}

	public int compareTo(LuceneQuery o) {
		return getTweetTime().compareTo(o.getTweetTime());
	}
}