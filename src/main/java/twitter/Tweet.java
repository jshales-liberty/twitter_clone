package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Tweet {
	private String tweet;
	private String createdBy;
	
	public Tweet(String tweet, String createdBy) {
		this.tweet = tweet;
		this.createdBy = createdBy;
		
		String url = "jdbc:sqlite:twitterclone.db";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();
		
		try (Connection conn = DriverManager.getConnection(url)){
			String sql = "INSERT INTO tweet(user_id, tweet, create_timestamp) VALUES(?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, this.createdBy);
			pstmt.setString(2, this.tweet);
			pstmt.setString(3, stringTimeStamp);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
