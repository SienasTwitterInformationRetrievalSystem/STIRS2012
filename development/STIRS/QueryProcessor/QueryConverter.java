package STIRS.QueryProcessor;

import java.util.logging.Logger;

/**
 * A class that converts a query from the TREC format to one that is easier to
 * use for Lucene
 * 
 * (Example below has been modified) An example of a TREC query is: <top> <num>
 * Number: MB01 </num> <title> Wael Ghonim </title> <querytime> 25th February
 * 2011 04:00:00 +0000 </querytime> <querytweettime> 3857291841983981
 * </querytweettime> </top>
 * 
 * @author Matthew Kemmer v1.0
 * @edited Karl Appel v2.0
 * 
 * @version 5/23/11 v1.0
 * @version 6/10/12 v2.0
 */
public class QueryConverter {
	/**
	 * Returns a LuceneQuery object given a query in the TREC format
	 */
	public static LuceneQuery convertQuery(Logger logger, String q,
			boolean adhoc) {
		String[] text = q.split(" ");
		int i = 0;
		String[] vals = { "", "", "", "" };

		// each tag that will be found in the query in order
		// this is a tag for a regular search
		String[] tags = { "<top>", "<num>", "</num>", "<query>", "</query>",
				"<querytime>", "</querytime>", "<querytweettime>",
				"</querytweettime>", "</top>" };
		if (!adhoc) {
			tags[3] = "<title>";
			tags[4] = "</title>";

		}

		int tagIndex = 0;

		// parses through the query and picks out the data inbetween each tag
		while (i < text.length) {
			if (text[i].equals(tags[tagIndex] + tags[tagIndex + 1])) {
				tagIndex += 2;
			} else {
				vals[(tagIndex - 1) / 2] = vals[(tagIndex - 1) / 2] + " "
						+ text[i];
			}
			i++;
		}

		// removes the extraneous "Number: "
		if (vals[0].startsWith("Number")) {
			vals[0] = vals[0].substring(vals[0].indexOf(":") + 1);
		}

		// remove white space
		for (int j = 0; j < vals.length; j++) {
			vals[j] = vals[j].trim();
		}

		logger.finest("Values extracted: " + vals[0] + "|" + vals[1] + "|"
				+ vals[2] + "|" + vals[3] + "|");

		if (adhoc) {
			return new LuceneQuery(vals[0], vals[1], vals[2],
					Long.parseLong(vals[3]), new Long(0), "adhoc");
		} else {
			return new LuceneQuery(vals[0], vals[1], vals[2],
					Long.parseLong(vals[3]));
		}
	}

	/**
	 * 
	 * @param logger
	 *            the logger that will record information about the system
	 * @param q
	 *            the query line that contains all the information neccessary
	 *            about the query
	 * @param task
	 *            the task that we want to perform
	 * @return LuceneQuery a lucene query for the particular topic
	 */
	public static LuceneQuery convertQuery(Logger logger, String q, String task) {
		// splits the text by a space
		String[] text = q.split(" ");

		// initializes the
		int i = 0;

		// the vals are going to be the values we extract from the query line in
		// order which they
		// are written so this is the <top> , <num> , <querytweettime> , etc
		// stuff we have
		String[] vals = { "", "", "", "", "" };

		if (task.equals("adhoc")) {
			return convertQuery(logger, q, true);
		}

		if (task.equals("filtering")) {
			// tags for the filtering task
			String[] tags = { "<top>", "<num>", "</num>", "<query>",
					"</query>", "<querytime>", "</querytime>",
					"<querytweettime>", "</querytweettime>",
					"<querynewesttweet>", "</querynewesttweet>", "</top>" };

			int tagIndex = 0;

			// parses through the query and picks out the data inbetween each
			// tag
			while (i < text.length) {
				if (text[i].equals(tags[tagIndex] + tags[tagIndex + 1])) {
					tagIndex += 2;
				} else {
					vals[(tagIndex - 1) / 2] = vals[(tagIndex - 1) / 2] + " "
							+ text[i];
				}
				i++;
			}

			// removes the extraneous "Number: "
			if (vals[0].startsWith("Number")) {
				vals[0] = vals[0].substring(vals[0].indexOf(":") + 1);
			}

			// remove white space
			for (int j = 0; j < vals.length; j++) {
				vals[j] = vals[j].trim();
			}

			// logger values that were extracted
			logger.finest("Values extracted: " + vals[0] + "|" + vals[1] + "|"
					+ vals[2] + "|" + vals[3] + "|" + vals[4] + "|");
		}
		System.out.println("Lucene Query Created");

		System.out.println(new LuceneQuery(vals[0], vals[1], vals[2], Long
				.parseLong(vals[3]), Long.parseLong(vals[4]), task));
		
		// returns a new LuceneQuery of the values extracte
		return new LuceneQuery(vals[0], vals[1], vals[2],
				Long.parseLong(vals[3]), Long.parseLong(vals[4]), task);
	}
}