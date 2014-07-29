package STIRS.QueryProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The TwitterSearch class queries a user-specified index and returns multiple
 * results.
 * 
 * @author Carl Tompkins v1.0
 * @version 6/7/11 v1.0
 */
public class QueryProcessor {

	// index path
	String index = "/compLing/index/";

	ArrayList<LuceneQuery> luceneQueryList = new ArrayList<LuceneQuery>();
	ArrayList<String> topicsList = new ArrayList<String>();

	/**
	 * Processes the Query appropriately with the proper format needed for our
	 * system
	 * 
	 * @param inputFile
	 *            the inputfile where the queries are located
	 * @param logger
	 *            the logger that keeps track of the system information
	 */
	public QueryProcessor(File inputFile, Logger logger) throws IOException {

		try {
			logger.info("Beginning to read query file.");
			
			// reads in file with queries
			FileInputStream fis = new FileInputStream(inputFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String line = null;
			
			// for each line in our list
			while ((line = reader.readLine()) != null) {
				System.out.println("The current query Line is " + line);
				// adds the query to our list
				luceneQueryList.add(QueryConverter.convertQuery(logger, line,
						false));
			}
			
			logger.info("Finished reading in query file.");

			reader.close();
			// Exceptions
		} catch (FileNotFoundException e) {
			logger.severe("FATAL - " + e.getMessage());
			System.err.println("FATAL - " + e.getMessage());
		} catch (NumberFormatException e) {
			logger.severe("There is an error with the syntax of the query file"
					+ ". Please check the file. Java reported: "
					+ e.getMessage());
		}
	}

	/**
	 * Query Processor for particular task
	 * 
	 * @param inputFile
	 *            the inputfile where the queries are located
	 * @param logger
	 *            the logger that keeps track of the system information
	 * @param task
	 *            the particular task we are asked to solve for this
	 */
	public QueryProcessor(File inputFile, Logger logger, String task)
			throws IOException {

		// expands the query so that it does not contain slang
		// the field

		try {
			logger.info("Beginning to read query file.");

			// input file with query
			FileInputStream fis = new FileInputStream(inputFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));

			String line = null;

			while ((line = reader.readLine()) != null) {
				// creates a new query out of the information from the file and
				// parses the information
				// appropriately
				luceneQueryList.add(QueryConverter.convertQuery(logger, line,
						task));
				// luceneQueryList.add(QueryConverter.convertQuery(logger,line));
				System.out.println("Processor done");
			}

			reader.close();

			logger.info("Finished reading in query file.");

			// exceptions and errors that may occur
		} catch (FileNotFoundException e) {
			logger.severe("FATAL - " + e.getMessage());
			System.err.println("FATAL - " + e.getMessage());
		} catch (NumberFormatException e) {
			logger.severe("There is an error with the syntax of the query file"
					+ ". Please check the file. Java reported: "
					+ e.getMessage());
		}
	}

	/**
	 * The returns the lucene query
	 * 
	 * @return luceneQueryList the lucene query all properly formatted
	 */
	public ArrayList<LuceneQuery> getSanitizedQueries() {
		return luceneQueryList;
	}
}