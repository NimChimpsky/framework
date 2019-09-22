package au.com.metricsoftware.metrix.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class ApiResourceHandler implements HttpHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ApiResourceHandler(String apiContext) {

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        URI requestUri = httpExchange.getRequestURI();
        new RequestParser().parse(requestUri);
    }
}
