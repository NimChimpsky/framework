package sample;

import com.google.gson.Gson;
import config.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

// must have controller annotation, to be detected by scanner in samplecontext
@Controller
public class HelloWorldController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Gson gson;

    // only one constructor allowed, any injected dependencies must be available in dependency provider implementation
    public HelloWorldController(final Gson gson) {
        this.gson = gson;
    }


    // method signature must not change for request types
    // get request take a map of string string, extracted from url  query string
    @Get("/HelloWorld")
    public String get(Map<String, String> parameters) {
        Person person = new Person(parameters.get("name"), Integer.parseInt(parameters.get("age")));
        return gson.toJson(person);
    }

    // method name irrelevant
    @Post("/HelloWorld")
    public String postalicious(String s) {
        return "posted hello world";
    }

    @Put("/HelloWorld")
    public String put(String s) {
        return "put hello world";
    }

    @Delete("/HelloWorld")
    public String delete(Map<String, String> parameters) {
        return "deleted hello world";
    }

    // Multiple annotations per method, not valid
//    @Post(url = "/HelloWorld")
//    @Delete(url = "/HelloWorld")
//    public String delete(String s) {
//        return "hello world";
//    }


}
