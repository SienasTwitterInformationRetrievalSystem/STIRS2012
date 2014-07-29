package QueryExpansion;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import STIRS.QueryProcessor.LuceneQuery;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * This class is meant to extract data from Wikipedia and expand queries to
 * Lucene.
 * 
 * @author Carl Tompkins and Chan Tran v1.0
 * @version 7/21/2011 v1.0
 */
public class WikipediaExpander {

	ArrayList<LuceneQuery> queries = null;
	ArrayList<String> completeTermList;
	ArrayList<String> mostCommon;
	MediaWikiBot bot;
	ArrayList<Word> terms;
	Pattern pattern = null;
	Matcher matcher;

	public WikipediaExpander() {
		terms = new ArrayList<Word>();
		completeTermList = new ArrayList<String>();
		mostCommon = new ArrayList<String>();

		try {
			bot = new MediaWikiBot("http://en.wikipedia.org/w/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor that takes an ArrayList of LuceneQuery objects
	 * 
	 * @param queries
	 *            ArrayList of LuceneQuery objects that containsthe queries that
	 *            need to be expanded
	 */
	public WikipediaExpander(ArrayList<LuceneQuery> queries) {
		this.queries = queries;
		mostCommon = new ArrayList<String>();
		terms = new ArrayList<Word>();
		completeTermList = new ArrayList<String>();
		try {
			bot = new MediaWikiBot("http://en.wikipedia.org/w/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor that sets up an initial topic.
	 * 
	 * @param term
	 *            The term to find within Wikipedia
	 */
	public WikipediaExpander(String topic) {
		terms = new ArrayList<Word>();
		StringTokenizer strtok = new StringTokenizer(topic, " ");
		int numTokens = strtok.countTokens();

		// Handles the "1,2,3" situation
		while (strtok.hasMoreTokens()) {
			terms.add(new Word(strtok.nextToken()));
		}

		if (numTokens > 2) {
			for (int i = 0; i < numTokens; i++) {
				if ((i + 1) != numTokens) {
					String nextTok1 = terms.get(i).getWord();
					String nextTok2 = terms.get(i + 1).getWord();
					terms.add(new Word(nextTok1 + " " + nextTok2));
				}
			}
		}

		// Handles the "1-2-3" situation
		terms.add(new Word(topic));

		completeTermList = new ArrayList<String>();
		mostCommon = new ArrayList<String>();
		try {
			bot = new MediaWikiBot("http://en.wikipedia.org/w/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Queries Wikipedia to find the specified page. Returns the article text in
	 * a specially denoted form.
	 * 
	 * @param term
	 *            The term to search for
	 * @return Specially denoted article text
	 */
	public String getArticle(String term) throws ActionException,
			ProcessException {
		SimpleArticle sa = new SimpleArticle(bot.readContent(term));
		pattern = Pattern.compile("#REDIRECT \\[\\[([A-Za-z -])*\\]\\]");
		matcher = pattern.matcher(sa.getText());
		while (matcher.find()) {
			String reDir = matcher.group();

			// Get rid of the brackets, do not need them anymore
			reDir = reDir.replaceAll("(\\[)*", "");
			reDir = reDir.replaceAll("(\\])*", "");
			System.out.println("Redirected source: " + reDir);
			reDir = reDir.replace("#REDIRECT ", "");
			term = reDir;

			getArticle(term);
		}
		return sa.getText();
	}

	/**
	 * Interprets the specially denoted article text and sets word definition
	 * and synonyms
	 */
	public void interpretArticle() throws ActionException, ProcessException {
		int value;
		String articletxt;
		for (Word term : terms) {
			// If there is a term to search by, search for it
			if (!term.getWord().equals("") || !(term.getWord() == null)) {
				// Retrieves the denoted text from the SimpleArticle created
				// from
				// the Wikipedia page

				articletxt = getArticle(term.getWord());

				// Gets all the "blue-linked" terms on the Wikipedia page
				// These are denoted by [[term that is linked]] in the article
				// text
				// The regex finds those links. Each link means there is another
				// Wikipedia
				// page associated with the term.
				Pattern pattern = Pattern
						.compile("(\\[{2})([A-Za-z: 0-9])*(\\]{2})");
				Matcher matcher = pattern.matcher(articletxt);

				// Continue until find all matches
				while (matcher.find()) {
					String finding = matcher.group();

					// Get rid of the brackets, do not need them anymore
					finding = finding.replaceAll("(\\[)*", "");
					finding = finding.replaceAll("(\\])*", "");

					// Add each finding to the list
					if (term.getWord().compareTo(finding) > 1
							|| term.getWord().compareTo(finding) < 0)
						completeTermList.add(finding);
				}

				// The hash map holds each unique term and the number of times
				// it is found
				// in the wikipedia article.
				HashMap<String, Integer> hm = new HashMap<String, Integer>();
				for (String keyword : completeTermList) {
					if (!hm.containsKey(keyword))
						hm.put(keyword, 1);
					else
						hm.put(keyword, hm.get(keyword) + 1);
				}

				for (String keyword : completeTermList) {

					// If it does not exist in the map, put it in
					if (!hm.containsKey(keyword)){
						hm.put(keyword, 1);
					}
					// Otherwise increment the number of times found by one
					else{
						hm.put(keyword, hm.get(keyword) + 1);
					}
				}

				// This next few lines finds the terms with the highest amount
				// of
				// occurrences within an article
				@SuppressWarnings("rawtypes")
				Set set = hm.entrySet();
				@SuppressWarnings("rawtypes")
				Iterator i = set.iterator();
				ArrayList<Integer> numFound = new ArrayList<Integer>();

				// Adds all the values in the map to numFound list
				while (i.hasNext()) {
					@SuppressWarnings("unchecked")
					Map.Entry<String, Integer> me = (Map.Entry<String, Integer>) i
							.next();
					numFound.add(me.getValue());
				}

				// Sorts the list, from low to high (index 0
				Collections.sort(numFound);

				if (numFound.size() > 0) {
					value = numFound.get(numFound.size() - 1);
					i = set.iterator();
					System.out.print("Term: " + term.getWord()
							+ "\tMost commonly found ");
					while (i.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, Integer> me = (Map.Entry<String, Integer>) i
								.next();
						if (me.getValue() == value) {
							String word = me.getKey();
							if (word != null && !mostCommon.contains(word)) {
								mostCommon.add(word.trim());
								break;
							}
							System.out.print(me.getKey() + " ");
							System.out.print("| ");
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		ArrayList<LuceneQuery> lucQuery = new ArrayList<LuceneQuery>();
		lucQuery.add(new LuceneQuery("897", "natural disaster\tAustralia",
				"e8w9r", (long) 389723));
		lucQuery.add(new LuceneQuery("908", "Jintao\tvisits\tUS", "084u",
				(long) 94084));
		WikipediaExpander we = new WikipediaExpander(lucQuery);
		lucQuery = we.runWiki();
		System.out.println(lucQuery);
		System.out.println("Most common: " + we.mostCommon.toString());
	}

	/**
	 * @return ArrayList<LuceneQuery> queries This method is used to run with
	 *         Lucene and STIRS. It utilizes the Wikipedia expander in order to
	 *         find relevant words regarding the given query. The list of
	 *         queries are split up and run through Wikipedia to find the most
	 *         common words that appear in the text. This will return an
	 *         expanded query and add it back into the ArrayList of LuceneQuery.
	 */
	public ArrayList<LuceneQuery> runWiki() {
		LuceneQuery luc = null;
		String whole = "";
		String indivWord = "";
		StringTokenizer token;
		
		// Splitting up the ArrayList of LuceneQuery to individual queries.
		for (int i = 0; i < queries.size(); i++) {
			luc = queries.get(i);
			whole = luc.getQuery();
			System.out.println("The whole query is: " + whole);
			token = new StringTokenizer(whole, "\t");
			while (token.hasMoreTokens()) {
				indivWord = token.nextToken();
				terms.add(new Word(indivWord));
			}

			try {
				interpretArticle();
			} catch (ActionException e) {
				e.printStackTrace();
			} catch (ProcessException e) {
				e.printStackTrace();
			}

			if (mostCommon.size() > 0) {
				String expandedQueryTerms = "";
				for (int j = 0; j < mostCommon.size(); j++) {
					expandedQueryTerms += ", " + mostCommon.get(j);
				}
				luc.setQuery(luc.getQuery().replace("\t", " ") + " "
						+ expandedQueryTerms);
				luc.setQuery(luc.getQuery().replace("\t", " ") + " "
						+ expandedQueryTerms.replace("\t", " "));
			}
			
			queries.set(i, luc);
			mostCommon.clear();
			terms.clear();
			completeTermList.clear();
		}
		return queries;
	}
}