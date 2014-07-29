package GoogleArchive;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import STIRS.QueryProcessor.LuceneQuery;

/**
 * This class expands the query by using Google. It will search Google and find
 * documents that are relevant to the query. It will find the most common words
 * used within the document and return the value as a String. It uses the
 * Article class to remove the symbols and stop words that are unnecessary.
 * 
 * @author Chan Tran v1.0
 * @version 7/22/2011 v1.0
 */
public class GoogleExpansion {

	String query;
	String document;
	int countCommon;
	ArrayList<String> txt = new ArrayList<String>();
	ArrayList<String> mostCommon = new ArrayList<String>();
	ArrayList<String> all = new ArrayList<String>();
	ArrayList<String> fileOut = new ArrayList<String>();
	ArrayList<Integer> num = new ArrayList<Integer>();
	StringTokenizer token;
	LuceneQuery luc;

	public GoogleExpansion() {
		document = "";
		luc = null;
		countCommon = 0;
	}

	/**
	 * This constructor takes in a doc String and sets the field variable
	 * document to the doc of the parameter. Everything else is treated like the
	 * empty constructor.
	 */
	public GoogleExpansion(String doc) {
		this.document = doc;
		luc = null;
		countCommon = 0;
	}

	/**
	 * This method finds the most common word or words in a given text or
	 * document. It will loop through each word and find out with word/words
	 * occurs the most and returns the values of the words as a String.
	 */
	public String mostCommon(String doc, String query, int numArticles) {
		this.query = query;
		Article art = new Article();
		document = art.notWantedQuery(doc);
		token = new StringTokenizer(document, " ");
		
		// Loops through the doc to find each individual word and adds it to an
		// ArrayList of String.
		while (token.hasMoreTokens()){
			txt.add(token.nextToken());
		}

		// Remove possible words that are already in the query
		removeQueryWords(query);

		// Counts the number of times the word occurs within the entire text.
		for (int i = 0; i < txt.size(); i++) {
			countCommon = 0;
			for (int j = 0; j < txt.size(); j++) {
				if (txt.get(i).equalsIgnoreCase(txt.get(j)))
					countCommon++;
			}
			num.add(countCommon);
		}

		// Finds the four most re-occurring words in the text.
		int count = 0;
		while (count < 4) {
			int highest = 0;
			int index = 0;
			
			for (int i = 0; i < num.size(); i++) {
				if (num.get(i) > highest) {
					highest = num.get(i);
					index = i;
				}
			}
			
			if (highest > 1) {
				String wordAt = txt.get(index);
				mostCommon.add(txt.get(index));
				all.add(txt.get(index));
				
				// Removes the word after it is accounted for.
				for (int i = 0; i < txt.size(); i++) {
					if (txt.get(i).equalsIgnoreCase(wordAt)) {
						txt.remove(i);
						num.remove(i);
						i = 0;
					}
				}
			}
			count++;
		}

		if (mostCommon.size() == 0) {
			for (int i = 0; i < Math.min(4, txt.size()); i++) {
				mostCommon.add(txt.get(i));
			}
		}

		String out = "";
		
		for (int i = 0; i < mostCommon.size(); i++) {
			if (i == mostCommon.size() - 1)
				out += mostCommon.get(i);
			else
				out += mostCommon.get(i) + " ";
		}
		
		System.out.println(mostCommon);
		mostCommon.clear();
		txt.clear();
		num.clear();
		return out;
	}

	public void removeQueryWords(String query) {
		// Removes any words that are contained in the query itself.
		token = new StringTokenizer(query, " ");
		
		while (token.hasMoreTokens()) {
			String indivQuery = token.nextToken();
			for (int i = 0; i < txt.size(); i++) {
				if (indivQuery.equalsIgnoreCase(txt.get(i))) {
					txt.remove(i);
					i = 0;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String compare(String allTxt) {
		String out = "";
		ArrayList<String> every = new ArrayList<String>();
		token = new StringTokenizer(allTxt, " ");
		
		while (token.hasMoreTokens()){
			every.add(token.nextToken());
		}

		ArrayList<String> removable = (ArrayList<String>) every.clone();
		ArrayList<Integer> frequency = new ArrayList<Integer>();
		int size = every.size();
		
		for (int i = 0; i < size; i++) {
			int numFound = 0;
			for (int j = 0; j < removable.size(); j++) {
				if (every.get(i).equalsIgnoreCase(removable.get(j))) {
					removable.remove(j);
					j--;
					numFound++;
				}
			}
			frequency.add(numFound);
		}
		
		@SuppressWarnings("unused")
		int numReturn = 0;
		for (int i = 0; i < frequency.size(); i++) {
			int freq = frequency.get(i);
			if (freq > 1) {
				out += every.get(i) + " ";
				numReturn++;
			}
		}
		out = out.trim();
		all.clear();
		fileOut.add(out);
		return out;
	}

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		GoogleExpansion go = new GoogleExpansion();
		while (true) {
			System.out.print("Word or phrase: ");
			String user = in.nextLine();
			go.mostCommon(user, "Groundhog Day", 4);
			System.out.println(go.compare(user));
			@SuppressWarnings("unused")
			LuceneQuery test = new LuceneQuery("003", "Saleh Yemen Overthrow",
					"Fri Feb 04 20:03:25 +0000 2011", (long) 256);
		}
	}
}
