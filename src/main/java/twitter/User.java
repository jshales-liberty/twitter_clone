package twitter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
		password += "toaster";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes());
			String digest = new String(md.digest());
			return digest;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void setPassword(String password) {
		password += "toaster";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes());
			String digest = new String(md.digest());
			this.password = digest;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
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
			String password) throws NoSuchAlgorithmException {
		this.id = 0;
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.email_address = email_address;
		this.birth_date = birth_date;
		this.bio = bio;
		this.setPassword(password);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String stringTimeStamp = timestamp.toString();

		try (Connection conn = DriverManager.getConnection(Twitter.DB_URL);
				PreparedStatement pstmt_validate = conn.prepareStatement(
						"Select count(*) as count from (Select username from user_info where username = ? OR email_address = ?)");
				PreparedStatement pstmt_create = conn.prepareStatement(
						"INSERT INTO user_info(username, password, first_name, last_name, birth_date, email_address, bio, create_timestamp) VALUES(?,?,?,?,?,?,?,?)");) {
			pstmt_validate.setString(1, this.getUsername());
			pstmt_validate.setString(2, this.getEmail());
			ResultSet rs = pstmt_validate.executeQuery();
			int i = rs.getInt("count");
			if (i < 1) {
				pstmt_create.setString(3, this.getFirst_name());
				pstmt_create.setString(4, this.getLast_name());
				pstmt_create.setString(7, this.getBio());
				pstmt_create.setString(5, this.getBirth_date());
				pstmt_create.setString(6, this.getEmail());
				pstmt_create.setString(1, this.getUsername());
				pstmt_create.setString(2, this.getPassword());
				pstmt_create.setString(8, stringTimeStamp);
				pstmt_create.executeUpdate();
				this.setId(this.checkCredentials());
			} else {
				this.setId(-1);
				System.out.println("User already exists");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int checkCredentials() {

		try (Connection conn = DriverManager.getConnection(Twitter.DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(
						"Select username, password, user_id from user_info where username = ? and password = ?;");) {
			pstmt.setString(1, this.username);
			pstmt.setString(2, this.getPassword());
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				String result = rs.getString("user_id");
				return Integer.parseInt(result);
			} else {
				return -1; // no user_id
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}
}