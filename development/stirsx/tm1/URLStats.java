package stirsx.tm1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stirsx.util.Log;

/**
 * Outputs URL statistics for a given file to stdout. The output lists all
 * document types linked (pdf, png, html, etc.) and the number of times each
 * type occurs.
 * 
 * @author David Purcell v1.0
 * @version 6/2011 v1.0
 */
public final class URLStats implements Runnable {

	//The number of threads to create when reading in a file.
	private static final int NUM_THREADS = 16;

	//A map for counting the number of each URL type.
	private static final Map<String, Integer> typeMap = new HashMap<String, Integer>();

	private String fileName;
	private int indexStart;
	private int indexEnd;

	/**
	 * @param file
	 *            The file to read.
	 * @param i
	 *            The line in the file to start reading at (inclusive, 0 based).
	 * @param j
	 *            The last line in the file to read (inclusive, 0 based).
	 */
	private URLStats(String file, int i, int j) {
		fileName = file;
		indexStart = i;
		indexEnd = j;
	}

	public void run() {
		// traverse entire file
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException ex) {
			Log.exception(ex);
			return;
		}

		String line = null;
		int i = 0;
		
		// bring the line up to the starting line
		while (i <= indexStart) {
			try {
				line = br.readLine();
				i++;
			} catch (IOException ex) {
				Log.exception(ex);
			}
		}
		
		// offset
		i--;

		Pattern urlPattern = Pattern.compile(URLListGen.URL_REGEX);

		// for each line
		while (line != null && i <= indexEnd) {
			// for each url in the line (regex match)
			Matcher matcher = urlPattern.matcher(line);
			
			// determine url type (html, png, pdf, etc.)
			while (matcher.find()) {
				String urlName = matcher.group();
				
				// handle www format urls
				if (urlName.startsWith("www.")) {
					urlName = "http://" + urlName;
				}
				
				// find the url type
				URL url = null;
				String type = "null";
				
				try {
					url = new URL(urlName);
					type = ((HttpURLConnection) url.openConnection())
							.getContentType();
				} catch (IOException ex) {
					Log.exception(ex);
					continue;
				}

				// increase count of type
				synchronized (typeMap) {
					Integer count = typeMap.get(type);
					typeMap.put(type, (count == null ? 0 : count) + 1);
				}
			}
			
			try {
				line = br.readLine();
			} catch (IOException ex) {
				Log.exception(ex);
			}
			
			i++;
		}
		
		try {
			br.close();
		} catch (IOException ex) {
			Log.exception(ex);
		}
	}

	/**
	 * Generates file URL statistics.
	 * 
	 * @param args
	 *            A single argument indicating the name of the file on disk to
	 *            create stats for.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			Log.error("Usage: <inputFile>");
			return;
		}

		// count the lines in the file
		int lineCount = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(args[0]));
			
			while (br.readLine() != null) {
				lineCount++;
			}
		} catch (FileNotFoundException ex) {
			Log.exception(ex);
			return;
		}
		
		br.close();

		// distribute work evenly across the threads
		int range = (int) Math.ceil((double) lineCount / NUM_THREADS);
		
		// number of threads before work is done (should be 1)
		int threadCount = Thread.activeCount();

		// spawn threads
		for (int index = 0; index < lineCount; index += range) {
			int end = index + range - 1;
			end = end < lineCount ? end : lineCount - 1;

			Thread thread = new Thread(new URLStats(args[0], index, end));
			thread.start();
		}

		// wait for threads to finish
		while (Thread.activeCount() != threadCount) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Log.exception(ex);
			}
		}
		
		// print out counts of every type + total URLs
		for (String key : typeMap.keySet()) {
			Log.info(key + ": " + typeMap.get(key));
		}
	}
}