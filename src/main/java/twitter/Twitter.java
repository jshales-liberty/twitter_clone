package twitter;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;

import org.jtwig.JtwigModel; 
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;

public class Twitter {
	public static void main(String[] args) {
		
		port(2613);
		
		get("/login", (request, response) -> {
			return createLoginHTML();
		});
		
		 post("/login", (req, res) -> {
	            String body = req.body();
	            Gson gson = new Gson();
	            User c = gson.fromJson(body, User.class);
	            return c.checkCredentials();
	        });
		
		get("/createNewUser", (request, response) -> {
			System.out.println("get practice");
			return createNewUserHTML();
		});
		
		post("/createNewUser", (request, response) -> {
			new User(request.queryParams("firstName"),
								request.queryParams("lastName"),
								request.queryParams("username"),
								request.queryParams("birth_date"),
								request.queryParams("email"),
								request.queryParams("bio"),
								request.queryParams("password"));
			
			System.out.println(request.queryParams("lastName"));
			return createLoginHTML();
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
	
}
