package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
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
        try {
            if (!validMethod(method, 1, Map.class)) {
                logger.error("GET methods only allowed one parameter, Map<String, String>");
                throw new IllegalAccessException(method.getDeclaringClass()
                                                       .getSimpleName() + "." + method.getName() + " should have one parameter of type Map<String, String>");
            }
            final Object controller = createAndPopulateDependencies(method.getDeclaringClass());
            Function<Map<String, String>, String> function = createQueryStringFunction(method, controller);
            controllerMap.put(url, function);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Exception scanning delete request mappings", e);
        }
    }

    public Map<String, Function<Map<String, String>, String>> requestMapper() {
        return controllerMap;
    }
}
