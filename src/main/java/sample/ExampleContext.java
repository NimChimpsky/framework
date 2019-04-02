package sample;

import com.google.gson.Gson;
import config.ApplicationContext;
import config.GET;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ExampleContext implements ApplicationContext {
    public static final String CONTEXT = "context";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Gson gson = new Gson();
    private Map<String, Function<String, String>> getControllerMap = new HashMap<>();

    public ExampleContext() {
        Set<Class<?>> clazzes = new HashSet<>(1);
        clazzes.add(HelloWorldController.class);
        findMappings(clazzes);
    }

    public void findMappings(Set<Class<?>> classesForScanning) {
        for (Class<?> clazz : classesForScanning) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(GET.class)) {
                    GET getRequestMapper = method.getAnnotation(GET.class);
                    String url = getRequestMapper.url();
                    try {
                        final Object controller = clazz.newInstance();
                        Function<String, String> function = createFunction(method, controller);
                        getControllerMap.put(url, function);
                    } catch (InstantiationException e) {
                        logger.error("InstantiationException scanning getters {}", e);
                    } catch (IllegalAccessException e) {
                        logger.error("IllegalAccessException scanning getters {}", e);
                    }
                }
            }
        }

    }

    private Function<String, String> createFunction(Method method, Object controller) {
        return new Function<String, String>() {
            @Override
            public String apply(String s) {
                try {
                    return (String) method.invoke(controller, s);
                } catch (IllegalAccessException e) {
                    logger.error("IllegalAccessException processing get request {}", e);
                    return e.getMessage();
                } catch (InvocationTargetException e) {
                    logger.error("InvocationTargetException processing get request {}", e);
                    return e.getMessage();
                }

            }
        };
    }

    @Override
    public Map<String, Function<String, String>> requestMappingGet() {
        return getControllerMap;
    }

    @Override
    public Map<String, Function<String, String>> requestMappingPost() {
        return null;
    }
}
