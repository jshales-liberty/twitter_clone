package twitter;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.ArrayList;

import static spark.Spark.port;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Twitter {

	public static void main(String[] args) {

		port(2613);

		get("/login", (request, response) -> {
			return createLoginHTML();
		});

		post("/login", (request, response) -> {
			String body = request.body();
			Gson gson = new Gson();
			User c = gson.fromJson(body, User.class);
			c = TwitterDB.checkCredentials(c);
			if (c != null) {
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
			if (!TwitterDB.checkExistence(c)) {
				c = TwitterDB.addUser(c);
				request.session().attribute("user", c);
				return true;
			} else {
				return false;
			}
		});

		get("/createTweetHTML", (request, response) -> {
			if (((User) request.session().attribute("user")) == null) {
				return createLoginHTML();
			} else {
				return createTweetPageHTML(
						((User) request.session().attribute("user"))
								.getUsername());
			}
		});

		get("/tweetList", (request, response) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(TwitterDB.getTweetList(
					((User) request.session().attribute("user")).getId()));
		});

		get("/popularTweeters", (request, response) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(TwitterDB.getPopularTweeters(
					((User) request.session().attribute("user")).getId()));
		});

		post("/submitTweet", (req, res) -> {
			String body = req.body();
			Gson gson = new Gson();
			Tweet tweet = gson.fromJson(body, Tweet.class);
			TwitterDB.createTweet(new Tweet(tweet.getTweet(),
					(((User) req.session().attribute("user")).getId()),
					((User) req.session().attribute("user")).getUsername()));

			return "jsonpost";
		});

		post("/submitFollow", (req, res) -> {
			String body = req.body();

			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(body).getAsJsonObject();

			User userBeingFollowed = TwitterDB
					.findUser(obj.get("follows").getAsString());
			return TwitterDB.followSomeone(
					((User) req.session().attribute("user")).getId(),
					userBeingFollowed.getId());
		});

		get("/logoff", (req, res) -> {
			String userName = ((User) req.session().attribute("user"))
					.getUsername();
			req.session().removeAttribute("user");
			return createlogOffPageHTML(userName);
		});

		get("/userActivity", (req, res) -> {
			String user = req.queryParams("UserName");
			user = user.replace("\'", "");
			User newuser = TwitterDB.findUser(user);
			if (newuser == null) {
				return createTweetPageHTML(
						((User) req.session().attribute("user")).getUsername());
			} else {
				int id = newuser.getId();
				return createUserPage(user, TwitterDB.getUserTweets(id),
						TwitterDB.getUserFollows(id));
			}
		});

		get("/userTweets", (req, res) -> {
			String user = req.queryParams("UserName");
			int id = TwitterDB.getUserId(user);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(TwitterDB.getUserTweets(id));
		});
		
		post("/submitUnfollow", (req, res) -> {
			String body = req.body();

			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(body).getAsJsonObject();

			User userBeingUnfollowed = TwitterDB
					.findUser(obj.get("follows").getAsString());
			return TwitterDB.unfollowSomeone(
					((User) req.session().attribute("user")).getId(),
					userBeingUnfollowed.getId());

		});
		
		post("/submitLike", (req, res) -> {
			String body = req.body();

			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(body).getAsJsonObject();

			return TwitterDB.submitLike(
					((User) req.session().attribute("user")).getId(),
					obj.get("tweetId").getAsInt());
		});
		
		post("/isTweetLiked", (req, res) -> {
			String body = req.body();
			
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(body).getAsJsonObject();
			
			return TwitterDB.isTweetLiked(((User) req.session().attribute("user")).getId(),
					obj.get("tweetId").getAsInt());
		});
		
		post("/submitUnlike", (req, res) -> {
			String body = req.body();

			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(body).getAsJsonObject();

			return TwitterDB.submitUnlike(((User) req.session().attribute("user")).getId(),
					obj.get("tweetId").getAsInt());
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

	public static String createUserPage(String username,
			ArrayList<Tweet> tweets, ArrayList<String> follows) {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/userActivity.jTwig");
		JtwigModel model = JtwigModel.newModel().with("username", username)
				.with("tweets", tweets).with("follows", follows);

		return template.render(model);
	}

	public static String createTweetPageHTML(String username) {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/tweets.jTwig");
		JtwigModel model = JtwigModel.newModel().with("username", username);
		;

		return template.render(model);
	}

	private static String createlogOffPageHTML(String username) {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/logOff.jTwig");
		JtwigModel model = JtwigModel.newModel().with("username", username);

		return template.render(model);
	}
}