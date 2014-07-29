package nistEvaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

//allNISTTopics ArrayList of NISTTopic
//NISTTopic topic #, arraylist of rel1, arraylist of rel2, arraylist of rel0
public class PrecisionScore {

	AllNISTTopics allNISTTopics = new AllNISTTopics();

	ArrayList<?> stirsOutput;
	ArrayList<?> precisionPerTopic;

	private static float PRECISIONBASE = 30;

	// FILEPATH FILE WIIL ALWAYS BE IN THE INSTALL LOCATION
	private String path = "C:/Users/Karl Appel/Desktop/FilePaths";

	private String trecPath;
	private String stirsPath;

	// default number of topics
	int numOfTopics = 49;

	public PrecisionScore() throws IOException {
		readPaths();
		readRelJudge();
		System.out.println("Number of topics: " + allNISTTopics.howMany());
		readSTIRSOutput();
	}

	public void readPaths() {
		FileInputStream f = null;

		try {
			f = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(f));
		String line;

		try {
			while ((line = br.readLine()) != null) {
				if (line.indexOf("trecPath:") != -1) {
					trecPath = line.substring(line.indexOf(": ") + 2);
				}

				if (line.indexOf("stirsPath:") != -1) {
					stirsPath = line.substring(line.indexOf(": ") + 2);
					System.out.println(stirsPath);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readRelJudge() throws IOException {

		ArrayList<String> relZero = new ArrayList<String>();
		ArrayList<String> relOne = new ArrayList<String>();
		ArrayList<String> relTwo = new ArrayList<String>();

		FileInputStream file = null;

		try {
			file = new FileInputStream(trecPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(file));
		String line;

		String previousTopic = null;
		NISTTopic currentTopic = new NISTTopic();
		// READ TO TOPIC 30 , THEN TO THE OTHER TOPICS 30-50

		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, " ");

			// GET TOPIC
			String topic = st.nextToken();
			st.nextToken();
			String tweetId = st.nextToken();
			String relevance = st.nextToken();

			// READ ONE TOPIC AT A TIME
			if (previousTopic != null && !previousTopic.equalsIgnoreCase(topic)) { // TOPIC
																					// CHANGE
				currentTopic.setRelevanceOne(relOne);
				currentTopic.setRelevanceTwo(relTwo);
				currentTopic.setNonRelevant(relZero);
				currentTopic.setTopicNum(previousTopic);
				allNISTTopics.addTopic(currentTopic);

				relOne = new ArrayList<String>();
				relTwo = new ArrayList<String>();
				relZero = new ArrayList<String>();
				currentTopic = new NISTTopic();
				currentTopic.setTopicNum(topic);
				previousTopic = topic;
			}
			
			if (previousTopic == null){
				previousTopic = topic;
			}

			if (relevance.equalsIgnoreCase("0")){
				relZero.add(tweetId.trim());
			}else if (relevance.equalsIgnoreCase("1")){
				relOne.add(tweetId.trim());
			}else if (relevance.equalsIgnoreCase("2")){
				relTwo.add(tweetId.trim());
			}
		}

		// ADD THE LAST ONE WHEN WE HAVE REACHED END OF FILE
		currentTopic.setRelevanceOne(relOne);
		currentTopic.setRelevanceTwo(relTwo);
		currentTopic.setNonRelevant(relZero);
		currentTopic.setTopicNum(previousTopic);
		allNISTTopics.addTopic(currentTopic);
	}

	public void readSTIRSOutput() throws IOException {
		float totalAveragePrecision = 0;
		float totalHighRelevantPrecision = 0;
		FileInputStream file = null;
		
		try {
			file = new FileInputStream(stirsPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(file));
		String line;

		String previousTopic = null;

		int countOne = 0;
		int countTwo = 0;

		NISTTopic nTopic = allNISTTopics.getNISTTopicByTopicNum("1");
		ArrayList<?> relOne = nTopic.getRelevanceOne();
		ArrayList<?> relTwo = nTopic.getRelevanceTwo();

		// READ TO END
		String topic = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().length() == 0){
				line = br.readLine();
			}
			
			if (line == null){
				break;
			}

			StringTokenizer st = new StringTokenizer(line, " ");
			System.out.println(line);
			
			// GET TOPIC
			topic = st.nextToken();
			topic = topic.substring(2);
			topic = topic.replaceFirst("0*", "");
			st.nextToken();
			String tweetId = st.nextToken();
			@SuppressWarnings("unused")
			String relevance = st.nextToken();

			if (previousTopic != null && !previousTopic.equalsIgnoreCase(topic)) { // TOPIC
																					// CHANGE

				System.out.println("Precision for topic num: " + previousTopic);
				float highRel = countTwo / PRECISIONBASE;
				System.out.println("High Rel Precision = " + highRel);
				float combRel = (countOne + countTwo) / PRECISIONBASE;
				totalAveragePrecision = totalAveragePrecision + combRel;
				totalHighRelevantPrecision = totalHighRelevantPrecision
						+ highRel;
				System.out.println("Combined Rel Precision = " + combRel);

				previousTopic = topic;
				countOne = 0;
				countTwo = 0;

				nTopic = allNISTTopics.getNISTTopicByTopicNum(topic);
				
				if (nTopic != null) {
					relOne = nTopic.getRelevanceOne();
					relTwo = nTopic.getRelevanceTwo();
					previousTopic = topic;
				}
			}

			if (previousTopic == null){
				previousTopic = topic;
			}
			
			if (relOne.contains(tweetId.trim())) {
				countOne++;
			} else if (relTwo.contains(tweetId.trim())) {
				countTwo++;
			}
		}

		// ADD THE LAST ONE WHEN WE HAVE REACHED END OF FILE
		System.out.println("Relevance one size: " + previousTopic + " "
				+ relOne.size());
		System.out.println();
		System.out.println("Precision for topic num: " + previousTopic);
		float highRel = countOne / PRECISIONBASE;
		System.out.println("High Rel Precision = " + highRel);
		float combRel = (countOne + countTwo) / PRECISIONBASE;
		totalAveragePrecision = totalAveragePrecision + combRel;
		System.out.println("Combined Rel Precision = " + combRel);
		System.out.println();
		System.out.println("Total Number of Totals = " + numOfTopics);
		System.out.println("Total Average Precision at 30: "
				+ totalAveragePrecision / numOfTopics);
		System.out.println("Total High Relevant Precision at 30: "
				+ totalHighRelevantPrecision / numOfTopics);

		Calendar calendar = new GregorianCalendar();
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int midDayNumber = calendar.get(Calendar.AM_PM);
		@SuppressWarnings("unused")
		String midDayString = "";

		String stringMinute = "";
		String stringHour = "";
		String stringSecond = "";

		if (minute < 10) {
			stringMinute = "0" + Integer.toString(minute);
			minute = Integer.parseInt(stringMinute);
		} else {
			stringMinute = Integer.toString(minute);
		}
		
		if (hour < 10) {
			stringHour = "0" + Integer.toString(hour);
			hour = Integer.parseInt(stringHour);
		} else {
			stringHour = Integer.toString(hour);
		}

		if (second < 10) {
			stringSecond = "0" + Integer.toString(second);
			second = Integer.parseInt(stringSecond);
		}else {
			stringSecond = Integer.toString(second);
		}

		if (midDayNumber == 0) {
			midDayString = "AM";
			if (hour == 0)
				hour = 12;
		}else {
			midDayString = "PM";
			if (hour == 0){
				hour = 12;
			}
		}
	}

	public static void main(String[] args) {
		try {
			@SuppressWarnings("unused")
			PrecisionScore precSc = new PrecisionScore();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}