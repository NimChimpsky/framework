package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Put;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PutProcessor extends BaseAnnotationProcessor {
    private Map<String, BiFunction<Map<String, String>, String, String>> controllerMap = new HashMap<>();

    public PutProcessor(Map<Class<?>, Object> dependencyProvider) {
        super(dependencyProvider);
    }

    @Override
    public void accept(Method method) {
        Put putRequestMapper = method.getAnnotation(Put.class);
        String url = putRequestMapper.value();
        BiFunction<Map<String, String>, String, String> biFunction = extractResponseBodyAndQueryStringFunction(method);
        controllerMap.put(url, biFunction);
    }

    public Map<String, BiFunction<Map<String, String>, String, String>> requestMapper() {
        return controllerMap;
    }
}
