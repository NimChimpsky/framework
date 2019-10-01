package au.com.metricsoftware.metrix;

import au.com.metricsoftware.metrix.config.*;
import com.sun.net.httpserver.HttpServer;
import io.github.classgraph.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class MetrixServer {
    private final Integer port;
    private final String prefix;
    private final String apiPrefix;
    private final String[] controllerPackages;
    private final Map<Class<?>, Object> dependencies;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public MetrixServer(Builder builder) {
        this.port = builder.port;
        this.controllerPackages = builder.controllerPackages;
        this.prefix = builder.prefix;
        this.apiPrefix = builder.apiPrefix;
        this.dependencies = builder.dependencies;
    }

    public void start() throws IOException, ClassNotFoundException {
        logger.info("Starting metrix server on port {}", port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(prefix, new StaticResourceHandler(prefix));
        String pkg = "com.xyz";
        String routeAnnotation = pkg + ".Route";
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()             // Scan classes, methods, fields, annotations
                             .whitelistPackages(controllerPackages)      // Scan com.xyz and subpackages (omit to scan all packages)
                             .scan()) {                   // Start the scan
            List<Class> controllers = new Class[];
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation("Controller");

            for (ClassInfo routeClassInfo : classInfoList) {
                Class routeClassInfo.getClass();
                AnnotationInfo routeAnnotationInfo = routeClassInfo.getAnnotationInfo(routeAnnotation);
                List<AnnotationParameterValue> routeParamVals = routeAnnotationInfo.getParameterValues();
                // @com.xyz.Route has one required parameter
                String route = (String) routeParamVals.get(0).getValue();
                System.out.println(routeClassInfo.getName() + " is annotated with route " + route);
            }
        }
         =ClassPathScannerHelper.getControllers(controllerPackages);
        logger.info("Found {} controllers ", controllers.length);
        Context context = new Context(controllers, dependencies);
        RequestParser requestParser = new RequestParser();
        server.createContext(apiPrefix, new ApiHandler(apiPrefix, context, requestParser));
        server.setExecutor(null); // creates a default executor
        server.start();
    }


    public static class Builder {
        private Integer port = 80;
        private String prefix = "/";
        private String apiPrefix = "api";
        private Map<Class<?>, Object> dependencies;
        private String[] controllerPackages = {""};

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder withUrlPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder withApiUrlPrefix(String apiPrefix) {
            this.apiPrefix = apiPrefix;
            return this;
        }

        public Builder withControllerPackages(String... controllerPackages) {
            this.controllerPackages = controllerPackages;
            return this;
        }

        public Builder withDependencies(Map<Class<?>, Object> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public MetrixServer build() {
            return new MetrixServer(this);
        }
    }
}
