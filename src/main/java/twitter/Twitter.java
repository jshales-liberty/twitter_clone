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

public class Twitter {

	private static String userId;

	public static void main(String[] args) {

		port(2613);

		get("/login", (request, response) -> {
			return createLoginHTML();
		});

		post("/login", (request, response) -> {
			String body = request.body();
			Gson gson = new Gson();
			User c = gson.fromJson(body, User.class);
			int result = c.checkCredentials();
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

		// post("/createNewUser", (request, response) -> {
		// new User(request.queryParams("firstName"),
		// request.queryParams("lastName"),
		// request.queryParams("username"),
		// request.queryParams("birth_date"),
		// request.queryParams("email"),
		// request.queryParams("bio"),
		// request.queryParams("password"));
		// System.out.println("create new user is used");
		// System.out.println(request.queryParams("lastName"));
		// return createLoginHTML();
		// });

		get("/popularTweeters", (request, response) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			;
			return gson.toJson(getPopularTweeters());
		});

		post("/createNewUser", (request, response) -> {
			new User(request.queryParams("firstName"),
					request.queryParams("lastName"),
					request.queryParams("username"),
					request.queryParams("birth_date"),
					request.queryParams("email"), request.queryParams("bio"),
					request.queryParams("password"));
			return createLoginHTML();
		});

		get("/createTweetHTML", (request, response) -> {
			// remove line below
			userId = "LB33";
			return createTweetPageHTML(userId);

		});

		post("/createUser", (request, response) -> {
			new User(request.queryParams("firstName"),
					request.queryParams("lastName"),
					request.queryParams("username"),
					request.queryParams("birth_date"),
					request.queryParams("email"), 
					request.queryParams("bio"),
					request.queryParams("password"));
			request.session().attribute("username",
					request.queryParams("username"));
			userId = request.session().attribute("username");

			System.out.println(userId);
			System.out.println("create user is used");

			return createTweetPageHTML(userId);
		});

		post("/submitTweet", (req, res) -> {
			String body = req.body();
			Gson gson = new Gson();
			Tweet tweet = gson.fromJson(body, Tweet.class);

			new Tweet(tweet.getTweet(), "1");

			return "jsonpost";
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

	public static String createTweetPageHTML(String userId) {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/tweets.jTwig");
		JtwigModel model = JtwigModel.newModel();

		return template.render(model);
	}

	public static ArrayList<String> getPopularTweeters() {
		String url = "jdbc:sqlite:twitterclone.db";
		ArrayList<String> popularTweeters = new ArrayList<String>();

		try (Connection conn = DriverManager.getConnection(url);
				Statement stmt = conn.createStatement();) {
			String sql = "SELECT username FROM user_info LIMIT 3;";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				popularTweeters.add(rs.getString("username"));
				System.out.println(popularTweeters.size());
			}
			return popularTweeters;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return popularTweeters;
		}
	}

}