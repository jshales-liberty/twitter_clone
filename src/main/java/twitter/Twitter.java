package twitter;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;

import org.jtwig.JtwigModel; 
import org.jtwig.JtwigTemplate;

public class Twitter {
	public static void main(String[] args) {
		
		port(2613);
		
		get("/login", (request, response) -> {
			return createLoginHTML();
		});
		
		get("/createNewUser", (request, response) -> {
			System.out.println("get practice");
			return createNewUserHTML();
		});
		
		post("/createNewUser", (request, response) -> {
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
