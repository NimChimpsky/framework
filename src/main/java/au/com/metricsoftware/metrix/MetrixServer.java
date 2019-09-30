package au.com.metricsoftware.metrix;

import au.com.metricsoftware.metrix.config.ApiResourceHandler;
import au.com.metricsoftware.metrix.config.StaticResourceHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class MetrixServer {
    private final Integer port;
    private final String context;
    private final String apiContext;
    private final String[] controllerPackages;
    private final Map<Class<?>, Object> dependencies;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public MetrixServer(Builder builder) {
        this.port = builder.port;
        this.controllerPackages = builder.controllerPackages;
        this.context = builder.context;
        this.apiContext = builder.apiContext;
        this.dependencies = builder.dependencies;
    }

    public void start() throws IOException {
        logger.info("Starting metrix server on port {}", port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(context, new StaticResourceHandler(context));
        server.createContext(apiContext, new ApiResourceHandler(apiContext));

        server.setExecutor(null); // creates a default executor
        server.start();
    }


    public static class Builder {
        private Integer port = 80;
        private String context = "/";
        private String apiContext = "api";
        private Map<Class<?>, Object> dependencies;
        private String[] controllerPackages = {""};

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder withStaticContext(String context) {
            this.context = context;
            return this;
        }

        public Builder withUrlPrefix(String urlPrefix) {
            this.apiContext = apiContext;
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
