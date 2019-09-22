package au.com.metricsoftware.metrix.config;

public interface DependencyProvider {

    Object get(Class<?> clazz);
}
