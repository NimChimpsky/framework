import com.google.gson.Gson;
import config.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ExampleContext implements ApplicationContext {
    private Gson gson = new Gson();

    @Override
    public Map<String, Function<String, String>> requestMappingGet() {

        Map<String, Function<String, String>> map = new HashMap<>(1);
        HelloWorldController helloWorldController = new HelloWorldController();
        Function<String, String> function = helloWorldController::get;
        map.put("", function);
        return map;
    }

    @Override
    public Map<String, Function<String, String>> requestMappingPost() {
        return null;
    }
}
