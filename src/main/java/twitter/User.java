package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
	private int id;
	private String first_name;
	private String last_name;
	private String username;
	private String email_address;
	private String birth_date;
	private String bio;
	private String password;
	private String image;

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

	public User(String first_name, String last_name, String username, String email_address, String birth_date, String bio, String password, String image) {
		this.id = 0;
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.email_address = email_address;
		this.birth_date = birth_date;
		this.bio = bio;
		this.password = password;
		this.image = image;
		try {
			String url = "jdbc:sqlite:twitterclone.db";
			Connection conn = DriverManager.getConnection(url);
			String sql = "INSERT INTO user_info(first_name, last_name, bio, birth_date, email_address, username) VALUES(?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql); 
	            pstmt.setString(1, this.first_name);
	            pstmt.setString(2, this.last_name);
	            pstmt.setString(3, this.bio);
	            pstmt.setString(4, this.birth_date);
	            pstmt.setString(5, this.email_address);
	            pstmt.setString(6, this.username);
	            pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean checkCredentials(String username, String password) {
		try {
			String url = "jdbc:sqlite:twitterclone.db";
			Connection conn = DriverManager.getConnection(url);
			;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"Select username, password from user_info where username = " + username + "and password = " + password + ";");
			return (rs.getString("username") == username && rs.getString("password") == password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}
