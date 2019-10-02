package au.com.metricsoftware.metrix.config.annotation_processing;

import au.com.metricsoftware.metrix.annotations.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Function<Map<String, String>, String> function = extractQueryStringFunction(method);
        controllerMap.put(url, function);
    }

    public Map<String, Function<Map<String, String>, String>> requestMapper() {
        return controllerMap;
    }
}
