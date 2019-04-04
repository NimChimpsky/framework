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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RootServlet extends HttpServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Function<Map<String, String>, String>> requestMappingsGet;
    private Map<String, Function<Map<String, String>, String>> requestMappingsDelete;
    private Map<String, Function<String, String>> requestMappingsPost;
    private Map<String, Function<String, String>> requestMappingsPut;
    private ApplicationContext applicationContext;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        applicationContext = (ApplicationContext) servletContext.getAttribute(ApplicationContext.getContext());
        requestMappingsGet = applicationContext.requestMappingGet();
        requestMappingsPost = applicationContext.requestMappingPost();
        requestMappingsPut = applicationContext.requestMappingPut();
        requestMappingsDelete = applicationContext.requestMappingDelete();
    }

    @Override
    protected void doGet(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
        String url = httpServletRequest.getRequestURI();
        String key = url.replace(ApplicationContext.getPath(), "");

        Function<Map<String, String>, String> controller = requestMappingsGet.get(key);
        String jsonBody = controller.apply(extractParameterMap(httpServletRequest.getParameterMap()));
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        response.setStatus(200);
        out.write(jsonBody);
        out.close();
    }

    @Override
    protected void doDelete(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
        String url = httpServletRequest.getRequestURI();
        String key = url.replace(ApplicationContext.getPath(), "");
        Function<Map<String, String>, String> controller = requestMappingsDelete.get(key);
        String jsonBody = controller.apply(extractParameterMap(httpServletRequest.getParameterMap()));
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        response.setStatus(200);
        out.write(jsonBody);
        out.close();
    }


    private Map<String, String> extractParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String> tidyParameterMap = new HashMap<>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            tidyParameterMap.put(entry.getKey(), entry.getValue()[0]);
        }
        return tidyParameterMap;

    }

    @Override
    protected void doPost(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
        String url = httpServletRequest.getRequestURI();
        String requestBody = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String key = url.replace(ApplicationContext.getPath(), "");
        Function<String, String> controller = requestMappingsPost.get(key);
        String jsonBody = controller.apply(requestBody);

        response.setContentType("application/json");
        response.setStatus(200);
        PrintWriter out = response.getWriter();
        out.write(jsonBody);
        out.close();
    }

    @Override
    protected void doPut(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
        String url = httpServletRequest.getRequestURI();
        String requestBody = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String key = url.replace(ApplicationContext.getPath(), "");
        Function<String, String> controller = requestMappingsPut.get(key);
        String jsonBody = controller.apply(requestBody);
        response.setContentType("application/json");
        response.setStatus(200);
        PrintWriter out = response.getWriter();
        out.write(jsonBody);
        out.close();
    }

}
