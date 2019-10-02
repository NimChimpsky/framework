package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PostProcessor extends BaseAnnotationProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, BiFunction<Map<String, String>, String, String>> controllerMap = new HashMap<>();

    public PostProcessor(Map<Class<?>, Object> dependencyProvider) {
        super(dependencyProvider);
    }

    @Override
    public void accept(Method method) {
        Post postRequestMapper = method.getAnnotation(Post.class);
        String url = postRequestMapper.value();
        try {
            if (validReturnType(method) && validMethod(method, 2, Arrays.asList(Map.class, String.class))) {
                final Object controller = createAndPopulateDependencies(method.getDeclaringClass());
                BiFunction<Map<String, String>, String, String> function = createRequestBodyFunction(method, controller);
                controllerMap.put(url, function);
            }

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | AnnotationProcessingException e) {
            logger.error("Exception scanning request mappings", e);
        }
    }

    public Map<String, BiFunction<Map<String, String>, String, String>> requestMapper() {
        return controllerMap;
    }
}
