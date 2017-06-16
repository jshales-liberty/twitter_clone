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

	final static String DB_URL = "jdbc:sqlite:twitterclone.db";

	public static void main(String[] args) {

		port(2613);

		get("/login", (request, response) -> {
			return createLoginHTML();
		});

		post("/login", (request, response) -> {
			String body = request.body();
			Gson gson = new Gson();
			User c = gson.fromJson(body, User.class);
			int result = c.checkCredentials(); //returns user id

			if (result == -1) {
				return false;
			} else {
				request.session().attribute("username", c.getUsername());
				request.session().attribute("user_id", result);
				return true;
			}
		});

		get("/createUser", (request, response) -> {
			return createNewUserHTML();
		});

		post("/createUser", (request, response) -> {
			String body = request.body();
			Gson gson = new Gson();
			User c = gson.fromJson(body, User.class);
			User d = new User(c.getFirst_name(), c.getLast_name(),
					c.getUsername(), c.getEmail(), c.getBirth_date(),
					c.getBio(), c.getPassword());
			if (d.getId() == -1) {
				return false;
			} else {
				request.session().attribute("username", d.getUsername());
				request.session().attribute("user_id", d.checkCredentials());
				return true;
			}
		});

		get("/popularTweeters", (request, response) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(
					getPopularTweeters(request.session().attribute("user_id")));
		});

		get("/createTweetHTML", (request, response) -> {
			return createTweetPageHTML();

		});

		post("/submitTweet", (req, res) -> {
			String body = req.body();
			Gson gson = new Gson();
			Tweet tweet = gson.fromJson(body, Tweet.class);
			System.out.println(req.session().attribute("user_id").toString());
			new Tweet(tweet.getTweet(), (req.session().attribute("user_id")));

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
			return createlogOffPageHTML(req.session().attribute("username"));
		});

	}

	private static String createlogOffPageHTML(String username) {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/logOff.jTwig");
		JtwigModel model = JtwigModel.newModel().with("username", username);

		return template.render(model);
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
				Statement stmt = conn.createStatement();) {
			// String sql = "SELECT username FROM user_info LIMIT 3;";

			PreparedStatement pstmt = conn.prepareStatement(sql);
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
}