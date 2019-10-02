package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DeleteProcessor extends BaseAnnotationProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Function<Map<String, String>, String>> controllerMap = new HashMap<>();

    public DeleteProcessor(Map<Class<?>, Object> dependencyProvider) {
        super(dependencyProvider);
    }

    @Override
    public void accept(Method method) {
        Delete deleteRequestMapper = method.getAnnotation(Delete.class);
        String url = deleteRequestMapper.value();
        Function<Map<String, String>, String> function = extractQueryStringFunction(method);
        controllerMap.put(url, function);
    }

    public Map<String, Function<Map<String, String>, String>> requestMapper() {
        return controllerMap;
    }
}
