package QueryExpansion;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Carl Tompkins and Chan Tran v1.0
 * @version 7/21/2011 v1.0
 */
public class Word {
	String word = null;
	String definition = null;
	String[] articles = { "a", "an", "the" };
	String[] prepositions = { "as", "at", "by", "for", "in", "of", "on", "to",
			"with" };
	ArrayList<String> synonyms = null;
	ArrayList<String> posTags = null;

	public Word() {
		word = "";
		definition = "";
		synonyms = null;
		posTags = new ArrayList<String>();
	}

	/**
	 * Constructor that initializes the word field
	 * 
	 * @param term
	 *            the word for the object to hold
	 */
	public Word(String term) {
		word = term;
		definition = "";
		synonyms = null;
		posTags = new ArrayList<String>();
	}

	/**
	 * Constructor that initializes all fields
	 * 
	 * @param word
	 *            the word to hold
	 * @param def
	 *            the word's definition
	 * @param synonyms
	 *            the word's synonyms
	 */
	public Word(String word, String def, ArrayList<String> synonyms) {
		this.word = word;
		this.definition = def;
		this.synonyms = synonyms;
		posTags = new ArrayList<String>();
	}

	/** Adds posTag to the ArrayList of Strings. */
	public void addPosTag(String posTag) {
		posTags.add(posTag);
	}

	/**
	 * Returns the ArrayList of posTags if any Strings are contained within the
	 * ArrayList.
	 */
	public ArrayList<String> getPosTags() {
		return posTags;
	}

	/**
	 * This method compares the given query in the parameter with WordNet. It
	 * will split up the query into individual words and find the synonyms of
	 * each word. When the synonyms are found (if any), output will add the
	 * original word followed by the synonym of that word back into the query.
	 */
	public String compare(String query) {
		String output = new String();
		LucQeWord dict = new LucQeWord();
		ArrayList<String> words = new ArrayList<String>();
		StringTokenizer strtok = new StringTokenizer(query, " ");
		String syn = "";
		int loops = 0;
		int num = 0;

		// Splits up the query one by one.
		while (strtok.hasMoreTokens()) {
			words.add(strtok.nextToken());
			num++;
			dict.wordPhraseSynonyms(words.get(loops));
			syn = dict.getAllSynonyms();

			// Finds the synonyms of the word.
			strtok = new StringTokenizer(syn, ",");
			while (strtok.hasMoreTokens()) {
				String temp = strtok.nextToken();
				if (!words.get(loops).trim().equals(temp.trim())) {
					words.set(loops, temp.trim());
					break;
				}
			}
			// This is used to move onto the next word in the given query.
			// Without this, the while loop will not function
			// correctly.
			strtok = new StringTokenizer(query, " ");
			for (int i = 1; i <= num; i++) {
				if (strtok.hasMoreTokens())
					strtok.nextToken();
			}
			loops++;
		}
		// Loops through the ArrayList of words and adds them to the String of
		// output.
		for (int i = 0; i < words.size(); i++) {
			if (i == words.size() - 1) {
				output += words.get(i);
			} else {
				output += words.get(i) + " ";
			}
		}
		return output;
	}

	/**
	 * This is the original compare method. This is used to test the comparison
	 * of a given word and WordNet. It will find all of the synonyms for the
	 * given word and return a synonym of that word.
	 */
	public String compareTest(String word) {
		LucQeWord dict = new LucQeWord();
		String newWord = word;
		String syn = "";
		dict.phraseSynonyms(word);
		syn = dict.getAllSynonyms();

		// Looping through synonyms of word.
		StringTokenizer strtok = new StringTokenizer(syn, ",");
		while (strtok.hasMoreTokens()) {
			String temp = strtok.nextToken();
			if (!word.trim().equals(temp.trim())) {
				newWord = temp.trim();
				break;
			}
		}
		return newWord;
	}

	/**
	 * This method is a shortened version of originalCompare and returns an
	 * ArrayList of all the terms that are synonyms of the given query.
	 */
	public ArrayList<String> getSynonyms(String query) {
		ArrayList<String> synonymArray = new ArrayList<String>();
		LucQeWord dict = new LucQeWord();
		String syn = "";
		dict.phraseSynonyms(query);
		System.out.println("Your query: " + query);
		syn += dict.getAllSynonyms();
		StringTokenizer strtok = new StringTokenizer(syn, ",");
		while (strtok.hasMoreTokens()) {
			synonymArray.add(strtok.nextToken());
		}
		return synonymArray;
	}

	/**
	 * Get method that returns the word
	 * 
	 * @return the term that is defined and the synonyms refer to
	 */
	public String getWord() {
		return word;
	}

	public boolean hasSynonyms() {
		return true;
	}

	/**
	 * @return true if term is an article, otherwise false if term is not an
	 *         article Determines whether or not the String term is an article
	 *         of a, an, and the.
	 */
	public boolean isArticle(String term) {
		for (String article : articles) {
			if (term.equals(article)) {
				return true;
			}
		}
		return false;
	}

	public boolean isArticle() {
		for (String article : articles) {
			if (this.word.equals(article)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if term is a preposition, otherwise false if term is not a
	 *         preposition Determines whether or not the String term is a
	 *         preposition (see field variable prepositions).
	 */
	public boolean isPreposition(String term) {
		for (String prep : prepositions) {
			if (term.equals(prep)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPreposition() {
		for (String prep : prepositions) {
			if (this.word.equals(prep)) {
				return true;
			}
		}
		return false;
	}

	public String setDefinition() {
		this.definition = LucQeWord.getDefinition(this);
		return this.definition;
	}

	/**
	 * setDefinition sets the definition for the object
	 * 
	 * @param definition
	 *            the definition of the word
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * setTerm sets the word for the object
	 * 
	 * @param term
	 *            the term to set
	 */
	public void setTerm(String term) {
		word = term;
	}

	/**
	 * This toString method returns the output of the word, definition, and its
	 * synonyms in the form: \t (tab) Word: (tab) word \t (tab) Definition:
	 * (tab) define word \t (tab) Synonyms: (tab) any synonyms of the word
	 */
	public String toString() {
		String returnString = "\tWord: \t" + word + "\n\tDefinition: "
				+ definition + "\n";

		returnString += "\tPart of speech tags:\t" + posTags
				+ "\n-----------------------------------\n";
		return returnString;
	}
}