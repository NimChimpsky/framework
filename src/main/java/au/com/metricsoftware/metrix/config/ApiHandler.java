package au.com.metricsoftware.metrix.config;

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
    private final String prefix;

    public ApiHandler(final String prefix, final Context context, final RequestParser requestParser) {
        this.context = context;
        this.requestParser = requestParser;
        this.prefix = prefix;
        requestMappingsGet = context.requestMappingGet();
        requestMappingsPost = context.requestMappingPost();
        requestMappingsPut = context.requestMappingPut();
        requestMappingsDelete = context.requestMappingDelete();
    }

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
            default: {
                logger.error("No handler specified for {}", requestMethod);
                break;
            }
        }
    }


    private void doGet(final HttpExchange httpExchange) throws IOException {
        logger.debug("GET request {}", httpExchange.getRequestURI().getPath());
        queryParametersOnly(httpExchange, requestMappingsGet);
    }


    private void doDelete(final HttpExchange httpExchange) throws IOException {
        logger.debug("DELETE request {}", httpExchange.getRequestURI().getPath());
        queryParametersOnly(httpExchange, requestMappingsDelete);
    }


    private void doPost(final HttpExchange httpExchange) throws IOException {
        logger.debug("POST request {}", httpExchange.getRequestURI().getPath());
        withJsonRequestBody(httpExchange, requestMappingsPost);
    }


    private void doPut(final HttpExchange httpExchange) throws IOException {
        logger.debug("PUT request {}", httpExchange.getRequestURI().getPath());
        withJsonRequestBody(httpExchange, requestMappingsPut);
    }

    private void withJsonRequestBody(final HttpExchange httpExchange, Map<String, BiFunction<Map<String, String>, String, String>> requestMappings) throws IOException {
        String url = httpExchange.getRequestURI().getPath();
        String key = url.replace(prefix, "");
        String requestBody = requestParser.requestBodyToString(httpExchange);
        logger.debug("Request Body received {}", requestBody);
        Map<String, String> parameterMap = requestParser.queryStringToParameterMap(httpExchange);

        BiFunction<Map<String, String>, String, String> controller = requestMappings.get(key);
        String jsonBody = controller.apply(parameterMap, requestBody);
        send(200, httpExchange, jsonBody);
    }

    private void queryParametersOnly(final HttpExchange httpExchange, Map<String, Function<Map<String, String>, String>> requestMappingsGet) throws IOException {
        String url = httpExchange.getRequestURI().getPath();
        String key = url.replace(prefix, "");
        Function<Map<String, String>, String> controller = requestMappingsGet.get(key);
        if (controller == null) {
            mappingNotFound(url, httpExchange);
        }
        Map<String, String> parameterMap = requestParser.queryStringToParameterMap(httpExchange);
        if (logger.isDebugEnabled()) {
            logMap(parameterMap);
        }
        String jsonBody = controller.apply(parameterMap);
        send(200, httpExchange, jsonBody);

    }

    private void mappingNotFound(String key, HttpExchange httpExchange) throws IOException {
        logger.error("Mapping for {} not found", key);
        send(404, httpExchange, "No mapping found for " + key);
    }

    private void logMap(Map<String, String> parameterMap) {

        String paramPrefix = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            stringBuilder.append(paramPrefix + entry.getKey() + "=" + entry.getValue());
            paramPrefix = ", ";
        }
        logger.debug("Query parameters found : {}", stringBuilder.toString());

    }

    private void send(int code, HttpExchange httpExchange, String jsonBody) throws IOException {
        logger.debug("Response body returned : {} ", jsonBody);
        OutputStream os = httpExchange.getResponseBody();
        httpExchange.sendResponseHeaders(code, jsonBody.length());
        os.write(jsonBody.getBytes("UTF-8"));
        os.close();
    }


}
