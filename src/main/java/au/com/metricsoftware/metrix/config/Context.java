package au.com.metricsoftware.metrix.config;

import au.com.metricsoftware.metrix.annotations.Delete;
import au.com.metricsoftware.metrix.annotations.Get;
import au.com.metricsoftware.metrix.annotations.Post;
import au.com.metricsoftware.metrix.annotations.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class Context {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Function<Map<String, String>, String>> getControllerMap = new HashMap<>();
    private Map<String, Function<Map<String, String>, String>> deleteControllerMap = new HashMap<>();
    private Map<String, BiFunction<Map<String, String>, String, String>> postControllerMap = new HashMap<>();
    private Map<String, BiFunction<Map<String, String>, String, String>> putControllerMap = new HashMap<>();
    private final Map<Class<?>, Object> dependencyProvider;


    public Context(Iterable<Class<?>> clazzes, final Map<Class<?>, Object> dependencyProvider) {
        this.dependencyProvider = dependencyProvider;
        findMappings(clazzes);
    }


    private void findMappings(Iterable<Class<?>> classesForScanning) {
        for (Class<?> clazz : classesForScanning) {

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Get.class)) {
                    Get getRequestMapper = method.getAnnotation(Get.class);
                    String url = getRequestMapper.value();
                    try {

                        if (!validMethod(method, 1, Map.class)) {
                            logger.error("GET methods only allowed one parameter, Map<String, String>");
                            throw new IllegalAccessException(clazz.getSimpleName() + "." + method.getName() + " should have one parameter of type Map<String, String>");
                        }
                        final Object controller = createAndPopulateDependencies(clazz);
                        Function<Map<String, String>, String> function = createQueryStringFunction(method, controller);
                        // TODO check method signature takes Map<String, String> as argument
                        getControllerMap.put(url, function);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        logger.error("Exception scanning get request mappings", e);
                    }
                } else if (method.isAnnotationPresent(Post.class)) {
                    Post postRequestMapper = method.getAnnotation(Post.class);
                    String url = postRequestMapper.value();
                    try {
                        final Object controller = createAndPopulateDependencies(clazz);
                        BiFunction<Map<String, String>, String, String> function = createRequestBodyFunction(method, controller);
                        postControllerMap.put(url, function);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        logger.error("Exception scanning post request mappings", e);
                    }
                } else if (method.isAnnotationPresent(Delete.class)) {
                    Delete deleteRequestMapper = method.getAnnotation(Delete.class);
                    String url = deleteRequestMapper.value();
                    try {
                        if (!validMethod(method, 1, Map.class)) {
                            logger.error("GET methods only allowed one parameter, Map<String, String>");
                            throw new IllegalAccessException(clazz.getSimpleName() + "." + method.getName() + " should have one parameter of type Map<String, String>");
                        }
                        final Object controller = createAndPopulateDependencies(clazz);
                        Function<Map<String, String>, String> function = createQueryStringFunction(method, controller);
                        deleteControllerMap.put(url, function);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        logger.error("Exception scanning delete request mappings", e);
                    }
                } else if (method.isAnnotationPresent(Put.class)) {
                    Put putRequestMapper = method.getAnnotation(Put.class);
                    String url = putRequestMapper.value();
                    try {
                        final Object controller = createAndPopulateDependencies(clazz);
                        BiFunction<Map<String, String>, String, String> function = createRequestBodyFunction(method, controller);
                        putControllerMap.put(url, function);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        logger.error("Exception scanning put request mappings", e);
                    }
                } else {
                    // fuck patch,and fuck making it anymore oo, there are only four options
                }
            }
        }

    }

    private boolean validMethod(Method method, int count, Class<Map> clazz) {
        return !method.getReturnType().equals(String.class) || method.getParameterCount() != count || !method.getParameterTypes()[0].equals(clazz);
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
//        Field[] fields = clazz.getDeclaredFields();
//        List<Field> injectedFields = Arrays.stream(fields)
//                                           .filter(field -> field.isAnnotationPresent(Inject.class))
//                                           .collect(Collectors.toList());
//
//        for (Field field : injectedFields) {
//            Object dependency = dependencyProvider.get(field.getType());
//            if (dependency != null) {
//                field.setAccessible(true);
//                field.set(component, dependency);
//            } else {
//                logger.warn("Unable to find dependency " + field.getName() + " with type " + field.getType() + " in " + clazz
//                        .getName());
//            }
//        }
        return component;
    }

    private Function<Map<String, String>, String> createQueryStringFunction(Method method, Object controller) {
        return new Function<Map<String, String>, String>() {
            @Override
            public String apply(Map<String, String> parameters) {
                try {
                    return (String) method.invoke(controller, parameters);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.info("parameters {}", mapToCsv(parameters));
                    logger.error("Exception processing get request mapping", e);
                    return e.getMessage();
                }
            }
        };
    }


    private BiFunction<Map<String, String>, String, String> createRequestBodyFunction(Method method, Object controller) {
        return new BiFunction<Map<String, String>, String, String>() {
            @Override
            public String apply(Map<String, String> parameters, String requestBody) {
                try {
                    return (String) method.invoke(controller, parameters, requestBody);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.info("parameters {}", mapToCsv(parameters));
                    logger.info("request body {}", requestBody);
                    logger.error("Exception processing post request", e);
                    return e.getMessage();
                }

            }
        };
    }

    public Map<String, Function<Map<String, String>, String>> requestMappingGet() {
        return getControllerMap;
    }

    public Map<String, BiFunction<Map<String, String>, String, String>> requestMappingPost() {
        return postControllerMap;
    }


    public Map<String, BiFunction<Map<String, String>, String, String>> requestMappingPut() {
        return putControllerMap;
    }

    public Map<String, Function<Map<String, String>, String>> requestMappingDelete() {
        return deleteControllerMap;
    }

    private String mapToCsv(Map<String, String> map) {
        if (map.isEmpty()) {
            return "";
        }
        return map.entrySet().stream().map(new Function<Map.Entry<String, String>, String>() {
            @Override
            public String apply(Map.Entry<String, String> entry) {
                return entry.getKey() + ":" + entry.getValue();
            }
        }).reduce(new BinaryOperator<String>() {
            @Override
            public String apply(String s, String s2) {
                return s + "," + s2;
            }
        }).get();
    }
}
