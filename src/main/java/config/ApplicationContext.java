package config;

import java.util.Map;
import java.util.function.Function;

public interface ApplicationContext {

    Map<String, Function<String, String>> requestMappingGet();

    Map<String, Function<String, String>> requestMappingPost();

    static String getPath() {
        return "/api/v1";
    }
    static String getContext(){return "context";}
}
