import com.google.gson.Gson;
import config.ApplicationContext;
import config.GET;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ExampleContext implements ApplicationContext {
    private Gson gson = new Gson();
    private Map<String, Function<String, String>> getControllerMap = new HashMap<>();

    public void findMappings(Set<Class<?>> classesForScanning) {
        for (Class<?> clazz : classesForScanning) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(GET.class)) {
                    GET getRequestMapper = field.getAnnotation(GET.class);
                    String url = getRequestMapper.url();

                    try {
                        final Object controller = clazz.newInstance();


                        final Method getMethod = clazz.getMethod(field.getName(), String.class);


                        Function<String, String> function = new Function<String, String>() {
                            @Override
                            public String apply(String s) {
                                try {
                                    return (String) getMethod.invoke(controller, s);
                                } catch (IllegalAccessException e) {
                                    return e.getMessage();
                                } catch (InvocationTargetException e) {
                                    return e.getMessage();
                                }

                            }
                        };

                        getControllerMap.put(url, function);

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public Map<String, Function<String, String>> requestMappingGet() {

        Map<String, Function<String, String>> map = new HashMap<>(1);
        HelloWorldController helloWorldController = new HelloWorldController();
        Function<String, String> function = helloWorldController::get;
        map.put("", function);
        return map;
    }

    @Override
    public Map<String, Function<String, String>> requestMappingPost() {
        return null;
    }
}
