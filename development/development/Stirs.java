package development;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.*;
import java.util.StringTokenizer;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

import tm2.TM2;
import STIRS.Lucene.LuceneSearch;
import STIRS.Lucene.MassIndexer;
import STIRS.Lucene.RankedTweetList;
import STIRS.QueryProcessor.LuceneQuery;
import STIRS.QueryProcessor.QueryProcessor;
import stirsx.tm1.Module;
import stirsx.tm1.RankedJoin;
import stirsx.tm1.TM1URLOnly;
import stirsx.util.Normalizer;
import stirsx.util.TimeFilter;

/**
 * Stirs is the central class in the STIRS system. It is used to call all other
 * classes in the STIRS system. NOTE: The term Query and Topic are used
 * interchangeably through the code
 * 
 * @author Carl Tompkins v1.0
 * @author Karl Appel and Lauren Mathews v2.0
 * @author Lauren Mathews v3.0
 * 
 * @version 6/2011 v1.0
 * @version 6/7/2012 v2.0
 * @version 6/4/2014 v3.0
 */
public class Stirs {
	private final static Logger LOGGER = Logger
			.getLogger(Stirs.class.getName());
	private static FileHandler fileTxt = null;
	private static XMLFormatter formatterXML;
	private static int startTopic = 0;
	private static String tag = "default";
	private static boolean scoreComparator = false;

	/**
	 * The main method. Program execution begins here.
	 * 
	 * @param args Program arguments, which include:
	 * 		-d -> Debug mode enabled
	 * 		-task task -> denotes the task that we want to perform adhoc/filtering
	 * 		-realtime -> specifies that the task or output is real time and does not use future evidence 
	 * 		-i index -> Determines what index to use (File path)
	 * 		-q query -> Specifies what query file to use (File path)
	 * 		-r return_values -> Comma-separated list that determines what is returned
	 * 		-hits hits -> number of hits you want to return
	 * 		-allResults -> prints out all the results of our search rather than just the top 30
	 * 		-startTopic -> you tell what start topic you have, otherwise it will give you that the topic 
	 * 						number starts at 0.
	 * 		-tag -> the name of the run , could be anything 
	 */

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		// Twitter modules we wish to use if any
		ArrayList<Integer> tms = new ArrayList<Integer>();

		// the queries we have from our file
		ArrayList<LuceneQuery> queries = new ArrayList<LuceneQuery>();

		// list of ranked tweet list
		ArrayList<RankedTweetList> rtl = new ArrayList<RankedTweetList>();

		// By default, will return all possible values
		ArrayList<String> returnValues = new ArrayList<String>();
		returnValues.add("tweetID");
		returnValues.add("username");
		returnValues.add("status");
		returnValues.add("date");

		boolean debug = false;
		boolean raw = false;
		boolean allResults = false;
		File queryFile = null;

		int numHits = 1000;
		int maxOutput = 30;

		String configLog = null;

		// the index which we want to use for our search
		// this may be changed if we do a realtime search
		// /compLing/index/englishCorpusIndex
		String index = "C:/Users/Lauren/workspace/Twitter/index";

		// sets the default options for the tweets
		String option = null;
		String task = null;
		boolean realTime = false;

		// output file type
		String output = "csv";

		// the usage of our system from the command prompt or a terminal window
		final String USAGE = "USAGE: java Stirs [-task adhoc/filtering OR -realTime] [-d] [-i index] -q "
				+ "query_file [-r return_value1,return_value2,...,return_valuek]"
				+ " [-h num_hits] [-raw] [-tm 1,2,3,4 -o {3a,3ab,3b,3ba,3c}]";

		// Set up the FileHandler for the log file
		try {
			fileTxt = new FileHandler("Logging.xml");
			formatterXML = new XMLFormatter();
			fileTxt.setFormatter(formatterXML);
			LOGGER.addHandler(fileTxt);
		} catch (IOException e) {
			debug = true;
			LOGGER.warning("Logging file could not be created. Debug mode automatically turned on.");
		}

		// Initially set so that all output goes to the log file
		// This can be changed by turning on debug mode
		LOGGER.setUseParentHandlers(false);

		// Sets the level of all Handlers and the logger to fine
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.FINE);
		}
		LOGGER.setLevel(Level.FINE);

		// If there aren't any arguments, end program execution
		if (args.length == 0) {
			LOGGER.severe("FATAL - No parameters specified. Program exit.");
			System.err.println("You must specify a query file path.");
			System.err.println(USAGE);
			System.exit(-1);
		}

		// Log when STIRS begins
		LOGGER.info("STIRS started.");
		System.out.println("STIRS has been initialized...");

		// Set the command line arguments
		for (int i = 0; i < args.length; i++) {

			// output by score or by recent time
			if ("-scorecomparator".equals((args[i]).toLowerCase())) {
				scoreComparator = true;
			}

			// Command line argument that specifies whether to show helpful
			// debugging output to the console
			if ("-d".equals(args[i])) {
				debug = true;
				LOGGER.setUseParentHandlers(true);
			}

			// Command line argument that specifies whether to include Lucene
			// scores in the Tweet information. TREC specifications ask for the
			// Lucene score, so this should be enabled.
			if ("-raw".equals(args[i])) {
				raw = true;
			}

			// name of run , otherwise if not specified it will be default
			if ("-tag".equals(args[i])) {
				tag = args[i + 1];
			}

			// start topic number of the system
			if ("-startTopic".equals(args[i])) {
				startTopic = Integer.parseInt(args[i + 1]) - 1;
			}

			// sets the appropriate task for our system if one exist
			if ("-task".equals(args[i])) {
				task = args[i + 1];
				task = task.toLowerCase();

				// this specific task ask for all results to be retrieved
				// and because of this certain features are turned on
				if (task.equals("filtering")) {
					numHits = 10000;
					maxOutput = 10000;
					realTime = true;
					allResults = true;
				}
			}

			// turns realtime search on if specified
			if ("-realtime".equals(args[i])) {
				realTime = true;
			}

			// Command line parameter to specify the index to use (not
			// required to run the program)
			if ("-i".equals(args[i])) {
				if (i + 1 < args.length) {
					index = args[i + 1].trim();
				}
			}

			if (args[i].toLowerCase().equals("-allresults")) {
				allResults = true;
			}

			// Command line parameter to specify the location of the query file.
			// This parameter is required to run the program.
			else if ("-h".equals(args[i])) {
				if (i + 1 < args.length) {
					try {
						numHits = Integer.parseInt(args[i + 1].trim());
					} catch (NumberFormatException e) {
						LOGGER.severe("FATAL - Number of hits entered is not a number. Java reported: "
								+ e.getMessage() + ". " + "Program exit.");
					}
				}
			}

			// Command line parameter to specify the location of the query file.
			// This parameter is required to run the program.
			else if ("-q".equals(args[i])) {
				if (i + 1 < args.length) {
					String filePath = args[i + 1];
					try {
						queryFile = new File(filePath);
					} catch (NullPointerException e) {
						LOGGER.severe("FATAL - No file path specified. Program exit.");
						System.err.println("File path is null.");
						System.exit(-1);
					}

					if ("".equals(filePath)) {
						LOGGER.warning("File path is empty. Continuing execution however.");
						System.err.println("WARNING: File path is empty.");
					}
				}
			}

			// Command line argument that specifies what you want to return
			// This argument is not necessary to run the program.
			// If no specific return value is specified, it will return all
			// values (tweetID,status,username,date)
			else if ("-r".equals(args[i])) {
				if (i + 1 < args.length) {
					returnValues.clear();
					String newReturnV = args[i + 1];
					StringTokenizer strtok = new StringTokenizer(newReturnV,
							",");
					while (strtok.hasMoreTokens()) {
						String returnValue = strtok.nextToken().trim();
						if (!returnValues.contains(returnValue)) {
							if (returnValue.equals("tweetID")
									|| returnValue.equals("status")
									|| returnValue.equals("username")
									|| returnValue.equals("date"))
								returnValues.add(returnValue);
							else
								LOGGER.warning("Unknown return value '"
										+ returnValue + "'. Ignoring...");
						}
					}
				} else {
					LOGGER.setUseParentHandlers(true);
					LOGGER.severe("FATAL - Incorrect return variables given. "
							+ "Correct values are: tweetID,status,username,date."
							+ "Program exit.");
					System.err.println(USAGE);
					System.exit(-1);
				}
			}

			// Checks if TMs are enabled. Any number of TMs can be enabled at
			// the same time. Comma-separated list.
			else if ("-tm".equals(args[i])) {
				if (i + 1 < args.length) {
					StringTokenizer strtok = new StringTokenizer(args[i + 1],
							",");
					tms = new ArrayList<Integer>();
					while (strtok.hasMoreTokens()) {
						String tmString = strtok.nextToken().trim();
						try {
							int tmType = Integer.parseInt(tmString);
							tms.add(tmType);
						} catch (NumberFormatException e) {
							LOGGER.setUseParentHandlers(true);
							LOGGER.severe("FATAL - TM argument was not a "
									+ "number. Java reported: "
									+ e.getMessage() + " Program exit.");
							System.exit(-1);
						}
					}
				} else {
					LOGGER.setUseParentHandlers(true);
					LOGGER.severe("FATAL - TM number not specified. Program exit.");
					System.err.println(USAGE);
					System.exit(-1);
				}
			}

			// Option argument. TM 3 has several run configurations, and these
			// configurations can be specified here.
			else if ("-o".equals(args[i])) {
				if (i + 1 < args.length) {
					option = (args[i + 1]).toLowerCase();
					if (!option.equals("3a") && !option.equals("3ab")
							&& !option.equals("3b") && !option.equals("3ba")) {
						LOGGER.setUseParentHandlers(true);
						LOGGER.severe("FATAL - Option argument not valid. Program exit.");
						System.err.println(USAGE);
						System.exit(-1);
					}
				} else {
					LOGGER.setUseParentHandlers(true);
					LOGGER.severe("FATAL - Option not specified. Program exit.");
					System.err.println("You must specify an option.");
					System.err.println(USAGE);
					System.exit(-1);
				}
			}
		}

		if (allResults) {
			numHits = 10000;
			maxOutput = 10000;
		}

		// If TM 3 is enabled, make sure that there is an option selected
		if (tms != null) {
			if (tms.contains(new Integer(3)) && option == null) {
				LOGGER.setUseParentHandlers(true);
				LOGGER.severe("FATAL - Option not specified for TM 3. Program exit.");
				System.err
						.println("You did not specify an option. This is a required argument when TM 3 is enabled.");
				System.err.println(USAGE);
				System.exit(-1);
			}
		}

		// the task that we currently have are real time so they must be
		// specified as realtime
		// otherwise they may not work properly
		if (!realTime && task != null || task != null && !allResults) {
			LOGGER.warning("Arguments might be in contradiction with each other"
					+ " and an error is likely to occur. ");
		}

		// If a query file was not specified, output the message and end
		// program execution.
		if (queryFile == null) {
			LOGGER.setUseParentHandlers(true);
			LOGGER.severe("FATAL - No file path given. Can't find file. Program exit.");
			System.err
					.println("You did not specify a query file. This is a required argument.");
			System.err.println(USAGE);
			System.exit(-1);
		}

		// If a file was specified, make sure it is a file.
		else if (!queryFile.isFile()) {
			LOGGER.setUseParentHandlers(true);
			LOGGER.severe("FATAL - Path given does not lead to a file. Program exit.");
			System.err
					.println("The path that you specified does not lead to a file. Please recheck the path and try again.");
			System.err.println(USAGE);
			System.exit(-1);
		}

		// Make sure that file can be read
		else if (!queryFile.canRead()) {
			LOGGER.setUseParentHandlers(true);
			LOGGER.severe("FATAL - File cannot be read. Recheck permissions. Program exit.");
			System.err
					.println("The file that you specified cannot be read. Please recheck file permissions and try again.");
			System.exit(-1);
		}

		// configures the log to contain what the system did
		configLog = "\nIndex to use:\t\t" + index + "\n";
		configLog += "Debug mode\t\t" + debug + "\n";
		configLog += "Number of hits\t\t" + numHits + "\n";
		configLog += "Raw mode\t\t" + raw + "\n";
		configLog += "Query file path\t\t" + queryFile.getAbsolutePath() + "\n";
		configLog += "Returning values:\t" + returnValues + "\n";
		configLog += "TMs enabled:\t\t";
		if (tms != null) {
			for (int i : tms) {
				configLog += i;
				configLog += " ";
			}
			configLog += "\n";
		} else {
			configLog += "none\n";
		}
		configLog += "Option selected:\t";

		// configures the log for query expansion
		if (option != null) {
			configLog += option;
			if (option.equals("3b"))
				configLog += "->Wikipedia Query Expansion";
			else if (option.equals("3a"))
				configLog += "->WordNet Query Expansion";
			else if (option.equals("3ab"))
				configLog += "->WordNet-Wikipedia Query Expansion";
			else if (option.equals("3ba"))
				configLog += "->Wikipedia-WordNet Query Expansion";
			else
				configLog += "->Google Expansion";
		} else {
			configLog += "none";
		}

		// Log the configuration of this run of STIRS
		LOGGER.info("Parameters are O.K. -> Config completed");
		LOGGER.config(configLog);
		LOGGER.info("Query file was checked; verified as a file and determined to be readable.");

		System.out.println("Command line options and file checks completed...");
		LOGGER.info("Started QueryProcessor.");
		System.out.println("QueryProcessor initializing...");

		// if the task is not null we need to process the information out of
		// them
		// properly. This is slightly different for each task
		try {
			// the query processor is going to change the format of the query
			// from the
			// the trec format to one we can use for our system
			QueryProcessor queryProcessor = null;
			if (task != null) {
				queryProcessor = new QueryProcessor(queryFile, LOGGER, task);
			} else {
				queryProcessor = new QueryProcessor(queryFile, LOGGER);
			}

			// just gets the queries in the proper format we want
			// nothing special out it being sanitized
			queries = queryProcessor.getSanitizedQueries();

			LOGGER.info("QueryProcessor completed.");

			// Prints out the query information
			String queryStrings = "";
			for (LuceneQuery query : queries) {
				queryStrings += query.toString();
			}

			LOGGER.fine("Queries extracted from file:\n " + queryStrings);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		System.out.println("QueryProcess has successfully finished...");

		// Check to see if TM 3 is enabled; if it is, it is the next in line
		// Next step is to run through LuceneSearch
		System.out.println("RealTime search = " + realTime);

		// realtime is a bit different from a search that is not realtime
		// as a result I broke it up because it was easier to work with
		// even though there is a lot of code repeated and a good amount of
		// overloaded constructors
		LOGGER.info("Starting up LuceneSearch.");
		if (!realTime) {
			// creates a new instance of a Lucene Search
			LuceneSearch lsearch = new LuceneSearch(LOGGER, index, queries,
					returnValues, raw);
			System.out.println(numHits);
			System.out.println(maxOutput);
			lsearch.setHitsReturned(numHits);
			try {
				// does a basic lucene search
				rtl = lsearch.search();
				// if we have to run exceptions
			} catch (CorruptIndexException e) {
				LOGGER.setUseParentHandlers(true);
				LOGGER.severe("FATAL - Index is corrupt. This is a serious issue and needs to be dealt with immediately. Java reported: "
						+ e.getMessage());
			} catch (IOException e) {
				LOGGER.setUseParentHandlers(true);
				LOGGER.severe("FATAL - There was an error attempting to read the index. Please check the integrity of the directory. Java "
						+ "reported: " + e.getMessage() + ". Program exit.");
				System.exit(-1);
			} catch (ParseException e) {
				LOGGER.setUseParentHandlers(true);
				LOGGER.severe("FATAL - Lucene encountered an error when parsing the query. Please check the query/queries. Java "
						+ "reported: " + e.getMessage());
			}
			LOGGER.info("LuceneSearch finished searching.");

			List<Module> modules = new ArrayList<Module>();
			// if we have modules to run
			System.out.println("Number of hits run = " + numHits);
			System.out.println("Number of hits for 1 run is "
					+ rtl.get(0).size());
			System.out.println("Number of hits for 2 run is "
					+ rtl.get(1).size());
			System.out.println("Number of hits for 3 run is "
					+ rtl.get(2).size());
			System.out.println("Number of hits for 4 run is "
					+ rtl.get(3).size());
			System.out.println("Number of hits for 5 run is "
					+ rtl.get(4).size());
			System.out.println("Number of hits for 6 run is "
					+ rtl.get(5).size());
			if (tms != null) {
				for (Integer i : tms) {
					if (i == 1)
						modules.add(new RankedJoin()); // combo of URLs and
														// baseline (Lucene)
					else if (i == 2)
						modules.add(new TM2());
					else if (i == 4)
						modules.add(new TM1URLOnly());
				}
			}

			// for each module we are going to get the results and possibly
			// combine them
			// together
			for (Module module : modules) {
				PrintWriter temp = null;
				try {
					temp = new PrintWriter(new FileWriter("testFile.txt"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				RankedTweetList rankedTweetList = rtl.get(0);
				temp.println("Before Modules");
				for (int i = 0; i < rtl.size(); i++) {
					temp.println("The tweet for list 1 is ");
					temp.println(rankedTweetList.getTweet(i).getStatus());
					temp.println("And the score is");
					temp.println(rankedTweetList.getTweet(i).getScore());
					temp.flush();
				}

				rtl = (ArrayList<RankedTweetList>) module.getResults(queries,
						rtl);

				System.out.println("After Modules");
				for (int i = 0; i < rtl.size(); i++) {

					temp.println("The tweet for list 1 is ");
					temp.println(rankedTweetList.getTweet(i).getStatus());
					temp.println("And the score is");
					temp.println(rankedTweetList.getTweet(i).getScore());
					temp.flush();
				}
				temp.close();

				System.out.println("Ran Module: "
						+ module.getClass().getSimpleName());
			}
			TimeFilter timeFilter = new TimeFilter();
			rtl = timeFilter.filter(rtl, queries);

			Results.output(rtl, raw, output, task, allResults, maxOutput,
					startTopic, tag, scoreComparator, queries);
		}
		
		// this is a realtime search so we are going to do something a bit
		// different
		// we are going to automatically create an index of tweets which contain
		// only
		// tweets between the two different tweetID times
		// and we will process the results according to the task
		// there is going to be a class called MassIndexer that takes care of
		// the indexing for
		// the tweets between the two given times for each topic
		else {
			// corpus and indexes(one for each topic) location respectively on
			// our machine
			String corpusLocation = "/home/gituser/corpus/englishCorpus.txt";
			String indexLocation = "/home/gituser/indexes";

			// This will take in the appropriate this will take in the queries
			// and the appropriate information to see if there
			// is an index for each of the queries between the two given times
			// if there is not one it creates one and will return the location
			// it created it in
			// all the locations for all the indexes will be returned

			MassIndexer queriesIndexer = new MassIndexer(corpusLocation,
					indexLocation, queries, task);

			ArrayList<String> indexesPath = new ArrayList<String>();
			try {
				// path where each index is located for each corresponding topic
				indexesPath = queriesIndexer.getIndexesPaths();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < queries.size(); i++) {
				// the parameter for a Lucene Search takes in an arraylist of
				// LuceneQuery. For what we are doing(creating an
				// index for each topic) we will only need one item in our
				// arraylist of query.
				ArrayList<LuceneQuery> tempQuery = new ArrayList<LuceneQuery>();
				// the ranked list of tweets which will only contain one list
				ArrayList<RankedTweetList> tempList = new ArrayList<RankedTweetList>();
				tempQuery.add(queries.get(i));
				System.out.println("Index Path is " + indexesPath.get(i));
				index = indexesPath.get(i);
				System.out.println("Index used is " + index);
				// does a lucene search

				LuceneSearch lsearch = new LuceneSearch(LOGGER, index,
						tempQuery, returnValues, raw);
				System.out.println("Number of Hits requested = " + numHits);
				lsearch.setHitsReturned(numHits);
				try {
					if (task != null) {
						// topic or query number
						int queryNumber = i + 1;
						// gets the ranked tweet list for a task
						tempList = lsearch.search(queryNumber, task);
					} else {
						// gets the ranked tweet list for results without task
						// which compose of tweets from the beginning of the
						// corpus to the
						// current querytweettime
						tempList = lsearch.search();
					}
					// exception handling
				} catch (CorruptIndexException e) {
					LOGGER.setUseParentHandlers(true);
					LOGGER.severe("FATAL - Index is corrupt. This is a serious issue and needs to be dealt with immediately. Java reported: "
							+ e.getMessage());
				} catch (IOException e) {
					LOGGER.setUseParentHandlers(true);
					LOGGER.severe("FATAL - There was an error attempting to read the index. Please check the integrity of the directory. Java "
							+ "reported: " + e.getMessage() + ". Program exit.");
					System.exit(-1);
				} catch (ParseException e) {
					LOGGER.setUseParentHandlers(true);
					LOGGER.severe("FATAL - Lucene encountered an error when parsing the query. Please check the query/queries. Java "
							+ "reported: " + e.getMessage());
				}
				// adds rankedtweet list to list of all of them
				rtl.addAll(tempList);
			}
			LOGGER.info("LuceneSearch finished searching.");
			System.out.println("Finished LuceneSearch ");
			// rtl.get(0).save("rtl0.dat");
			List<Module> modules = new ArrayList<Module>();
			// if we have modules to run
			if (tms != null) {
				for (Integer i : tms) {
					// runs module 1 with the joined list
					if (i == 1) {
						// declares indexes path
						indexesPath = new ArrayList<String>();
						// corpus location and index location
						String urlCorpusLocation = "/home/gituser/corpus/allUrlContentSortedAfterFilter.txt";
						String urlIndexPath = "/home/gituser/urlIndexes";
						// mass indexer that will do the indexing for us
						MassIndexer urlMassIndexer = new MassIndexer(
								urlCorpusLocation, urlIndexPath, queries, task,
								true);

						try {
							// returns the path names of the indexes we had just
							// received
							indexesPath = urlMassIndexer.getIndexesPaths();
							System.out.println(indexesPath.size());
						} catch (Exception e) {
							System.out
									.println("There was an exception with the indexer");

						}
						// creates a new ranked join list
						modules.add(new RankedJoin(indexesPath, task,
								maxOutput, numHits));
					}
					// runs module 2 with our particular model
					else if (i == 2) {
						modules.add(new TM2());
					}
				}
			}

			System.out.println("Ranked Tweet list before module is "
					+ rtl.size());
			for (Module module : modules) {
				rtl = (ArrayList<RankedTweetList>) module.getResults(queries,
						rtl);
				System.out.println("Ran Module: "
						+ module.getClass().getSimpleName());
			}

			System.out.println("Ranked Tweet list size after modules is "
					+ rtl.size());

			// normalizes the tweets properly from [0 to 1]
			Normalizer normalizer = new Normalizer();
			System.out.println("Ran normalizer");
			rtl = normalizer.normalize(rtl);

			// prints tweets appropriately to file
			Results.output(rtl, raw, output, task, allResults, maxOutput,
					startTopic, tag, scoreComparator, queries);
		}
	}
}