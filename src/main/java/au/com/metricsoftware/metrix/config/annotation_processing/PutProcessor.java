package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PutProcessor extends BaseAnnotationProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, BiFunction<Map<String, String>, String, String>> controllerMap = new HashMap<>();

    public PutProcessor(Map<Class<?>, Object> dependencyProvider) {
        super(dependencyProvider);
    }

    @Override
    public void accept(Method method) {
        Put putRequestMapper = method.getAnnotation(Put.class);
        String url = putRequestMapper.value();
        try {
            final Object controller = createAndPopulateDependencies(method.getDeclaringClass());
            BiFunction<Map<String, String>, String, String> function = createRequestBodyFunction(method, controller);
            controllerMap.put(url, function);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Exception scanning put request mappings", e);
        }
    }

    public Map<String, BiFunction<Map<String, String>, String, String>> requestMapper() {
        return controllerMap;
    }
}
