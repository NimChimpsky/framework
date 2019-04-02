package sample;

import com.google.gson.Gson;
import config.GET;
import config.POST;

public class HelloWorldController {

    private Gson gson;


    @GET(url = "/HelloWorld")
    public String get(String s) {
        return "hello world";
    }

    @POST(url = "/HelloWorld")
    public String post(String s) {
        return "hello world";
    }
}
