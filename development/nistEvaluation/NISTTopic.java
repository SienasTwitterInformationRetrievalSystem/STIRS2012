package nistEvaluation;

import java.util.ArrayList;

public class NISTTopic {
	//ALL WILL CONTAIN TWEET IDS
	ArrayList<?> relevanceOne;
	ArrayList<?> relevanceTwo;
	ArrayList<?> nonRelevant;
	
	String topicNum;
	
	public ArrayList<?> getRelevanceOne() {
		return relevanceOne;
	}
	public void setRelevanceOne(ArrayList<?> relevanceOne) {
		this.relevanceOne = relevanceOne;
	}
	public ArrayList<?> getRelevanceTwo() {
		return relevanceTwo;
	}
	public void setRelevanceTwo(ArrayList<?> relevanceTwo) {
		this.relevanceTwo = relevanceTwo;
	}
	public ArrayList<?> getNonRelevant() {
		return nonRelevant;
	}
	public void setNonRelevant(ArrayList<?> nonRelevant) {
		this.nonRelevant = nonRelevant;
	}
	public String getTopicNum() {
		return topicNum;
	}
	public void setTopicNum(String topicNum) {
		this.topicNum = topicNum;
	}
	
	public NISTTopic(ArrayList<?> relevanceOne, ArrayList<?> relevanceTwo,
			ArrayList<?> nonRelevant, String topicNum) {
		super();
		this.relevanceOne = relevanceOne;
		this.relevanceTwo = relevanceTwo;
		this.nonRelevant = nonRelevant;
		this.topicNum = topicNum;
	}
	
	public NISTTopic() {}
}