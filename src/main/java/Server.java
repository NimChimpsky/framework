import config.ApplicationContext;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.nio.file.Paths;

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
