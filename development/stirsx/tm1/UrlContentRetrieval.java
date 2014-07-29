package stirsx.tm1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Takes a file with the Tweet ID and url(s) which are on the
 * same line and gets the content of that url. This content with the Tweet ID is
 * then sent to a new file. Urls that cannot be read will be sent to a error
 * file with the associated Tweet ID.
 * 
 * @author Karl Appel v1.0
 * @version 6/22/11 v1.0
 */
public class UrlContentRetrieval {

	public static final String SEQUENCE_INDENTIFIER = "^)*&!P!@#@^^^((5hg%jsJ";

	public static void main(String[] arg) throws IOException {
		System.out.println("Program running... Please wait");

		String urlFile = arg[0];

		int indexStart = Integer.parseInt(arg[1]);
		int indexEnd = Integer.parseInt(arg[2]);

		String contentFileLocation = arg[3];
		String errorFileLocation = arg[4];

		// Will be used to calculate how long it took to do this process
		long startTime = System.currentTimeMillis();

		/*
		 * this string is a set of unique characters that will help use identify
		 * a new tweet id
		 */

		FileInputStream fPos = new FileInputStream(urlFile);
		BufferedReader bPos = new BufferedReader(new InputStreamReader(fPos));

		File contentFile = new File(contentFileLocation);
		File errorFile = new File(errorFileLocation);

		// if these files already exist delete them
		Boolean contentFileExist = contentFile.exists();
		Boolean errorFileExist = errorFile.exists();

		if (contentFileExist) {
			contentFile.delete();
		}

		if (errorFileExist) {
			errorFile.delete();
		}

		PrintWriter errorFileWriter = new PrintWriter(errorFile);
		PrintWriter contentFileWriter = new PrintWriter(contentFile);

		int urlsProcessed = 0;

		// the url is always going to be the second word in a parse with
		// the tweet ID being the first

		int tweetIDArrayNum = 0;
		int urlArrayNum = 1;
		int numUrls = (indexEnd - indexStart) + 1;
		int count = 0;

		while (count < indexStart) {
			bPos.readLine();
			count++;
		}

		// goes through all the urls and tries to retrieve their content
		while (urlsProcessed < numUrls) {
			String textLine = bPos.readLine();

			// tweet id and url are two different words in the array
			String[] arrayWords = textLine.split(" ");

			String url = arrayWords[urlArrayNum];

			if (url.startsWith("www.")) {
				url = "http://" + url.substring(4);
			}

			String urlPageContent = null;

			// gets content from url
			try {
				Document doc = Jsoup.connect(url).get();

				// converts the content from html to text
				urlPageContent = Jsoup.parse(doc.toString()).text();
			}
			// if the information cannot be retrieved the Tweet ID and url are
			// printed in a error file
			catch (Exception e) {
				errorFileWriter.println(arrayWords[tweetIDArrayNum] + " "
						+ arrayWords[urlArrayNum]);
				errorFileWriter.flush();
			}

			// if the information is retrievable the tweet id and url content
			// are placed in a content folder
			if (urlPageContent != null) {
				contentFileWriter.println(arrayWords[tweetIDArrayNum]);
				contentFileWriter.println(urlPageContent);
				contentFileWriter.println(SEQUENCE_INDENTIFIER);
				contentFileWriter.flush();
			}
			
			// prints out how many urls have been processed after each is
			// processed
			System.out.println(urlsProcessed);
			urlsProcessed++;
		}

		bPos.close();
		contentFileWriter.close();
		errorFileWriter.close();

		// Total time it took for this program to run
		long endTime = System.currentTimeMillis();

		long totalTime = (endTime - startTime) / 1000;

		System.out.print("Total time = " + totalTime + " seconds");
	}
}