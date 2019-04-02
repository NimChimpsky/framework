package config;

public interface Factory {

    Object get(Class<?> clazz);
}
