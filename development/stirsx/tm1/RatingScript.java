package stirsx.tm1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class RatingScript {

	/**
	 * first put -dataSet , the data set you want that contain the relevant
	 * results example: file0.txt, file1.txt, etc. second put -resultSet, then
	 * results you want to test , "originalResults.txt", etc.
	 */
	public static void main(String[] args) throws IOException {

		// contains the tweets that either are considered relevant or highly
		// relevant
		ArrayList<String> relevant = new ArrayList<String>();
		ArrayList<String> highlyRelevant = new ArrayList<String>();

		int i = 1;

		// takes in all the data sets requested
		while (args[i] != "-resultSet") {
			// file location, the folder is defined but the files in the folders
			// are called
			String RESULTS_LOCATION = "C:/" + args[i];

			// new buffer reader created to take file input in
			BufferedReader testSets = new BufferedReader(new InputStreamReader(
					new FileInputStream(RESULTS_LOCATION)));

			String line = testSets.readLine();

			// takes in each tweet line by line
			while (line != null) {
				line = testSets.readLine();

				// breaks the tweet up so we can get rating
				StringTokenizer tokenizedStrings = new StringTokenizer(line);
				String rating = null;

				while (tokenizedStrings.hasMoreTokens()) {
					rating = tokenizedStrings.nextToken();

					if (rating.equals("1")) {
						relevant.add(line);
					} else {
						relevant.add(line);
						highlyRelevant.add(line);
					}

					i++;
				}

				while (i < args.length) {
					String file = "C:/" + args[i];

					BufferedReader resultSets = new BufferedReader(
							new InputStreamReader(new FileInputStream(file)));

					line = resultSets.readLine();

					resultSets.close();
				}
				testSets.close();
			}
		}
	}
}