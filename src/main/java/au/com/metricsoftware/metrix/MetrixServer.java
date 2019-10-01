package au.com.metricsoftware.metrix;

import au.com.metricsoftware.metrix.config.*;
import com.sun.net.httpserver.HttpServer;
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
        List<Class<?>> controllers = ClassPathUtil.findControllers(controllerPackages);
        logger.info("Found {} controllers ", controllers.size());
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
