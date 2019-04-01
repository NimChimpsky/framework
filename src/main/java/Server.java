import config.ApplicationContext;
import config.GET;
import config.UrlProcessor;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.servlet.ServletException;
import java.lang.annotation.Annotation;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static io.undertow.Handlers.resource;
import static io.undertow.servlet.Servlets.*;

public class Server {

    public static final String CONTEXT = "context";
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(final String[] args) {
        Integer port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[1]);
        }

        LOGGER.info("Starting the webserver on port {} ", port);
        ApplicationContext applicationContext = new ExampleContext();
        DeploymentInfo servletBuilder = deployment()
                .setClassLoader(Server.class.getClassLoader())
                .setContextPath(applicationContext.getPath())
                .setDeploymentName("api.war")
                .addServletContextAttribute(CONTEXT, applicationContext)
                .addServlets(
                        servlet("RootServlet", RootServlet.class)
                                .addMapping("/*")

                );

        DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        HttpHandler servletHandler = null;
        try {
            servletHandler = manager.start();
        } catch (ServletException e) {
            LOGGER.error("DeploymentManager failed to start, contact support");
            throw new RuntimeException(e);
        }
        UrlProcessor urlProcessor = new UrlProcessor();
        Set<TypeElement> annotations = new HashSet<>();
        annotations.add(GET.class);
        RoundEnvironment roundEnvironment = new RoundEnvironment() {
            @Override
            public boolean processingOver() {
                return false;
            }

            @Override
            public boolean errorRaised() {
                return false;
            }

            @Override
            public Set<? extends Element> getRootElements() {
                return null;
            }

            @Override
            public Set<? extends Element> getElementsAnnotatedWith(TypeElement a) {
                return null;
            }

            @Override
            public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a) {
                return null;
            }
        }
        urlProcessor.process(annotations, roundEnvironment);
        Undertow server = Undertow.builder()
                                  .addHttpListener(port, "localhost")
                                  .setHandler(Handlers.path()
                                                      .addPrefixPath(applicationContext.getPath(), servletHandler)
                                                      .addExactPath("/", resource(new PathResourceManager(Paths.get("src/main/resources/"), 100))
                                                              .setDirectoryListingEnabled(true)) // resolves index.html
                                                      .addExactPath("/js/ajax.js", resource(new PathResourceManager(Paths
                                                              .get("src/main/resources/js/ajax.js"), 100))
                                                              .setDirectoryListingEnabled(true))
                                                      .addExactPath("/js/frappe.js", resource(new PathResourceManager(Paths
                                                              .get("src/main/resources/js/frappe.js"), 100))
                                                              .setDirectoryListingEnabled(true))
                                                      .addExactPath("/js/metriculous.js", resource(new PathResourceManager(Paths
                                                              .get("src/main/resources/js/metriculous.js"), 100))
                                                              .setDirectoryListingEnabled(true))
                                                      .addExactPath("/js/file/file.js", resource(new PathResourceManager(Paths
                                                              .get("src/main/resources/js/file/file.js"), 100))
                                                              .setDirectoryListingEnabled(true))
                                                      .addExactPath("/js/person/person.js", resource(new PathResourceManager(Paths
                                                              .get("src/main/resources/js/person/person.js"), 100))
                                                              .setDirectoryListingEnabled(true))
                                                      .addExactPath("/css/metriculous.css", resource(new PathResourceManager(Paths
                                                              .get("src/main/resources/css/metriculous.css"), 100))
                                                              .setDirectoryListingEnabled(true)))
                                  //.setHandler(servletHandler)
                                  .build();
        server.start();


    }

}
