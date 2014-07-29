package stirsx.tm1;

import STIRS.Lucene.RankedTweetList;
import STIRS.QueryProcessor.LuceneQuery;
import java.util.List;

/**
 * The Module interface defines the necessary behavior of modules that modify
 * tweet rankings.
 * 
 * @author David Purcell v1.0
 * @version 6/2011 v1.0
 */
public interface Module {

	/**
	 * <p>
	 * Returns the results of a Module after processing the given queries and
	 * baseline results (or Module modified).
	 * <p>
	 * This method should only return <code>null</code> if a critical exception
	 * occurs.
	 * 
	 * @param queries
	 *            A list of queries.
	 * @param rankedTweetLists
	 *            A list of baseline or Module modified results for the queries.
	 * @return An adjusted list of tweet rankings for the queries.
	 */
	public abstract List<RankedTweetList> getResults(List<LuceneQuery> queries,
			List<RankedTweetList> rankedTweetLists);
}
