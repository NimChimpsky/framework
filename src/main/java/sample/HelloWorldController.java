package sample;

import com.google.gson.Gson;
import config.Controller;
import config.Get;
import config.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Controller
public class HelloWorldController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Gson gson;

    public HelloWorldController(final Gson gson) {
        this.gson = gson;
    }


    @Get(url = "/HelloWorld")
    public String get(Map<String, String> parameters) {
        Person person = new Person(parameters.get("name"), Integer.parseInt(parameters.get("age")));
        return gson.toJson(person);
    }

    @Post(url = "/HelloWorld")
    public String post(String s) {
        return "hello world";
    }
}
