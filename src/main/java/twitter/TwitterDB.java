package twitter;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class TwitterDB {
	public static final String DB_URL = "jdbc:sqlite:twitterclone.db";

	public static boolean checkExistence(User u) {
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt_validate = conn.prepareStatement(
						"Select count(*) as count from (Select username from user_info where username = ? OR email_address = ?)")) {
			pstmt_validate.setString(1, u.getUsername());
			pstmt_validate.setString(2, u.getEmail());
			ResultSet rs = pstmt_validate.executeQuery();
			if (rs.getInt("count") < 1) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public static User addUser(User u) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt_create = conn.prepareStatement(
						"INSERT INTO user_info(username, password, first_name, last_name, birth_date, email_address, bio, create_timestamp) VALUES(?,?,?,?,?,?,?,?)");) {
			PreparedStatement pstmt_pullID = conn.prepareStatement("Select user_id from user_info where username = ?;");
			pstmt_create.setString(3, u.getFirst_name());
			pstmt_create.setString(4, u.getLast_name());
			pstmt_create.setString(7, u.getBio());
			pstmt_create.setString(5, u.getBirth_date());
			pstmt_create.setString(6, u.getEmail());
			pstmt_create.setString(1, u.getUsername());
			pstmt_create.setString(2, u.getPassword());
			pstmt_create.setString(8, stringTimeStamp);
			pstmt_create.executeUpdate();
			pstmt_pullID.setString(1, u.getUsername());
			ResultSet rs = pstmt_pullID.executeQuery();
			u.setId(rs.getInt(1));
			return u;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return u;
		}
	}

	public static User checkCredentials(User u) {

		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn
						.prepareStatement("Select * from user_info where username = ? and password = ?;");) {
			pstmt.setString(1, u.getUsername());
			pstmt.setString(2, u.getPassword());
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				u.setId(rs.getInt("user_id"));
				u.setFirst_name(rs.getString("first_name"));
				u.setLast_name(rs.getString("last_name"));
				u.setBio(rs.getString("bio"));
				u.setBirthday(rs.getString("birth_date"));
				return u;
			} else {
				return null;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			e.getMessage();
			return null;
		}
	}
	
	public static User findUser(String userName){
		String selectSql = "SELECT * FROM user_info WHERE username = ?";

		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmtSelect = conn.prepareStatement(selectSql)) {

			pstmtSelect.setString(1, userName);
			ResultSet rs = pstmtSelect.executeQuery();
			if (rs.isBeforeFirst()) {
				User u;
				try {
					u = new User(rs.getString("first_name"), rs.getString("last_name"), 
									  rs.getString("username"), rs.getString("email_address"), 
									  rs.getString("bio"), rs.getString("birth_date")," ");
					u.setId(rs.getInt("user_id"));
					return u;
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			} else {
				return null;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static boolean createTweet(Tweet insertTweet){
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tweet(user_id, tweet, create_timestamp) VALUES(?,?,?)");){
			pstmt.setInt(1, insertTweet.getCreatedBy());
			pstmt.setString(2, insertTweet.getTweet());
			pstmt.setString(3, insertTweet.getCreateTimestamp());
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public static boolean followSomeone(int activeUserId, int userBeingFollowedId){
		String insertSql = "INSERT INTO follower(user_id, follows_user_id, create_timestamp) VALUES(?,?,?)";

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();

		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmtInsert = conn.prepareStatement(insertSql);) {
			
			pstmtInsert.setInt(1, activeUserId);				
			pstmtInsert.setInt(2, userBeingFollowedId);
			pstmtInsert.setString(3, stringTimeStamp);
			pstmtInsert.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false; 
		}
	}
	
public static ArrayList<Tweet> getTweetList(int userId) {
		
		ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();
		
		String sql = "SELECT t.tweet 'Follows_Tweet', "
						+ "t.tweet_id 'Tweet_Id', "
				  		+ "t.user_id 'Follows_User_Id', "
				  		+ "ui.username 'Follows_User_Name', "
				  		+ "t.create_timestamp 'Tweet_Timestamp' "
				  	+ "FROM TWEET t "
				  	+ "Left Join user_info ui on ui.user_id = t.user_id "
				  	+ "where t.user_id in (select distinct FOLLOWS_USER_ID from FOLLOWER "
				  	+ "where USER_ID = ?) " 
				  	+ "union "
				  	+ "select t.tweet  'Follows_Tweet', "
				  		+ "t.tweet_id 'Tweet_Id', "
				  		+ "t.user_id 'Follows_User_Id', "
				  		+ "ui.username 'Follows_User_Name', "
				  		+ "t.create_timestamp 'Tweet_TimeStamp' " 
				  	+ "FROM tweet t "
				  	+ "Left Join user_info ui on ui.user_id = t.user_id "
				  	+ "where t.user_id = ? "
				  	+ "ORDER BY t.create_timestamp DESC "
				  	+ "LIMIT 100 ";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			
			pstmt.setInt(1, userId);
			pstmt.setInt(2, userId);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Tweet tweet = new Tweet(
						rs.getString("Follows_Tweet"),
						rs.getInt("Tweet_Id"),
						rs.getInt("Follows_User_Id"), 
						rs.getString("Follows_User_Name"), 
						rs.getString("Tweet_Timestamp"));
				tweetsList.add(tweet);
			}
			return tweetsList;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return tweetsList;
		}
	}

	public static ArrayList<String> getPopularTweeters(int userId) {
		ArrayList<String> popularTweeters = new ArrayList<String>();
		String sql = "SELECT * FROM user_info WHERE NOT user_id IN (SELECT follows_user_id FROM follower WHERE user_id = ?)	AND NOT user_id = ? LIMIT 8";
	
		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			
			pstmt.setInt(1, userId);
			pstmt.setInt(2, userId);
	
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				popularTweeters.add(rs.getString("username"));
			}
			return popularTweeters;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return popularTweeters;
		}
	}
	
	public static boolean unfollowSomeone(int userId, int followsUserId) {
		String sql = "DELETE FROM follower WHERE user_id = ? and follows_user_id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			
			pstmt.setInt(1, userId);
			pstmt.setInt(2, followsUserId);
	
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public static boolean insertLike(int userId, int tweetId, int tweetUserId) {
		String sql = "INSERT INTO likes(user_id, tweet_id, tweet_user_id, create_timestamp) VALUES(?,?,?,?)";

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();

		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmtInsert = conn.prepareStatement(sql);) {
			
			pstmtInsert.setInt(1, userId);				
			pstmtInsert.setInt(2, tweetId);
			pstmtInsert.setInt(3, tweetUserId);
			pstmtInsert.setString(4, stringTimeStamp);
			pstmtInsert.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false; 
		}

	}
}