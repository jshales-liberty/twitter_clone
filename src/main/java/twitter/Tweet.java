package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Tweet {
	private String tweet;
	private int createdById;
	
	public Tweet(String tweet, int createdById) {
		this.tweet = tweet;
		this.createdById = createdById;
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();
		
		try (Connection conn = DriverManager.getConnection(Twitter.DB_URL)){
			String sql = "INSERT INTO tweet(user_id, tweet, create_timestamp) VALUES(?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, this.createdById);
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

	public int getCreatedBy() {
		return createdById;
	}

	public void setCreatedById(String createdBy) {
		this.createdById = createdById;
	}
}
