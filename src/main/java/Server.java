import config.Context;
import config.RootServlet;
import config.SampleDependencyProvider;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.undertow.Handlers.resource;
import static io.undertow.servlet.Servlets.*;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(final String[] args) {
        Integer port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[1]);
        }

        LOGGER.info("Starting the webserver on port {} ", port);
        Context context = new Context(new SampleDependencyProvider());
        DeploymentInfo servletBuilder = deployment()
                .setClassLoader(Server.class.getClassLoader())
                .setContextPath(Context.getContext())
                .setDeploymentName("api.war")
                .addServletContextAttribute(Context.getContext(), context)
                .addServlets(
                        servlet("config.RootServlet", RootServlet.class)
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
                                  .setHandler(createHandler(servletHandler))
                                  //.setHandler(servletHandler)
                                  .build();
        server.start();
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();

//        Path index = Paths.get("src/main/resources/Index.html");
        Path index = Paths.get("/Index.html");
        File file = index.toFile();

        LOGGER.info("can read " + file.canRead());
        LOGGER.info("name  " + file.getName());


    }

    private static HttpHandler createHandler(HttpHandler servletHandler) {
        return Handlers.path()
                       .addExactPath("/", resource(new PathResourceManager(Paths.get("src/main/resources/Index.html"), 100))
                               .setDirectoryListingEnabled(false)) // resolves index.html
                       .addPrefixPath(Context.getPath(), servletHandler)
                       .addPrefixPath("/static", resource(new PathResourceManager(Paths.get("src/main/resources/"))));

    }

}
