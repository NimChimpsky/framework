package au.com.metricsoftware.metrix.config;


import au.com.metricsoftware.metrix.annotations.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassPathScannerHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathScannerHelper.class);
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            LOGGER.info("dir doesn't exist {}, package {}", directory, packageName);
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            LOGGER.info("file " + file.getPath());
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                LOGGER.info("isDir ");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                LOGGER.info("endsWith .class");
                Class<?> clazz = Class.forName(packageName + '.' + file.getName()
                                                                       .substring(0, file.getName().length() - 6));
                LOGGER.info("simpleName" + clazz.getSimpleName());
                if (clazz.isAnnotationPresent(Controller.class)) {
                    LOGGER.info("annoation is present " + clazz.getSimpleName());
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageNames The base packages
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getControllers(String[] packageNames)
            throws ClassNotFoundException, IOException {
        for (String packageName : packageNames) {
            LOGGER.info("packageName {}", packageName);
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ArrayList<Class> classes = new ArrayList<Class>();
        for (String packageName : packageNames) {
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }

            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }
}
