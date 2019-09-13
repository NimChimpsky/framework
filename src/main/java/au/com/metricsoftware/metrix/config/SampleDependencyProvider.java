package au.com.metricsoftware.metrix.config;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SampleDependencyProvider implements DependencyProvider {
    private Map<Class<?>, Object> dependencies = new HashMap<>(1);

    public SampleDependencyProvider() {
        Gson gson = new Gson();
        dependencies.put(Gson.class, gson);
    }

    @Override
    public Object get(Class<?> clazz) {
        return dependencies.get(clazz);
    }
}
