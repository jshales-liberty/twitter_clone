package twitter;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
		try (Connection conn = DriverManager.getConnection(Twitter.DB_URL);
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
}