package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
	private int id;
	private String first_name;
	private String last_name;
	private String handle;
	private String email;
	private String birthday;
	private String bio;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public User(int id, String first_name, String last_name, String handle, String email, String birthday, String bio) {
		super();
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.handle = handle;
		this.email = email;
		this.birthday = birthday;
		this.bio = bio;
	}

	public boolean checkCredentials(String handle, String password)
	{try{
		  String url = "jdbc:sqlite:tests.db";
	        Connection conn = null;
	        try {
	            conn = DriverManager.getConnection(url);
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        Statement stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery("Select handle, password from users where handle = " + handle + 
            		"and password = " + password+ ";");
            return (rs.getString("handle") == handle && rs.getString("password") == password);
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
		
	}
	
}
