package au.com.metricsoftware.metrix.config;

import au.com.metricsoftware.metrix.annotations.Delete;
import au.com.metricsoftware.metrix.annotations.Get;
import au.com.metricsoftware.metrix.annotations.Post;
import au.com.metricsoftware.metrix.annotations.Put;
import au.com.metricsoftware.metrix.config.annotation_processing.DeleteProcessor;
import au.com.metricsoftware.metrix.config.annotation_processing.GetProcessor;
import au.com.metricsoftware.metrix.config.annotation_processing.PostProcessor;
import au.com.metricsoftware.metrix.config.annotation_processing.PutProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class Context {

    private final GetProcessor getProcessor;
    private final PostProcessor postProcessor;
    private final PutProcessor putProcessor;
    private final DeleteProcessor deleteProcessor;
    private final List<Class<? extends Annotation>> annotations = Arrays.asList(Get.class, Post.class, Put.class, Delete.class);
    private final Map<Class<? extends Annotation>, Consumer<Method>> annotationProcessorMap = new HashMap<>(annotations.size());

    public Context(Iterable<Class<?>> clazzes, final Map<Class<?>, Object> dependencyProvider) {
        this.getProcessor = new GetProcessor(dependencyProvider);
        annotationProcessorMap.put(Get.class, getProcessor);
        this.postProcessor = new PostProcessor(dependencyProvider);
        annotationProcessorMap.put(Post.class, postProcessor);
        this.deleteProcessor = new DeleteProcessor(dependencyProvider);
        annotationProcessorMap.put(Delete.class, deleteProcessor);
        this.putProcessor = new PutProcessor(dependencyProvider);
        annotationProcessorMap.put(Put.class, putProcessor);
        findMappings(clazzes);
    }


    private void findMappings(Iterable<Class<?>> classesForScanning) {
        for (Class<?> clazz : classesForScanning) {

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                for (Class<? extends Annotation> annotation : annotations) {
                    if (method.isAnnotationPresent(annotation)) {
                        annotationProcessorMap.get(annotation).accept(method);
                    }
                }
            }
        }

    }
    public Map<String, Function<Map<String, String>, String>> requestMappingGet() {
        return getProcessor.requestMapper();
    }

    public Map<String, BiFunction<Map<String, String>, String, String>> requestMappingPost() {
        return postProcessor.requestMapper();
    }


    public Map<String, BiFunction<Map<String, String>, String, String>> requestMappingPut() {
        return putProcessor.requestMapper();
    }

    public Map<String, Function<Map<String, String>, String>> requestMappingDelete() {
        return deleteProcessor.requestMapper();
    }


}
