package au.com.metricsoftware.metrix.config;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RequestParser() {
    }

    void parse(URI requestUri) {
        String path = requestUri.getPath();
        String rawPath = requestUri.getRawPath();
        String query = requestUri.getQuery();
        String rawQuery = requestUri.getRawQuery();
        String authority = requestUri.getAuthority();
        String rawAuthority = requestUri.getRawAuthority();
        String fragment = requestUri.getFragment();
        String rawFragment = requestUri.getRawFragment();
//        logger.info("path {}, rawPath{}", path, rawPath);
//        logger.info("query {}, rawQuery{}", query, rawQuery);
//        logger.info("authority {}, rawAuthority{}", authority, rawAuthority);
//        logger.info("fragment {}, rawFragment{}", fragment, rawFragment);
    }

    Map<String, String> queryStringToParameterMap(HttpExchange httpExchange) {
        String qs = httpExchange.getRequestURI().getQuery();
        Map<String, String> result = new HashMap<>();
        if (qs == null)
            return result;

        int last = 0, next, l = qs.length();
        while (last < l) {
            next = qs.indexOf('&', last);
            if (next == -1)
                next = l;

            if (next > last) {
                int eqPos = qs.indexOf('=', last);
                try {
                    if (eqPos < 0 || eqPos > next)
                        result.put(URLDecoder.decode(qs.substring(last, next), "utf-8"), "");
                    else
                        result.put(URLDecoder.decode(qs.substring(last, eqPos), "utf-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e); // will never happen, utf-8 support is mandatory for java
                }
            }
            last = next + 1;
        }
        return result;
    }

    String requestBodyToString(HttpExchange httpExchange) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = httpExchange.getRequestBody().read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString("UTF-8");
    }
}
