package twitter;

import java.sql.Timestamp;

public class Tweet {
	private String tweet;
	private int createdById;
	private String createdByName;
	private String createTimestamp;
	
	public Tweet(String tweet, int createdById, String createdByName) {
		this.tweet = tweet;
		this.createdById = createdById;
		this.setCreatedByName(createdByName);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();
		this.setCreateTimestamp(stringTimeStamp);
		
		
	}
	
	public Tweet(String tweet, int createdById, String createdByName, String timestamp) {
		this.tweet = tweet;
		this.createdById = createdById;
		this.setCreatedByName(createdByName);
		this.setCreateTimestamp(timestamp);
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public int getCreatedBy() {
		return createdById;
	}

	public void setCreatedById(int createdBy) {
		this.createdById = createdBy;
	}

	public String getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(String createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}
}
