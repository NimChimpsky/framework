package config;

import java.util.Map;
import java.util.function.Function;

public interface ApplicationContext {

    Map<String, Function<Map<String, String>, String>> requestMappingGet();

    Map<String, Function<String, String>> requestMappingPost();

    static String getPath() {
        return "/api/v1";
    }
    static String getContext(){return "context";}

    Map<String, Function<String, String>> requestMappingPut();

    Map<String, Function<Map<String, String>, String>> requestMappingDelete();
}
