package STIRS.QueryProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Hashtable;

/**
 * A slang dictionary lookup
 * 
 * @author Matthew Kemmer v1.0
 * @version 6/14/11 v1.0
 */
public class SlangDictionary {
	private Hashtable<String, String> dictionary;
	private final String SLANG_DICT_LOC = "/home/gituser/development/slangDict.txt";

	public SlangDictionary() {
		fillDictionary();
	}

	/** Fills the dictionary from the text file */
	private void fillDictionary() {
		try {
			Scanner s = new Scanner(new File(SLANG_DICT_LOC));
			dictionary = new Hashtable<String, String>(s.nextInt());
			s.nextLine();
			
			while (s.hasNextLine()) {
				s.nextLine();
				String slang = s.nextLine();
				String english = s.nextLine();
				dictionary.put(slang, english);
			}
			
			s.close();
		} catch (FileNotFoundException e) {
			System.err.print(e.toString());
			System.exit(0);
		}
	}

	/**
	 * Searches the dictionary for the slang term.
	 * 
	 * @param slang
	 *            The slang term to look up
	 * @return The equivalent English term. If no term was found, null is
	 *         returned.
	 */
	public String lookUp(String slang) {
		return dictionary.get(slang.toLowerCase());
	}
}