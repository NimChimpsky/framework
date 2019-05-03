package config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static config.ClassPathScannerHelper.findClasses;

public interface ApplicationContext {

    Map<String, Function<Map<String, String>, String>> requestMappingGet();

    Map<String, BiFunction<Map<String, String>, String, String>> requestMappingPost();

    static String getPath() {
        return "/api/v1";
    }
    static String getContext(){return "context";}

    Map<String, BiFunction<Map<String, String>, String, String>> requestMappingPut();

    Map<String, Function<Map<String, String>, String>> requestMappingDelete();

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    static Class[] getControllers(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }


}
