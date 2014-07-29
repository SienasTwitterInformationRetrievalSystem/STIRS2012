package stirsx.tm1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stirsx.util.Log;

/**
 * <p>
 * Generates a list of tweetIDs and URLs from a given corpus of tweets.
 * <p>
 * 
 * Each tweetID is written to an output file line, followed the URL in that
 * tweet (space separated). If a tweetID has multiple URLs, there will be one
 * line for each tweetID/URL pair.
 * <p>
 * 
 * @author David Purcell v1.0
 * @version 6/2011 v1.0
 */
public final class URLListGen {

	/**
	 * A regular expression for matching URLs.
	 * <p>
	 * The regex matches URLs with standard "<i>http://</i>", "<i>https://</i>",
	 * "<i>www.</i>", "<i>ftp://</i>", and "<i>file://</i>" protocols.
	 * <p>
	 * The regex does <strong>not</strong> match URLs like
	 * "<i>youtube.com/example</i>".
	 */
	public static final String URL_REGEX = "\\b((https?|ftp|file)://|"
			+ "(www|ftp)\\.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*"
			+ "[-a-zA-Z0-9+&@#/%=~_|]";

	private URLListGen() {}

	/**
	 * Generates a tweetID/URL listing from a given corpus. The first parameter
	 * is the name of the corpus to read. The second parameter is the name of
	 * the output file to write to.
	 * 
	 * @param args
	 *            The two arguments (input file, output file).
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			Log.error("Usage: <inputFile> <outputFile>");
			return;
		}

		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException ex) {
			Log.exception(ex);
			return;
		}

		PrintWriter out = new PrintWriter(args[1]);
		String line = br.readLine();
		Pattern urlPattern = Pattern.compile(URLListGen.URL_REGEX);

		while (line != null) {
			Matcher matcher = urlPattern.matcher(line);
			String tweetID = (new StringTokenizer(line)).nextToken();
			
			// find urls in tweet
			while (matcher.find()) {
				String url = matcher.group();
				out.println(tweetID + " " + url);
				out.flush();
			}
			
			line = br.readLine();
		}

		out.close();
		br.close();
	}

	/**
	 * Returns whether or not the specified text contains a URL. Valid URLs are
	 * specified by the {@linkplain #URL_REGEX} pattern.
	 * 
	 * @param text
	 *            The text to check for URLs.
	 * @return <code>true</code>, if the text contains one or more URLs.
	 * @see #URL_REGEX
	 */
	public static boolean containsUrl(String text) {
		Pattern urlPattern = Pattern.compile(URLListGen.URL_REGEX);
		Matcher matcher = urlPattern.matcher(text);

		return matcher.find();
	}
}