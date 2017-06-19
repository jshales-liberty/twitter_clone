package twitter;

import static spark.Spark.get;
import static spark.Spark.post;
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
			return gson.toJson(TwitterDB.getTweetList(((User) request.session().attribute("user")).getId()));
		});

		get("/popularTweeters", (request, response) -> {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(TwitterDB.getPopularTweeters(((User) request.session().attribute("user")).getId()));
		});

		post("/submitTweet", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            Tweet tweet = gson.fromJson(body, Tweet.class);
            new Tweet(tweet.getTweet(), (((User) req.session().attribute("user")).getId()), ((User) req.session().attribute("user")).getUsername());
            
            return "jsonpost";
        });	
		
		post("/submitFollow", (req, res) -> {
			String body = req.body();

			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(body).getAsJsonObject();
			
			User userBeingFollowed = TwitterDB.findUser(obj.get("follows").getAsString());
			return TwitterDB.followSomeone(((User) req.session().attribute("user")).getId(), userBeingFollowed.getId());
		});

		get("/logoff", (req, res) -> {
			String userName = ((User) req.session().attribute("user")).getUsername();
			req.session().removeAttribute("user");
			return createlogOffPageHTML(userName);
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

	private static String createlogOffPageHTML(String username) {
		JtwigTemplate template = JtwigTemplate
				.classpathTemplate("templates/logOff.jTwig");
		JtwigModel model = JtwigModel.newModel().with("username", username);

		return template.render(model);
	}
}