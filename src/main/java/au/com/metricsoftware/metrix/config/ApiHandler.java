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


    protected void doGet(final HttpExchange httpExchange) throws IOException {
        logger.debug("GET request {}", httpExchange.getRequestURI().getPath());
        queryParametersOnly(httpExchange, requestMappingsGet);
    }


    protected void doDelete(final HttpExchange httpExchange) throws IOException {
        logger.debug("DELETE request {}", httpExchange.getRequestURI().getPath());
        queryParametersOnly(httpExchange, requestMappingsDelete);
    }


    protected void doPost(final HttpExchange httpExchange) throws IOException {
        logger.debug("POST request {}", httpExchange.getRequestURI().getPath());
        withJsonRequestBody(httpExchange, requestMappingsPost);
    }


    protected void doPut(final HttpExchange httpExchange) throws IOException {
        logger.debug("PUT request {}", httpExchange.getRequestURI().getPath());
        withJsonRequestBody(httpExchange, requestMappingsPut);
    }

    private void withJsonRequestBody(final HttpExchange httpExchange, Map<String, BiFunction<Map<String, String>, String, String>> requestMappings) throws IOException {
        String url = httpExchange.getRequestURI().getPath();
        String key = url.replace(prefix, "");
        String requestBody = requestParser.requestBodyToString(httpExchange);
        logger.debug("Request Body found {}", requestBody);
        Map<String, String> parameterMap = requestParser.queryStringToParameterMap(httpExchange);

        BiFunction<Map<String, String>, String, String> controller = requestMappings.get(key);
        String jsonBody = controller.apply(parameterMap, requestBody);
        send(httpExchange, jsonBody);
    }

    private void queryParametersOnly(final HttpExchange httpExchange, Map<String, Function<Map<String, String>, String>> requestMappingsGet) throws IOException {
        String url = httpExchange.getRequestURI().getPath();
        String key = url.replace(prefix, "");
        Function<Map<String, String>, String> controller = requestMappingsGet.get(key);
        Map<String, String> parameterMap = requestParser.queryStringToParameterMap(httpExchange);
        String prefix = "";
        if (logger.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                stringBuilder.append(prefix + entry.getKey() + "=" + entry.getValue());
                prefix = ", ";
            }
            logger.debug("Query parameters found : {}", stringBuilder.toString());

        }
        String jsonBody = controller.apply(parameterMap);
        send(httpExchange, jsonBody);

    }

    private void send(HttpExchange httpExchange, String jsonBody) throws IOException {
        logger.debug("Reponse body returned : {} ", jsonBody);
        OutputStream os = httpExchange.getResponseBody();
        httpExchange.sendResponseHeaders(200, jsonBody.length());
        os.write(jsonBody.getBytes("UTF-8"));
        os.close();
    }


}
