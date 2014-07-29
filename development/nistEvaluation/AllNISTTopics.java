package nistEvaluation;

import java.util.ArrayList;

public class AllNISTTopics {
	//ALL WILL CONTAIN TWEET IDS
	ArrayList<NISTTopic> topics = new ArrayList<NISTTopic>();
	
	public NISTTopic getNISTTopicByTopicNum(String topicID){
		for (int i=0;i<topics.size();i++){
			NISTTopic nistT = topics.get(i);
			String topicNum = nistT.getTopicNum();
			if (topicNum.equalsIgnoreCase(topicID))
				return nistT;
		}
		return null;
	}
	
	public ArrayList<?> getNonRelsByTopicNum(String topicID){
		for (int i=0;i<topics.size();i++){
			NISTTopic nistT = topics.get(i);
			String topicNum = nistT.getTopicNum();
			if (topicNum.equalsIgnoreCase(topicID))
				return nistT.getNonRelevant();
		}
		return null;	
	}
	
	public ArrayList<?> getOneRelsByTopicNum(String topicID){
		for (int i=0;i<topics.size();i++){
			NISTTopic nistT = topics.get(i);
			String topicNum = nistT.getTopicNum();
			if (topicNum.equalsIgnoreCase(topicID))
				return nistT.getRelevanceOne();
		}
		return null;	
	}
	
	public ArrayList<?> getTwoRelsByTopicNum(String topicID){
		for (int i=0;i<topics.size();i++){
			NISTTopic nistT = topics.get(i);
			String topicNum = nistT.getTopicNum();
			if (topicNum.equalsIgnoreCase(topicID))
				return nistT.getRelevanceTwo();
		}
		return null;	
	}

	public ArrayList<NISTTopic> getTopics() {
		return topics;
	}

	public void setTopics(ArrayList<NISTTopic> topics) {
		this.topics = topics;
	}

	public int howMany() {
		return topics.size();
	}
	
	public void addTopic(NISTTopic topicToAdd){
		topics.add(topicToAdd);
	}
}