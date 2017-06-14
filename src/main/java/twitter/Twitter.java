package twitter;

import static spark.Spark.get;
import static spark.Spark.post;

import java.sql.Timestamp;

import static spark.Spark.port;

import org.jtwig.JtwigModel; 
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;

public class Twitter {
	
	private static String userId;
	
	public static void main(String[] args) {
		
		port(2613);
		
		get("/login", (request, response) -> {
			return createLoginHTML();
		});
		
		get("/createNewUser", (request, response) -> {
			return createNewUserHTML();
		});
		
		get("/createTweetHTML", (request, response) -> {
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
					request.queryParams("password"),
					request.queryParams("pic"));
					request.session().attribute("username", request.queryParams("username"));
					userId = request.session().attribute("username");
					System.out.println(userId);
			return createTweetPageHTML(userId);
		});
		
		post("/submitTweet", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            Tweet tweet = gson.fromJson(body, Tweet.class);
            
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            
            System.out.println(timestamp);
            System.out.println(tweet.getTweet());
            
            return "jsonpost";
        });
	}
	
	public static String createLoginHTML(){
		JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jTwig");
        JtwigModel model = JtwigModel.newModel();

        return template.render(model);
	}
	
	public static String createNewUserHTML(){
		JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/newUser.jTwig");
		JtwigModel model = JtwigModel.newModel();

		return template.render(model);
	}
	
	public static String createTweetPageHTML(String userId){
		JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/tweets.jTwig");
		JtwigModel model = JtwigModel.newModel();
		
		return template.render(model);
	}
}