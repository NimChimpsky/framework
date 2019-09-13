package au.com.metricsoftware.metrix;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MetrixServer {
    private final Integer port;
    private final String context;
    private final String[] controllerPackages;

    public MetrixServer(Builder builder) {
        this.port = builder.port;
        this.controllerPackages = builder.controllerPackages;
        this.context = builder.context;

    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(context, new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("Index.html");
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
// StandardCharsets.UTF_8.name() > JDK 7
            OutputStream os = t.getResponseBody();
            String response = result.toString("UTF-8");

//            copy(inputStream,os );
            t.sendResponseHeaders(200, response.length());

            os.write(result.toByteArray());
            os.close();
        }
    }

    public static class Builder {
        private Integer port = 80;
        private String context = "/";
        private String apiContext = "api";
        private String[] controllerPackages = {""};

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder withContext(String context) {
            this.context = context;
            return this;
        }

        public Builder withApiContext(String apiContext) {
            this.apiContext = apiContext;
            return this;
        }

        public Builder withControllerPackages(String... controllerPackages) {
            this.controllerPackages = controllerPackages;
            return this;
        }

        public MetrixServer build() {
            return new MetrixServer(this);
        }
    }
}
