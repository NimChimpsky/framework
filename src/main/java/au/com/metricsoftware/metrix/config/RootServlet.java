//package config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//public class RootServlet extends HttpServlet {
//    protected final Logger logger = LoggerFactory.getLogger(getClass());
//    private Map<String, Function<Map<String, String>, String>> requestMappingsGet;
//    private Map<String, Function<Map<String, String>, String>> requestMappingsDelete;
//    private Map<String, BiFunction<Map<String, String>, String, String>> requestMappingsPost;
//    private Map<String, BiFunction<Map<String, String>, String, String>> requestMappingsPut;
//    private Context context;
//
//    @Override
//    public void init() {
//        ServletContext servletContext = getServletContext();
//        context = (Context) servletContext.getAttribute(Context.getContext());
//        requestMappingsGet = context.requestMappingGet();
//        requestMappingsPost = context.requestMappingPost();
//        requestMappingsPut = context.requestMappingPut();
//        requestMappingsDelete = context.requestMappingDelete();
//    }
//
//    @Override
//    protected void doGet(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
//        queryParametersOnly(httpServletRequest, response, requestMappingsGet);
//    }
//
//    @Override
//    protected void doDelete(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
//        queryParametersOnly(httpServletRequest, response, requestMappingsDelete);
//    }
//
//    @Override
//    protected void doPost(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
//        withJsonRequestBody(httpServletRequest, response, requestMappingsPost);
//    }
//
//    @Override
//    protected void doPut(final HttpServletRequest httpServletRequest, final HttpServletResponse response) throws IOException {
//        withJsonRequestBody(httpServletRequest, response, requestMappingsPut);
//    }
//
//    private void queryParametersOnly(HttpServletRequest httpServletRequest, HttpServletResponse response, Map<String, Function<Map<String, String>, String>> requestMappingsGet) throws IOException {
//        String url = httpServletRequest.getRequestURI();
//        String key = url.replace(Context.getPath(), "");
//        Function<Map<String, String>, String> controller = requestMappingsGet.get(key);
//        String jsonBody = controller.apply(extractParameterMap(httpServletRequest.getParameterMap()));
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
//        response.setStatus(200);
//        out.write(jsonBody);
//        out.close();
//    }
//
//    private void withJsonRequestBody(HttpServletRequest httpServletRequest, HttpServletResponse response, Map<String, BiFunction<Map<String, String>, String, String>> requestMappings) throws IOException {
//        String url = httpServletRequest.getRequestURI();
//        String key = url.replace(Context.getPath(), "");
//        String requestBody = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//        Map<String, String> parameterMap = extractParameterMap(httpServletRequest.getParameterMap());
//        BiFunction<Map<String, String>, String, String> controller = requestMappings.get(key);
//        String jsonBody = controller.apply(parameterMap, requestBody);
//        response.setContentType("application/json");
//        response.setStatus(200);
//        PrintWriter out = response.getWriter();
//        out.write(jsonBody);
//        out.close();
//    }
//
//    private Map<String, String> extractParameterMap(Map<String, String[]> parameterMap) {
//        Map<String, String> tidyParameterMap = new HashMap<>(parameterMap.size());
//        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//            tidyParameterMap.put(entry.getKey(), entry.getValue()[0]);
//        }
//        return tidyParameterMap;
//    }
//
//}
