package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

            if (!validMethod(method, 1, Map.class)) {
                logger.error("GET methods only allowed one parameter, Map<String, String>");
                throw new IllegalAccessException(method.getDeclaringClass()
                                                       .getSimpleName() + "." + method.getName() + " should have one parameter of type Map<String, String>");
            }
            final Object controller = createAndPopulateDependencies(method.getDeclaringClass());
            Function<Map<String, String>, String> function = createQueryStringFunction(method, controller);
            // TODO check method signature takes Map<String, String> as argument
            controllerMap.put(url, function);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Exception scanning get request mappings", e);
        }
    }

    public Map<String, Function<Map<String, String>, String>> requestMapper() {
        return controllerMap;
    }
}
