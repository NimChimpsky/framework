package sample;

import config.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RootServlet extends HttpServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Function<String, String>> requestMapGet;
    private Map<String, Function<String, String>> requestMapPost;
    private ApplicationContext applicationContext;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        applicationContext = (ApplicationContext) servletContext.getAttribute(ApplicationContext.getContext());

        requestMapGet = applicationContext.requestMappingGet();
        requestMapPost = applicationContext.requestMappingPost();
    }

    @Override
    protected void doGet(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
        String url = httpServletRequest.getRequestURI();
        String key = url.replace(ApplicationContext.getPath(), "");
        String queryString = httpServletRequest.getQueryString();
        Function<String, String> controller = requestMapGet.get(key);
        String jsonBody = controller.apply(queryString);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        response.setStatus(200);
        out.write(jsonBody);
        out.close();
    }

    @Override
    protected void doPost(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
        String url = httpServletRequest.getRequestURI();
        String requestBody = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        Function<String, String> controller = requestMapPost.get(url);
        String jsonBody = controller.apply(requestBody);

        response.setContentType("application/json");
        response.setStatus(200);
        PrintWriter out = response.getWriter();
        out.write(jsonBody);
        out.close();
    }

}
