package au.com.metricsoftware.metrix.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class StaticResourceHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        URI requestUri = t.getRequestURI();
        String path = requestUri.getPath();
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

