package me.interview;

import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumSet;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class App  {

	private static Server createServer(ClassLoader currentClassLoader, int port) throws IOException, URISyntaxException {
		Server server = new Server(port);
		server.setStopAtShutdown(false);
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setErrorHandler(null);
		ClassPathResource resource = new ClassPathResource("webcontext/");
		contextHandler.setResourceBase(resource.getURI().toString());
		contextHandler.setContextPath("/");
		contextHandler.setClassLoader(currentClassLoader);
		contextHandler.addServlet(JspServlet.class, "*.jsp");
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocations("me.interview.springconfig");
		DispatcherServlet servlet = new DispatcherServlet(context);
		contextHandler.addServlet(new ServletHolder(servlet), "/");
		FilterHolder encodingFilter = new FilterHolder(new CharacterEncodingFilter("UTF8"));
		contextHandler.addFilter(encodingFilter, "/*", EnumSet.of(REQUEST, FORWARD));
		server.setHandler(contextHandler);
//		new Thread(new ServerRestarter(server, currentClassLoader)).start();
		return server;
	}
	
	public static void main(String[] args) throws Exception  {
		
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		Server server = createServer(currentClassLoader, 8080);
		server.start();
		server.join();
		
		
	}

}
