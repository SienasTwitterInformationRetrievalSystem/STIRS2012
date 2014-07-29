package STIRS.QueryProcessor;

/**
 * Translates the query result with slang into a new query result with the prior
 * query plus the translation of the slang
 * 
 * @author Karl Appel v1.0
 * @version 6/6/11 v1.0
 */
public class QueryExpansion {
	// intializes class slang to be called as slangword
	private SlangDictionary slangWord;

	public QueryExpansion() {
		// slang class to be used
		slangWord = new SlangDictionary();
	}

	/**
	 * Checks to see if any of the slang matches up to the dictionary we have
	 * provided
	 */
	public LuceneQuery slangQuery(LuceneQuery luceneQueryTerm) {
		// intializes the new query which will contain the set of all words
		// translated
		String newQuery = "";

		// gets the array of each word inside a tweet
		String queryWords[] = luceneQueryTerm.getQueryTerms();

		// for length of tweet each word is checked
		for (int count = 0; count < queryWords.length; count++) {
			String checkWord = slangWord.lookUp(queryWords[count]);
			// if slang matches in dictionary it is translated
			if (checkWord != null) {
				newQuery = newQuery + " " + checkWord;
			} else {
				newQuery = newQuery + " " + queryWords[count];
			}
		}

		// sets this as the new query
		luceneQueryTerm.setQuery(newQuery);
		return luceneQueryTerm;
	}
}