package sample;

import config.ApplicationContext;
import config.DependencyProvider;
import config.annotations.Get;
import config.annotations.Inject;
import config.annotations.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SampleContext implements ApplicationContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Function<Map<String, String>, String>> getControllerMap = new HashMap<>();
    private Map<String, Function<String, String>> postControllerMap = new HashMap<>();
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

                if (method.isAnnotationPresent(Get.class)) {
                    Get getRequestMapper = method.getAnnotation(Get.class);
                    String url = getRequestMapper.url();
                    try {
                        final Object controller = createAndPopulateDependencies(clazz);
                        Function<Map<String, String>, String> function = createGetFunction(method, controller);
                        getControllerMap.put(url, function);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        logger.error("Exception scanning get request mappings {}", e);
                    }
                } else if (method.isAnnotationPresent(Post.class)) {
                    Post postRequestMapper = method.getAnnotation(Post.class);
                    String url = postRequestMapper.url();
                    try {
                        final Object controller = createAndPopulateDependencies(clazz);
                        Function<String, String> function = createPostFunction(method, controller);
                        postControllerMap.put(url, function);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        logger.error("Exception scanning get request mappings {}", e);
                    }
                }
            }
        }

    }

    private Object createAndPopulateDependencies(Class<?> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Constructor[] constructors = clazz.getConstructors();
        Constructor constructor = constructors[0];
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] constructorArguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Object dependency = dependencyProvider.get(parameterTypes[i]);
            constructorArguments[i] = dependency;
        }
        Object component = constructor.newInstance(constructorArguments);
        Field[] fields = clazz.getDeclaredFields();
        List<Field> injectedFields = Arrays.stream(fields)
                                           .filter(field -> field.isAnnotationPresent(Inject.class))
                                           .collect(Collectors.toList());

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

    private Function<Map<String, String>, String> createGetFunction(Method method, Object controller) {
        return new Function<Map<String, String>, String>() {
            @Override
            public String apply(Map<String, String> parameters) {
                try {
                    return (String) method.invoke(controller, parameters);
                } catch (IllegalAccessException e) {
                    logger.error("IllegalAccessException processing get request mapping {}", e);
                    return e.getMessage();
                } catch (InvocationTargetException e) {
                    logger.error("InvocationTargetException processing get request mapping{}", e);
                    return e.getMessage();
                }

            }
        };
    }

    private Function<String, String> createPostFunction(Method method, Object controller) {
        return new Function<String, String>() {
            @Override
            public String apply(String requestBody) {
                try {
                    return (String) method.invoke(controller, requestBody);
                } catch (IllegalAccessException e) {
                    logger.error("IllegalAccessException processing post request mapping {}", e);
                    return e.getMessage();
                } catch (InvocationTargetException e) {
                    logger.error("InvocationTargetException processing post request mapping {}", e);
                    return e.getMessage();
                }

            }
        };
    }

    @Override
    public Map<String, Function<Map<String, String>, String>> requestMappingGet() {
        return getControllerMap;
    }

    @Override
    public Map<String, Function<String, String>> requestMappingPost() {
        return null;
    }
}
