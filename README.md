STIRS2012
=========

If you're looking at this, you're probably working for Siena College and working for the TREC Microblog Track. Congrats!

In 2014, it was our third year (previous years: 2011 & 2012) working on STIRS  - Siena's Information Retrieval System. By this time, the original researchers had all graduated and the current researchers were having a harder time working with the old code. The new code can be found under the main STIRS repository, but here is all the original code that that code originated from.

A lot of this doesn't work. It's simply here if you need to go back and see how we did it before STIRS was remodeled to fit in with the API. We've listed below what each of the projects do (at least, what they were supposed to do) and a general description on how to run the program (from Eclipse), but if you're looking for full documentation, check out the README file for the STIRS repository.

development: Whole project run from STIRS (STIRS.jar). Results outputs the results for each run.
	-Stirs: The “main” class from which STIRS is run.
	-Results: What we submit to TREC is the results for each run (which does different combos of the four modules). This takes those runs and outputs the results for each of them, in the format for TREC. The runs include the tweet relevance scores used for figure out the results (scores given to each tweet for each topic).

filteringTaskIndexer: Used for the 2012 Filtering Task.
	-TweetIDComparable: used like the Tweet class, but includes formatting and comparing for tweets; used by TwitterIndexer to sort tweets (almost repeats of TweetIDComparator and TweetScoreComparator)

GoogleArchive: Used for Query Expansion. Basically "typed" the query (topic) into google and scraped the top five pages to add onto the query (and re-submit it to get more tweets) NOT USED IN 2012

nistEvaluation: Used for evaluating precision (amount of relevant tweets retrieved/tweets retrieved) with each topic for any of the runs  STAND ALONE TREC returned a precision score based on each of the runs, and we created a precision test in order to test different combo styled-runs
	-ALLNISTTopics: a “data structure” class; used to see how much of, for each topic, we considered relevant vs. what NIST actually said was relevant
	-NISTTopic: a “data structure” class; returns an ArrayList. For each topic, it contains the non-relevant, relevant, and highly relevant tweets
	-PrecisionScore: returns a score, for each run, of how well we did compared to NIST (comparing, per topic, how many tweets were scored correctly)

queryBreakdown: Used with Lucene for POS tagging (splitting query)
	-DivideQuery: Was previously used by Karl and found to not be working and was unattached from the main program. Originally done after a module was run, it would go through each tweet and add on-to the tweet’s score based on certain parts of speech; found not to actually have any real effect (adds on to the score weren’t found to actually find more relevant tweets) not used in 2012

QueryExpansion: Query Expansion, worked with tm2 (stand-alone, but run before STIRS to adjust topic queries) (external query expansion, not used in 2012)

STIRS.Lucene: Lucene stuff (indexing all tweets)
	-LuceneSearch: For each topic, goes through the Lucene index and finds the (scored) relevant tweets
	-MassIndexer: Used for filteringTask (NOT NEEDED)
	-POSTagger: Used with DivideQuery; not used in final produce in 2012.
	-RankedTweetList: A “data structure” used to hold the tweet information in an ArrayList (found in Tweet class)
	-rescoreTweets: increases the score of the tweet if it has more percentage of the words in the query compared to other tweets
	-Tweet: Gathers the tweet information (tweetID, status, date, relevance score from Lucene) for each tweet
	-TweetIDComparator: Compares two tweets based on tweetID; used by other classes to be able to sort tweets ordered by the tweetID (basically in order)
	-TweetScoreComparator: Compares two tweets based on score; used by other classes to be able to sort tweets ordered by score
	-TwitterIndexer: creates Lucene's index of tweets from the corpus

STIRS.QueryProcessor: Used for processing each tweet to Lucene format
	-LuceneQuery: A “data structure” used to hold the new query format for Lucene
	-QueryConverter: Does the actual conversion from the query format given by TREC to the format needed by Lucene; LuceneQuery is the “data structure” used to hold this new query format
	-QueryExpansion: internal query expansion; stand-alone in 2012; used to take slang found in topic queries and convert them to normal format
	-QueryProcessor: creates Lucene objects (for each topic query) and “sanitizes” the query (trims, toLowerCase etc); these objects are used for when searching the index
	-SlangDictionary: Used with QueryExpansion; looks up the given slang term and returns the English term
	-TwitterSearch: not used in 2012; would go into the index for each topic and find those tweets and return them

stirsx.tm1: Link-crawling stuff
	-GetLongURL: Gets the long form of a url DELETE
	-Module: “superclass” used by the three modules (TM2, RankedJoin and TM1); all three used both LuceneQuery and RankedTweetList
	-RankedJoin: “main” class for the RankedJoin (tm = 2 in STIRS). Combined the two lists with the most relevant tweets.
	-RatingScript:  find precision, made by Karl, DELETE
	-TM1URLOnly: “main” class for the TM1 module (Link Crawling). Uses the various classes to determine relevance based off the URLs included in the tween
	-TwitterIndexerUrls: information from the linked pages is given to it; this class indexed the words (info) found in these links for each tweet; possibly check for filtering task only; stand-alone
	-UrlContentRetrieval: Retrieves the content from the URLs in the tweets and saves it in a file with that content and tweetID; used with TwitterIndexerUrls
	-URLListGen: created a file of tweetIDs with their URLs
	-UrlObject: ??? made by David Purcell
	-URLStats: outputs how many document types were used in tweets; not used in 2012?

stirsx.util: Miscellaneous - used in all Twitter modules (TM)
	-DecisionMaker: used to determine whether to retrieve the tweet or not for the topic? Used in Filtering task only
	-Log: records errors that happen (supposed to be used in many classes, to keep track of errors, but was only used originally in RankedJoin)
	-MakeUrlIndexes: creates a separate Lucene Index of all those tweets between certain time frames
	-Normalizer: NIST requires a certain format for its scores/relevance, so it converts it from the Lucene format to NIST format. Done somewhere in the process between Lucene giving a score and the other modules “updating” each tweets relevance score.
	-POSTagger: same as POSTagger from STIRS.Lucene; exact copy should have been referred to other class
	-QueryFormatter: original format with multiple lines for query; turns it from multiple lines to one single line; stand alone?
	-renameTagger: in the filtering task only, DELETE; used to change a tag
	-TimeFilter: To each query topic, there is a certain time frame to find those relevant tweets for the topic within the collected tweets. In order to only search those tweets required for that topic, a copy of the tweet collection was cut down to the relevant time frame to look for tweets for the topic. (Note: This causes a problem in bias – Lucene’s scoring system counted in how much words appeared within the index, and if a number of those words were outside the time frame for that topic, it could create a bias (an increased score) towards those few words found within the time frame; this was bias because those tweets weren’t necessary more relevant, and was based off data no longer used for that query topic).

tm2: worked with weka
	-ARFFCreator: takes a CSV file and creates an ARFF; weka used ARFF
	-CSVParser: takes a CSV file and parses it (gets the info from each column
	-Listorious: ??? not used in 2012 DELETE
	-ParseWekaTweetList: a “data structure” class that holds all the tweets with their attributes, in the form of a WekaTweet, in an ArrayList 
	-ProcessAttributes: finds the attributes for each tweet; returns a new WekaTweet
	-RetweetRank: used with Listorious; DELETE
	-TM2: “main” class for the TM2 module (WEKA/Machine Learning). Basically ranks the tweets following the WEKA model.
	-TweetData: returns the length of a tweet and if it contains a URL; possibly DELETE
	-User: stores user-related stuff (username, listCount, etc); used originally for Weka attributes, not used in 2012, DELETE
	-UserDatabase: database of User(s); DELETE
	-Weka: took in a decision tree model to predict the relevance for each tweet; updates the relevance in the ArrayList of WekaTweets
	-WekaTweet:  a “data structure” class that holds the information for a tweet; holds attributes for Weka (numbers, emotes, numbers, etc)
