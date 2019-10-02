package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GetProcessor extends BaseAnnotationProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Function<Map<String, String>, String>> controllerMap = new HashMap<>();

    public GetProcessor(Map<Class<?>, Object> dependencyProvider) {
        super(dependencyProvider);
    }

    @Override
    public void accept(Method method) {
        Get getRequestMapper = method.getAnnotation(Get.class);
        String url = getRequestMapper.value();
        try {
            if (validReturnType(method) && validMethod(method, 1, Arrays.asList(Map.class))) {
                final Object controller = createAndPopulateDependencies(method.getDeclaringClass());
                Function<Map<String, String>, String> function = createQueryStringFunction(method, controller);
                controllerMap.put(url, function);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | AnnotationProcessingException e) {
            logger.error("Exception scanning request mappings", e);
        }
    }

    public Map<String, Function<Map<String, String>, String>> requestMapper() {
        return controllerMap;
    }
}
