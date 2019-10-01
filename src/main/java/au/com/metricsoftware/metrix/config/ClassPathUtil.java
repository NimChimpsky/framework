package au.com.metricsoftware.metrix.config;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.LinkedList;
import java.util.List;

public class ClassPathUtil {

    public static List<Class<?>> findControllers(String... controllerPackages) {
        List<Class<?>> controllers = new LinkedList<>();
        ScanResult scanResult =
                new ClassGraph()
                        .enableAllInfo()             // Scan classes, methods, fields, annotations
                        .whitelistPackages(controllerPackages)      // Scan com.xyz and subpackages (omit to scan all packages)
                        .scan();                   // Start the scan

        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation("au.com.metricsoftware.metrix.annotations.Controller");

        for (ClassInfo routeClassInfo : classInfoList) {
            controllers.add(routeClassInfo.getClass());
        }

        scanResult.close();
        return controllers;
    }
}
