package au.com.metricsoftware.metrix.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class StaticResourceHandler implements HttpHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String context;


    public StaticResourceHandler(String context) {
        this.context = context;

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        URI requestUri = httpExchange.getRequestURI();
        new RequestParser().parse(requestUri);
        String path = requestUri.getPath();
        String resource = context != path ? path.replace(context, "") : "Index.html";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resource);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
// StandardCharsets.UTF_8.name() > JDK 7
        OutputStream os = httpExchange.getResponseBody();
        String response = result.toString("UTF-8");

//            copy(inputStream,os );
        httpExchange.sendResponseHeaders(200, response.length());

        os.write(result.toByteArray());
        os.close();
    }
}

