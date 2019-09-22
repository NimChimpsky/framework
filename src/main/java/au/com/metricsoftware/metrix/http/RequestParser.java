package au.com.metricsoftware.metrix.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class RequestParser {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RequestParser() {
    }

    public void parse(URI requestUri) {
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
}
