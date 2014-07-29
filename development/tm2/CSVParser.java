package tm2;

import java.util.*;
import java.io.*;

/**
 * A CSV parser which preserves lines that contain commas
 * 
 * @author Matthew Kemmer v1.0
 * @version 7/14/2011 v1.0
 */
public class CSVParser {
	private Scanner s;
	private String[][] data;
	private int currRow;

	/**
	 * Creates a CSVParser with a given File object
	 */
	public CSVParser(File f) throws IOException {
		s = new Scanner(f);
		fillData();
	}

	/**
	 * Creates a CSVParser with a given file name
	 */
	public CSVParser(String file) throws IOException {
		s = new Scanner(new File(file));
		fillData();
	}

	/**
	 * Parses the data and saves it to be iterated through later
	 */
	private void fillData() throws IOException {
		ArrayList<String[]> lines = new ArrayList<String[]>();

		while (s.hasNextLine()) {
			lines.add(split(s.nextLine(), ','));
		}

		data = new String[lines.size()][];

		lines.toArray(data);

		s.close();
	}

	/**
	 * Splits a line based on a given delimiter, ignoring it if it appears
	 * between quotes
	 * 
	 * @param s
	 *            The line to be parsed
	 * @param a
	 *            The delimiter character
	 * 
	 * @return A String array containing all of the tokens separated by the
	 *         delimiter, a
	 */
	private String[] split(String s, char a) {
		int length = s.length();

		ArrayList<String> r = new ArrayList<String>();
		String t = "";
		boolean firstQuote = false;
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);

			if (c == '"') {
				firstQuote = !firstQuote;
			}

			if (c == a && !firstQuote) {
				r.add(t);
				t = "";
			} else {
				t += c;
			}
		}

		if (!t.equals("")) {
			r.add(t);
		}

		String[] rArray = new String[r.size()];

		return r.toArray(rArray);
	}

	/**
	 * Returns the tokens of the next line, if there is a next line
	 * 
	 * @param The
	 *            tokens of the next line if it exists, null otherwise
	 */
	public String[] nextLine() {
		if (currRow < data.length) {
			return data[currRow++];
		}

		return null;
	}

	/**
	 * Returns if the file has a next line
	 * 
	 * @return True if the next line exists, false otherwise
	 */
	public boolean hasNextLine() {
		if (currRow < data.length) {
			return true;
		}

		return false;
	}
}