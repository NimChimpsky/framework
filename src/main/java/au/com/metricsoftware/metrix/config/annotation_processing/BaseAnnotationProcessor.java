package au.com.metricsoftware.metrix.config.annotation_processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseAnnotationProcessor implements Consumer<Method> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<Class<?>, Object> dependencyProvider;

    public BaseAnnotationProcessor(Map<Class<?>, Object> dependencyProvider) {
        this.dependencyProvider = dependencyProvider;
    }

    protected boolean validReturnType(Method method) {
        if (method.getReturnType().equals(String.class)) {
            return true;
        } else {
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            throw new IllegalArgumentException(className + "." + methodName + " should return String");
        }
    }

    protected boolean validMethod(Method method, int count, List<Class> expectedParameters) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        if (method.getParameterCount() != count) {
            throw new IllegalArgumentException("Wrong number of arguments for " + className + "." + methodName + ", should be " + count);
        }
        Class<?>[] parameterType = method.getParameterTypes();
        for (int i = 0; i < expectedParameters.size(); i++) {
            if (!parameterType[i].equals(expectedParameters.get(i))) {
                throw new IllegalArgumentException("Wrong arguments type for " + className + "." + methodName + ",expected " + expectedParameters
                        .get(i));
            }
        }
        return true;

    }

    protected Function<Map<String, String>, String> extractQueryStringFunction(Method method) {
        try {
            if (validReturnType(method) && validMethod(method, 1, Arrays.asList(Map.class))) {
                final Object controller = createAndPopulateDependencies(method.getDeclaringClass());
                Function<Map<String, String>, String> function = createQueryStringFunction(method, controller);
                return function;
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Exception scanning request mappings", e);

        }
        return null; // will result in 404
    }

    protected BiFunction<Map<String, String>, String, String> extractResponseBodyAndQueryStringFunction(Method method) {
        try {
            if (validReturnType(method) && validMethod(method, 2, Arrays.asList(Map.class, String.class))) {
                final Object controller = createAndPopulateDependencies(method.getDeclaringClass());
                BiFunction<Map<String, String>, String, String> function = createRequestBodyFunction(method, controller);
                return function;
            }

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Exception scanning request mappings", e);
        }
        return null;// will result in 404
    }
    protected Object createAndPopulateDependencies(Class<?> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
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

    protected Function<Map<String, String>, String> createQueryStringFunction(Method method, Object controller) {
        return new Function<Map<String, String>, String>() {
            @Override
            public String apply(Map<String, String> parameters) {
                try {
                    return (String) method.invoke(controller, parameters);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.info("parameters {}", mapToCsv(parameters));
                    logger.error("Exception processing query string mapping", e);
                    return e.getMessage();
                }
            }
        };
    }

    protected BiFunction<Map<String, String>, String, String> createRequestBodyFunction(Method method, Object controller) {
        return new BiFunction<Map<String, String>, String, String>() {
            @Override
            public String apply(Map<String, String> parameters, String requestBody) {
                try {
                    return (String) method.invoke(controller, parameters, requestBody);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.info("parameters {}", mapToCsv(parameters));
                    logger.info("request body {}", requestBody);
                    logger.error("Exception processing request body and query string mapping", e);
                    return e.getMessage();
                }

            }
        };
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
