package sample;

import config.ApplicationContext;
import config.GET;
import config.DependencyProvider;
import config.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SampleContext implements ApplicationContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Function<String, String>> getControllerMap = new HashMap<>();
    private final DependencyProvider dependencyProvider;

    public SampleContext(DependencyProvider dependencyProvider) {
        this.dependencyProvider = dependencyProvider;
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
                        final Object controller = createAndPopulateDependencies(clazz);
                        Function<String, String> function = createFunction(method, controller);
                        getControllerMap.put(url, function);
                    } catch (InstantiationException | IllegalAccessException e) {
                        logger.error("Exception scanning getters {}", e);
                    }
                }
            }
        }

    }

    private Object createAndPopulateDependencies(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        Object component = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        List<Field> injectedFields = Arrays.stream(fields).filter(new Predicate<Field>() {
            @Override
            public boolean test(Field field) {
                return field.isAnnotationPresent(Inject.class);
            }
        }).collect(Collectors.toList());
        for (Field field : injectedFields) {
            Object dependency = dependencyProvider.get(field.getType());
            if (dependency != null) {
                field.setAccessible(true);
                field.set(component, dependency);
            } else {
                logger.warn("Unable to find dependency " + field.getName() + " with type " + field.getType() + " in " + clazz
                        .getName());
            }
        }
        return component;
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
