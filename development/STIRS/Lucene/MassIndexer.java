package STIRS.Lucene;

import java.io.*;
import java.util.*;

import stirsx.tm1.TwitterIndexerUrls;
import STIRS.QueryProcessor.LuceneQuery;

/**
 * Sees whether an index (location) exists between two given times based on the
 * task for all queries. If a query doesn't have an index already for it, we
 * create one for it (first time query is called). We then return the list of
 * files where the index is located.
 * 
 * @author Lauren Mathews and Karl Appel v1.0
 * @version 6/7/12 v1.0
 */

public class MassIndexer {

	private String corpusPath, indexesPath, task;
	private ArrayList<LuceneQuery> queries;
	String docsPath = "/home/gituser/corpus/temp.txt";
	boolean url = false;
	private static final String SEQUENCE_IDENTIFIER = "^)*&!P!@#@^^^((5hg%jsJ";

	/**
	 * Constructor for the MassIndexer.
	 * 
	 * @param corpusPath
	 *            The path of the ordered corpus.
	 * @param indexesPath
	 *            The directory with all the indexes.
	 * @param queries
	 *            The list of queries taken in.
	 * @param task
	 *            Given task for either adhoc or filtering.
	 */
	public MassIndexer(String corpusPath, String indexesPath,
			ArrayList<LuceneQuery> queries, String task) {
		this.corpusPath = corpusPath;
		this.indexesPath = indexesPath;
		this.task = task;
		this.queries = queries;
	}

	/**
	 * 
	 * Another constructor for the mass indexer
	 * 
	 * @param corpusPath
	 *            Path of the corpus we want to use
	 * @param indexesPath
	 *            Path of the folder with indexes we want to use
	 * @param queries
	 *            ArrayList of Lucene Query
	 * @param task
	 *            The current task given for stirs
	 * @param url
	 *            Whether this is going to index url or corpus information
	 * 
	 */
	public MassIndexer(String corpusPath, String indexesPath,
			ArrayList<LuceneQuery> queries, String task, boolean url) {
		this.corpusPath = corpusPath;
		this.indexesPath = indexesPath;
		this.task = task;
		this.queries = queries;
		this.url = url;
	}

	/**
	 * 
	 * Another constructor for the mass indexer
	 * 
	 * @param corpusPath
	 *            Path of the corpus we want to use
	 * @param indexesPath
	 *            Path of the folder with indexes we want to use
	 * @param queries
	 *            ArrayList of Lucene Query
	 * @param task
	 *            The current task given for stirs
	 * @param url
	 *            Whether this is going to index url or corpus information
	 * @param docsPath
	 *            Path where the temporary file will be created for indexing
	 * 
	 * 
	 */
	public MassIndexer(String corpusPath, String indexesPath,
			ArrayList<LuceneQuery> queries, String task, boolean url,
			String docsPath) {
		this.corpusPath = corpusPath;
		this.indexesPath = indexesPath;
		this.task = task;
		this.queries = queries;
		this.url = url;
		this.docsPath = docsPath;
	}

	/**
	 * Checks to see if the file (index locations) exists.
	 * 
	 * @param indexFile
	 *            The index we want to check to see if it exists.
	 * @return Returns boolean if it exists or not.
	 */
	private boolean verifyIndexExistence(File indexFile) {
		if (indexFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the ArrayList of the File Index Locations; if one doesn't exist for
	 * a query (first time only), one is created.
	 * 
	 * @return ArrayList of Files with index path.
	 */
	public ArrayList<String> getIndexesPaths() throws IOException {

		// list of the file location for all the indexes
		ArrayList<String> queryIndexFiles = new ArrayList<String>();
		ArrayList<Long> adhocTweetIDs = new ArrayList<Long>();

		// the code below with the if statements could have been put together
		// but there is enough of a change where I seperated them into two
		// statements

		// creates indexes for a realtime search without a task or the adhoc
		// task
		// a realtime search for the adhoc task has a specific oldesttweet while
		// one without a task will assume we want all tweets in our corpus
		// up to the querytweettime we specified in our query file
		// this statement will get task between a certain oldesttime
		// the querytweettime

		if (task == null || task.equals("adhoc")) {
			String docsPath = "/home/gituser/corpus/temp.txt";
			// goes through for each query
			for (int i = 0; i < queries.size(); i++) {

				// gets the query
				LuceneQuery temp = queries.get(i);

				// initializes oldestTweetTime to zero
				String oldestTweetTime = "0";

				// gets the oldesttweettime for a query if one exist
				if (task != null && task.equals("adhoc")) {
					oldestTweetTime = Long.toString(temp.getOldestTweetTime());
				}

				// gets the current tweet time
				String currentTweetTime = Long.toString(temp.getTweetTime());

				// name of the file we want
				String indexOldestToCurrent = oldestTweetTime + "TO"
						+ currentTweetTime;

				// path of the file we want
				String indexPath = indexesPath + File.separator
						+ indexOldestToCurrent;

				// converts the name of the file we want into an actual file
				File indexFile = new File(indexPath);

				// verifies that this file exist
				boolean doesIndexExist = verifyIndexExistence(indexFile);
				System.out.println("Verifying index for current query number "
						+ (i + 1));

				// if it exist we add its name to the list of file names we want
				if (doesIndexExist || queryIndexFiles.contains(indexPath)) {
					System.out.println("Index exist for current query");
					queryIndexFiles.add(indexPath);

					// otherwise we have to create it
				} else {
					// path where we will make a temporary corpus consisting of
					// tweets
					// between the two specified time we want

					System.out
							.println("Index does not exist for current query");
					System.out
							.println("Index will now be created for current query");

					// create index for url stuff
					if (url) {
						// the adhoc task are going to be indexed in one read
						// which will be done after all the files are read in

						if (task.equals("adhoc")) {
							adhocTweetIDs.add(temp.getTweetTime());
						}

						// the filtering task or any other task will make a temp
						// file and index one step at a time
						else {
							createNewUrlIndex(
									indexPath,
									docsPath,
									temp.getOldestTweetTime() != null ? temp
											.getOldestTweetTime() : new Long(0),
									temp.getTweetTime());
						}
						// does the same as above but for the tweet index
					} else if (!url) {
						if (task.equals("adhoc")) {
							adhocTweetIDs.add(temp.getTweetTime());
						} else {
							createNewIndex(
									indexPath,
									docsPath,
									temp.getOldestTweetTime() != null ? temp
											.getOldestTweetTime() : new Long(0),
									temp.getTweetTime());
						}
					}

					// once this is done we add it to our arraylist of paths
					queryIndexFiles.add(indexPath);
				}
			}
			
			// non url index is created for adhoc
			if (task.equals("adhoc") && !url) {
				if (adhocTweetIDs.size() != 0) {
					try {
						System.out.println("Creating...");
						createNewIndexAdhoc(docsPath, adhocTweetIDs);
					} catch (Exception e) {
						System.err.println(e);

					}
				}
			}
			
			// url index is created for adhoc
			if (task.equals("adhoc") && url) {
				if (adhocTweetIDs.size() != 0) {
					try {
						System.out.println("Creating...");
						createNewUrlIndexAdhoc(docsPath, adhocTweetIDs);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// gets the index of tweets between the querytweettime and the
			// newesttweettime
		} else if (task.equals("filtering")) {

			for (int i = 0; i < queries.size(); i++) {
				// current query
				LuceneQuery temp = queries.get(i);

				// newest tweet time as specified by the topic
				String newestTweetTime = Long.toString(temp
						.getNewestTweetTime());

				// current querytweettime
				String currentTweetTime = Long.toString(temp.getTweetTime());

				// name of index we want to find
				String indexNewestToCurrent = currentTweetTime + "TO"
						+ newestTweetTime;

				// name of index file
				String indexPath = indexesPath + File.separator
						+ indexNewestToCurrent;

				// creates new file out of index name
				File indexFile = new File(indexPath);

				// checks to see if index exist
				boolean doesIndexExist = verifyIndexExistence(indexFile);
				System.out.println("Verifying index for current query number "
						+ (i + 1));
				// if it exist we add it to our arraylist of path names
				if (doesIndexExist) {

					queryIndexFiles.add(indexPath);

					// otherwise we create an index for tweets between those
					// times
					// and then we add it to arraylist of file names
				} else {

					// path where we will make a temporary corpus consisting of
					// tweets
					// between the two specified time we want
					String docsPath = "/home/gituser/corpus/temp.txt";

					System.out
							.println("Index does not exist for current query");
					System.out
							.println("Index will now be created for current query");

					// creates the new index for the query
					if (url) {
						createNewUrlIndex(indexPath, docsPath,
								temp.getTweetTime(), temp.getNewestTweetTime());
					} else {
						createNewIndex(indexPath, docsPath,
								temp.getTweetTime(), temp.getNewestTweetTime());
					}
					// adds it to the appropriate path
					queryIndexFiles.add(indexPath);
				}
			}
		}
		// once the indexing for the files is done the names of the actual files
		// is then returned
		return queryIndexFiles;
	}

	/**
	 * Writes the url indexes with only one read from text file This saves us
	 * some much needed time for new indexes
	 * 
	 * @param docsPath
	 *            : temporary location where we will have our path created
	 * @param adhocTweetIDs
	 *            the arraylist of adhoc tweet ids
	 * @throws Exception
	 *             input and output
	 */
	private void createNewUrlIndexAdhoc(String docsPath,
			ArrayList<Long> adhocTweetIDs) throws Exception {

		// sets up temporary file
		FileWriter fw = new FileWriter(docsPath);
		BufferedWriter bfw = new BufferedWriter(fw);

		// reads in appropriate corpus
		FileReader fr = new FileReader(corpusPath);
		BufferedReader bfr = new BufferedReader(fr);

		// sorts ids by time
		Collections.sort(adhocTweetIDs);

		// gets the first time
		Long to = adhocTweetIDs.get(0);

		String inputLine = bfr.readLine();

		// starts with tweet id as 0
		Long tweetID = new Long(0);

		while (inputLine != null) {
			tweetID = Long.parseLong(inputLine);
			if (tweetID.compareTo(to) <= 0) {

				// writes the new information to a temp file that we need
				while (inputLine != null
						&& !inputLine.equals(SEQUENCE_IDENTIFIER)) {
					bfw.write(inputLine);
					bfw.newLine();
					bfw.flush();
					inputLine = bfr.readLine();
				}

				// there was a problem with our data that the sequence
				// identifier
				// is repeated multiple times after a line and this is a fix for
				// that
				while (inputLine != null
						&& inputLine.equals(SEQUENCE_IDENTIFIER)) {
					inputLine = bfr.readLine();

				}
				// writes the identifier once
				bfw.write(SEQUENCE_IDENTIFIER);
				bfw.newLine();
				bfw.flush();

				// if the tweet id is greater than our time than we write an
				// index from
				// the start to the current id we are on
			} else {
				System.out.println(indexesPath + File.separator + "0TO" + to);
				new TwitterIndexerUrls(indexesPath + File.separator + "0TO"
						+ to, true, docsPath);

				// after indexing we remove it and contiune on
				adhocTweetIDs.remove(0);
				if (adhocTweetIDs.size() == 0) {
					break;
				}
				to = adhocTweetIDs.get(0);
			}

			// makes sure that if tweetID exist that is greater than our
			// greatest file than we have to write indexes out with all the
			// data that we have
			if (inputLine == null && adhocTweetIDs.size() > 0) {

				while (!(adhocTweetIDs.size() == 0)) {
					System.out.println(indexesPath + File.separator + "0TO"
							+ to);
					new TwitterIndexerUrls(indexesPath + File.separator + "0TO"
							+ to, true, docsPath);
					adhocTweetIDs.remove(0);
					to = adhocTweetIDs.get(0);
				}
			}
		}

		bfw.close();
		bfr.close();
	}

	/**
	 * Writes the indexes from the start of our list to specifiy tweet id times
	 * 
	 * @param docsPath
	 *            temporary path where text file will be written
	 * @param adhocTweetIDs
	 *            list of tweet ids that are sorted
	 */
	private void createNewIndexAdhoc(String docsPath,
			ArrayList<Long> adhocTweetIDs) throws Exception {
		
		// creates a fileReader out of corpus path
		FileReader fr = new FileReader(corpusPath);
		BufferedReader bfr = new BufferedReader(fr);
		Collections.sort(adhocTweetIDs);
		Long to = adhocTweetIDs.get(0);
		
		// first line
		String inputLine = bfr.readLine();

		// initializes the lowest tweetID to 0
		Long tweetID = (long) 0;

		// contiunes to print the tweets to a file until we go greater than the
		// current
		// tweetID we want to end on
		FileWriter fw = new FileWriter(docsPath);
		BufferedWriter bfw = new BufferedWriter(fw);
		
		while (inputLine != null) {
			tweetID = Long.parseLong(inputLine.substring(0, 17));
			if (tweetID.compareTo(to) <= 0) {
				bfw.write(inputLine);
				bfw.newLine();
				bfw.flush();
			} else {
				new TwitterIndexer("/home/gituser/indexes/0TO" + to, true,
						docsPath);
				adhocTweetIDs.remove(0);
				
				if (adhocTweetIDs.size() == 0) {
					break;
				}

				if (adhocTweetIDs.size() == 0) {
					break;
				}
				
				to = adhocTweetIDs.get(0);
			}

			inputLine = bfr.readLine();

		}
		
		// closes the reader and the writer
		bfr.close();
		bfw.close();

		// creates the new specified index that we want
	}

	/**
	 * Takes in the newest and current tweet time and create a new index
	 * location.
	 * 
	 * @param indexPath
	 *            Creates the new indexFile path.
	 * @param docsPath
	 *            a temporary location where we can create a corpus that consist
	 *            of tweets between the two specified times "from" and "to"
	 * @param from
	 *            The tweet id you want to start your index from
	 * @param to
	 *            The tweet id you want to end your index on
	 */
	private void createNewIndex(String indexPath, String docsPath, Long from,
			Long to) throws IOException {

		// creates a fileReader out of corpus path
		FileReader fr = new FileReader(corpusPath);

		// creates bufferedreader out of fr
		BufferedReader bfr = new BufferedReader(fr);

		// first line
		String inputLine = bfr.readLine();

		// initializes the lowest tweetID to 0
		Long tweetID = (long) 0;

		// does a linear search through our corpus until we hit the tweetID
		// we want to start an index on
		while (inputLine != null) {
			tweetID = Long.parseLong(inputLine.substring(0, 17));
			if (tweetID.compareTo(from) < 0) {

			} else {
				break;
			}
			inputLine = bfr.readLine();

		}
		FileWriter fw = new FileWriter(docsPath);
		BufferedWriter bfw = new BufferedWriter(fw);

		// contiunes to print the tweets to a file until we go greater than the
		// current
		// tweetID we want to end on
		while (inputLine != null) {
			tweetID = Long.parseLong(inputLine.substring(0, 17));
			if (tweetID.compareTo(to) <= 0) {
				bfw.write(inputLine);
				bfw.newLine();
				bfw.flush();
			} else {
				break;

			}

			inputLine = bfr.readLine();

		}
		// closes the reader and the writer
		bfr.close();
		bfw.close();

		// creates the new specified index that we want
		new TwitterIndexer(indexPath, true, docsPath);
	}

	/**
	 * Creates a url index from one point to another
	 * 
	 * @param indexPath
	 *            the index path that you are trying to create
	 * @param docsPath
	 *            the document path where the temp file will be stored for index
	 *            creation
	 * @param from
	 *            the start time of our index (Twitter tweet time)
	 * @param to
	 *            the end time of our index
	 */
	private void createNewUrlIndex(String indexPath, String docsPath,
			Long from, Long to) throws IOException {

		// creates the reader for the corpus path and writer for temp path
		System.out.println("The corpus path is " + corpusPath);
		FileReader fReader = new FileReader(corpusPath);

		BufferedReader br = new BufferedReader(fReader);

		PrintWriter writer = new PrintWriter(docsPath);

		String line = br.readLine();
		Long tweetID = Long.parseLong(line);
		// goes through corpus until it hits a tweet id greater than the one we
		// want
		while (line != null) {

			if (tweetID.compareTo(from) <= 0) {

			} else {
				break;
			}

			line = br.readLine();

			while (line != null && !line.equals(SEQUENCE_IDENTIFIER)) {

				line = br.readLine();
			}
			while (line != null && line.equals(SEQUENCE_IDENTIFIER)) {
				line = br.readLine();
			}

			if (line == null) {
				System.out
						.println("SEVERE WARNING: IF THIS OCCURS ERRORS ARE HIGHLY LIKELY");
				break;
			}

			String potentialID = line;
			tweetID = Long.parseLong(potentialID);

		}

		// makes the file between the 2 index times
		System.out.println("Now making temp file");
		while (line != null) {

			if (tweetID.compareTo(to) <= 0) {
				writer.println(tweetID);
				writer.flush();
				String content = "";

				// reads in the content
				while (line != null && !line.equals(SEQUENCE_IDENTIFIER)) {
					line = br.readLine();

					content += line;

				}
				// problem with duplicate sequence identifiers following
				while (line != null && line.equals(SEQUENCE_IDENTIFIER)) {
					line = br.readLine();
				}
				// prints content to file
				writer.println(content);
				writer.flush();
				if (line == null) {
					writer.println(SEQUENCE_IDENTIFIER);
					writer.flush();
					break;
				}

				else {

					writer.println(SEQUENCE_IDENTIFIER);
					writer.flush();
					String potentialID = line;
					tweetID = Long.parseLong(potentialID);

				}

			} else {
				break;
			}

		}
		writer.close();
		br.close();
		
		// once done with writing to file creates index out of that file
		new TwitterIndexerUrls(indexPath, true, docsPath);

	}

}