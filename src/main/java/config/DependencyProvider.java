package config;

public interface DependencyProvider {

    Object get(Class<?> clazz);
}
