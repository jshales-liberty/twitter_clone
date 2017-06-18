package twitter;

import static spark.Spark.get;
import static spark.Spark.post;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import static spark.Spark.port;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Twitter {

	public static final String DB_URL = "jdbc:sqlite:twitterclone.db"; 

	public static void main(String[] args) {

		port(2613);

		get("/login", (request, response) -> {
			return createLoginHTML();
		});

		post("/login", (request, response) -> {
			String body = request.body();
			Gson gson = new Gson();
			User c = gson.fromJson(body, User.class);
			c=TwitterDB.checkCredentials(c);
			if (c!=null) {
				request.session().attribute("user", c);
				return true;
			} else {
				return false;
			}
		});

		get("/createUser", (request, response) -> {
			return createNewUserHTML();
		});

		post("/createUser", (request, response) -> {
			String body = request.body();
			Gson gson = new Gson();
			User c = gson.fromJson(body, User.class);
			if (!TwitterDB.checkExistence(c))
				{c=TwitterDB.addUser(c);
				request.session().attribute("user", c);
				return true;}
			 else {
				return false;
			} });

		get("/createTweetHTML", (request, response) -> {
			if(((User) request.session().attribute("user")) == null){
				return createLoginHTML();
			}else {
				return createTweetPageHTML();
			}

		});

		get("/tweetList", (request, response) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(getTweetList(((User) request.session().attribute("user")).getId()));
		});

		get("/popularTweeters", (request, response) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(
					getPopularTweeters(((User) request.session().attribute("user")).getId()));
		});

		post("/submitTweet", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            Tweet tweet = gson.fromJson(body, Tweet.class);
            //System.out.println(req.session().attribute("user_id").toString());
            new Tweet(tweet.getTweet(), (((User) req.session().attribute("user")).getId()), ((User) req.session().attribute("user")).getUsername());
            
            return "jsonpost";
        });	
		
		post("/submitFollow", (req, res) -> {
			String body = req.body();

			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(body).getAsJsonObject();

			String selectSql = "SELECT user_id FROM user_info WHERE username = ?";
			String insertSql = "INSERT INTO follower(user_id, follows_user_id, create_timestamp) VALUES(?,?,?)";

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String stringTimeStamp = timestamp.toString();

			try (Connection conn = DriverManager.getConnection(DB_URL);
					PreparedStatement pstmtSelect = conn
							.prepareStatement(selectSql);
					PreparedStatement pstmtInsert = conn
							.prepareStatement(insertSql);) {

				pstmtSelect.setString(1, obj.get("follows").getAsString());
				ResultSet rs = pstmtSelect.executeQuery();

				pstmtInsert.setInt(1, req.session().attribute("user_id"));
				pstmtInsert.setInt(2, rs.getInt("user_id"));
				pstmtInsert.setString(3, stringTimeStamp);
				pstmtInsert.executeUpdate();

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			return req;
		});

		get("/logoff", (req, res) -> {
			return createlogOffPageHTML(((User) req.session().attribute("user")).getUsername());
		});

	}

	public static String createLoginHTML() {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/login.jTwig");
		JtwigModel model = JtwigModel.newModel();

		return template.render(model);
	}

	public static String createNewUserHTML() {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/newUser.jTwig");
		JtwigModel model = JtwigModel.newModel();

		return template.render(model);
	}

	public static String createTweetPageHTML() {
		
		
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/tweets.jTwig");
		JtwigModel model = JtwigModel.newModel();

		return template.render(model);
	}

	public static ArrayList<String> getPopularTweeters(int userId) {
		String url = "jdbc:sqlite:twitterclone.db";
		ArrayList<String> popularTweeters = new ArrayList<String>();
		String sql = "SELECT * FROM user_info WHERE NOT user_id IN (SELECT follows_user_id FROM follower WHERE user_id = ?)	AND NOT user_id = ? LIMIT 3";

		try (Connection conn = DriverManager.getConnection(url);
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
	
	public static ArrayList<Tweet> getTweetList(int userId) {
		
		ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();
		
		String sql = "SELECT t.TWEET 'Follows_Tweet', "
				  		+ "t.user_id 'Follows_User_Id', "
				  		+ "ui.username 'Follows_User_Name', "
				  		+ "t.create_timestamp 'Tweet_Timestamp' "
				  	+ "FROM TWEET t "
				  	+ "Left Join user_info ui on ui.user_id = t.user_id "
				  	+ "where t.user_id in (select distinct FOLLOWS_USER_ID from FOLLOWER "
				  	+ "where USER_ID = ?) " 
				  	+ "union "
				  	+ "select t.TWEET  'Follows_Tweet', "
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
				Tweet tweet = new Tweet(rs.getString("Follows_Tweet"), 
						rs.getInt("Follows_User_Id"), 
						rs.getString("Follows_User_Name"), 
						rs.getString("Tweet_Timestamp"));
				System.out.println(tweet.getTweet());
				tweetsList.add(tweet);
			}
			return tweetsList;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return tweetsList;
		}
	}
	
	private static String createlogOffPageHTML(String username) {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/logOff.jTwig");
		JtwigModel model = JtwigModel.newModel().with("username", username);

		return template.render(model);
	}
}