package config;

import config.annotations.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ClassPathScannerHelper {
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
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                Class<?> clazz = Class.forName(packageName + '.' + file.getName()
                                                                       .substring(0, file.getName().length() - 6));
                if (clazz.isAnnotationPresent(Controller.class)) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }
}
