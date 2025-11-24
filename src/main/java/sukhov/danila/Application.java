package sukhov.danila;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import sukhov.danila.in.web.*;

public class Application {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new ProductServlet()), "/api/products/*");
        context.addServlet(new ServletHolder(new BrandServlet()), "/api/brands/*");
        context.addServlet(new ServletHolder(new AuthServlet()), "/api/auth/*");

        server.setHandler(context);
        server.start();
        server.join();
    }
}