package config;

import au.com.metricsoftware.metrix.config.Context;
import au.com.metricsoftware.metrix.config.RequestParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ApiHandler implements HttpHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Function<Map<String, String>, String>> requestMappingsGet;
    private final Map<String, Function<Map<String, String>, String>> requestMappingsDelete;
    private final Map<String, BiFunction<Map<String, String>, String, String>> requestMappingsPost;
    private final Map<String, BiFunction<Map<String, String>, String, String>> requestMappingsPut;
    private final Context context;
    private final RequestParser requestParser;


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {
            case "GET": {
                doGet(httpExchange);
                break;
            }
            case "POST": {
                doPost(httpExchange);
                break;
            }
            case "PUT": {
                doPut(httpExchange);
                break;
            }
            case "DELETE": {
                doDelete(httpExchange);
                break;
            }
        }
    }


    public ApiHandler(Context context, RequestParser requestParser) {
        this.context = context;
        this.requestParser = requestParser;
        requestMappingsGet = context.requestMappingGet();
        requestMappingsPost = context.requestMappingPost();
        requestMappingsPut = context.requestMappingPut();
        requestMappingsDelete = context.requestMappingDelete();

    }


    protected void doGet(final HttpExchange httpExchange) throws IOException {
        queryParametersOnly(httpExchange, requestMappingsGet);
    }


    protected void doDelete(final HttpExchange httpExchange) throws IOException {
        queryParametersOnly(httpExchange, requestMappingsDelete);
    }


    protected void doPost(final HttpExchange httpExchange) throws IOException {
        withJsonRequestBody(httpExchange, requestMappingsPost);
    }


    protected void doPut(final HttpExchange httpExchange) throws IOException {
        withJsonRequestBody(httpExchange, requestMappingsPut);
    }

    private void queryParametersOnly(final HttpExchange httpExchange, Map<String, Function<Map<String, String>, String>> requestMappingsGet) throws IOException {
        String url = httpExchange.getRequestURI().getPath();
        String key = url.replace(context.getPrefix(), "");
        Function<Map<String, String>, String> controller = requestMappingsGet.get(key);
        Map<String, String> parameterMap = requestParser.parseQueryString(httpExchange);
        String jsonBody = controller.apply(parameterMap);
        send(httpExchange, jsonBody);

    }

    private void send(HttpExchange httpExchange, String jsonBody) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        httpExchange.sendResponseHeaders(200, jsonBody.length());
        os.write(jsonBody.getBytes("UTF-8"));
        os.close();
    }

    private void withJsonRequestBody(final HttpExchange httpExchange, Map<String, BiFunction<Map<String, String>, String, String>> requestMappings) throws IOException {
        String url = httpExchange.getRequestURI().getPath();
        String key = url.replace(context.getPrefix(), "");
        String requestBody = requestParser.bodyParser(httpExchange);
        Map<String, String> parameterMap = requestParser.parseQueryString(httpExchange);
        BiFunction<Map<String, String>, String, String> controller = requestMappings.get(key);
        String jsonBody = controller.apply(parameterMap, requestBody);
        send(httpExchange, jsonBody);
    }
}
