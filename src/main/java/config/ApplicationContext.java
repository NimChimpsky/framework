package config;

import java.util.Map;
import java.util.function.Function;

public interface ApplicationContext {

    Map<String, Function<String, String>> requestMappingGet();

    Map<String, Function<String, String>> requestMappingPost();

    default String getPath() {
        return "/api/v1";
    }
}
