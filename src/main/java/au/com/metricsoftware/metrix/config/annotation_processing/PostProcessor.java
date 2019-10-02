package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Post;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PostProcessor extends BaseAnnotationProcessor {
    private Map<String, BiFunction<Map<String, String>, String, String>> controllerMap = new HashMap<>();

    public PostProcessor(Map<Class<?>, Object> dependencyProvider) {
        super(dependencyProvider);
    }

    @Override
    public void accept(Method method) {
        Post postRequestMapper = method.getAnnotation(Post.class);
        String url = postRequestMapper.value();
        BiFunction<Map<String, String>, String, String> biFunction = extractResponseBodyAndQueryStringFunction(method);
        controllerMap.put(url, biFunction);
    }

    public Map<String, BiFunction<Map<String, String>, String, String>> requestMapper() {
        return controllerMap;
    }
}
