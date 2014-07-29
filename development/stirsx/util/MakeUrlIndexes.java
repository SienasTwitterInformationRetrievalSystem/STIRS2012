package stirsx.util;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import development.Stirs;
import STIRS.Lucene.MassIndexer;
import STIRS.QueryProcessor.LuceneQuery;
import STIRS.QueryProcessor.QueryProcessor;

/**
 * Quick fix to make url indexes for the topic range specified below on the
 * server
 * 
 * @author Karl Appel v1.0
 * @version 6/2012 v1.0
 */
public class MakeUrlIndexes {
	
	//args[0] startRange
	//args[1] endRange
	public static void main(String args[]) throws Exception {
		String queryFile = "/home/gituser/trec2012/trec2012TopicsFormatted.txt";
		String corpusPath = "/home/gituser/corpus/allUrlContentSortedAfterFilter.txt";
		String indexesPath = "/home/gituser/urlIndexes";

		String task = "adhoc";
		int startRange = Integer.parseInt(args[0]);
		int endRange = Integer.parseInt(args[1]);
		String tempFile = "/home/gituser/tempFiles/" + startRange + "TO"
				+ endRange;

		ArrayList<LuceneQuery> queries = new ArrayList<LuceneQuery>();
		QueryProcessor processor = new QueryProcessor(new File(queryFile),
				Logger.getLogger(Stirs.class.getName()), task);

		queries = processor.getSanitizedQueries();

		ArrayList<LuceneQuery> queriesWanted = new ArrayList<LuceneQuery>();

		for (int i = 0; i < queries.size(); i++) {
			if (i >= (startRange - 1) && i <= (endRange - 1)) {
				System.out.println("Doing " + (i + 1));
				queriesWanted.add(queries.get(i));
			}
		}
		
		MassIndexer indexer = new MassIndexer(corpusPath, indexesPath, queries,
				task, true, tempFile);
		indexer.getIndexesPaths();
	}
}