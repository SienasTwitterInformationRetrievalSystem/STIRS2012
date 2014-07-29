package GoogleArchive;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This class is used to remove all unwanted words from a given text file or
 * query. It will find stop words, articles and prepositions, in order to
 * determine the importance of that word.
 * 
 * @author Chan Tran v1.0
 * @version 7/21/2011 v1.0
 */
public class Article {

	ArrayList<String> wantedQuery = new ArrayList<String>();
	String wanted;
	String compareMe;

	public Article() {
		wanted = "";
		compareMe = "";
	}

	/**
	 * This method is used to determine whether or not it is a stop word. It
	 * will compare the word in the parameter with the list of stop words from
	 * the StopWords class. If it is a stop word, this will return true.
	 * Otherwise, it will return false if the given word is not a stop word.
	 */
	public boolean notWantedWord(String word) {
		boolean unWanted = false;
		compareMe = word;

		for (int i = 0; i < StopWords.LONG.length; i++) {
			if (word.equalsIgnoreCase(StopWords.LONG[i]))
				unWanted = true;
		}

		for (int i = 0; i < StopWords.SYMBOLS.length; i++) {
			if (word.equalsIgnoreCase(StopWords.SYMBOLS[i]))
				unWanted = true;
		}

		return unWanted;
	}

	/**
	 * This method takes in a query from the parameter. It will split up the
	 * query into individual words. An ArrayList of String will be used to store
	 * the individual words. If any of the words contains a stop word in the
	 * StopWord class, it will be removed from the ArrayList. It will return the
	 * ArrayList with unnecessary words taken out (if any).
	 */
	public String notWantedQuery(String query) {
		wantedQuery.clear();
		compareMe = query;
		StringTokenizer token = new StringTokenizer(query, " ");

		while (token.hasMoreTokens()) {
			wantedQuery.add(token.nextToken());
		}

		// Removes any words from the StopWords class under the constant LONG.
		for (int i = 0; i < StopWords.LONG.length; i++) {
			for (int j = 0; j < wantedQuery.size(); j++) {
				if (wantedQuery.get(j).equalsIgnoreCase(StopWords.LONG[i]))
					wantedQuery.remove(j);
			}
		}

		// Removes any symbols from StopWords class under the constant SYMBOLS.
		for (int i = 0; i < StopWords.SYMBOLS.length; i++) {
			for (int j = 0; j < wantedQuery.size(); j++) {
				if (wantedQuery.get(j).equalsIgnoreCase(StopWords.SYMBOLS[i]))
					wantedQuery.remove(j);
			}
		}

		wanted = "";

		for (int i = 0; i < wantedQuery.size(); i++) {
			if (i == wantedQuery.size() - 1)
				wanted += wantedQuery.get(i);
			else
				wanted += wantedQuery.get(i) + " ";
		}
		return wanted;
	}

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		Article test = new Article();
		while (true) {
			System.out.print("Choose a word or phrase: ");
			String user = in.nextLine();
			String ting = (String) test.notWantedQuery(user);
			System.out.println(ting);
		}
	}
}