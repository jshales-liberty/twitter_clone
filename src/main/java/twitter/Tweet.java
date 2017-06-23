package twitter;

import java.sql.Timestamp;

public class Tweet {
	private int tweetId;
	private String tweet;
	private int createdById;
	private String createdByName;
	private String createTimestamp;
	private String liked;
	private int like_count;

	public Tweet(String tweet, int createdById, String createdByName) {
		this.tweet = tweet;
		this.createdById = createdById;
		this.setCreatedByName(createdByName);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();
		this.setCreateTimestamp(stringTimeStamp);

	}

	public Tweet(String tweet, int tweetId, int createdById,
			String createdByName, String timestamp, String liked,
			int like_count) {
		this.tweet = tweet;
		this.tweetId = tweetId;
		this.createdById = createdById;
		this.setCreatedByName(createdByName);
		this.setCreateTimestamp(timestamp);
		this.liked = liked;
		this.setLike_count(like_count);
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

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}
}
