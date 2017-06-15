package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class User {
	private int id;
	private String first_name;
	private String last_name;
	private String username;
	private String email_address;
	private String birth_date;
	private String bio;
	private String password;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email_address;
	}

	public void setEmail(String email_address) {
		this.email_address = email_address;
	}

	public String getBirth_date() {
		return birth_date;
	}

	public void setBirthday(String birth_date) {
		this.birth_date = birth_date;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public User(String first_name, String last_name, String username,
			String email_address, String birth_date, String bio,
			String password) {
		this.id = 0;
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.email_address = email_address;
		this.birth_date = birth_date;
		this.bio = bio;
		this.password = password;

		String url = "jdbc:sqlite:twitterclone.db";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();


		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt_create = conn.prepareStatement(
						"INSERT INTO user_info(first_name, last_name, bio, birth_date, email_address, username, password, create_timestamp) VALUES(?,?,?,?,?,?,?,?)");
				PreparedStatement pstmt_validate = conn.prepareStatement(
						"Select username from user_info where username = ? OR email_address = ?");) {
			pstmt_validate.setString(1, this.username);
			pstmt_validate.setString(2, this.email_address);
			int i = pstmt_validate.executeUpdate();
			System.out.println(i);
			if (i <= 0) {
				pstmt_create.setString(1, this.first_name);
				pstmt_create.setString(2, this.last_name);
				pstmt_create.setString(3, this.bio);
				pstmt_create.setString(4, this.birth_date);
				pstmt_create.setString(5, this.email_address);
				pstmt_create.setString(6, this.username);
				pstmt_create.setString(7, this.password);
				pstmt_create.setString(8, stringTimeStamp);
				pstmt_create.executeUpdate();
			} else {
				System.out.println("User already exists");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean checkCredentials() {
		String url = "jdbc:sqlite:twitterclone.db";
		
		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement(
						"Select username, password from user_info where username = ? and password = ?;");) {
			pstmt.setString(1, this.username);
			pstmt.setString(2, this.password);
			ResultSet rs = pstmt.executeQuery();
			return (rs.getString("username").equals(this.getUsername())
					&& rs.getString("password").equals(this.getPassword()));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}