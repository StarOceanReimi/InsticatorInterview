package me.interview;

import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;

import org.apache.jasper.runtime.TldScanner;
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

	@SuppressWarnings("rawtypes")
	private static void ignoreJstlSystemUri() {
		try {
		    Field f = TldScanner.class.getDeclaredField("systemUris");
		    f.setAccessible(true);
		    ((Set)f.get(null)).clear();
		} catch (Exception e) {
		    throw new RuntimeException("Could not clear TLD system uris.",e);
		}
	}
	
	public static void main(String[] args) throws Exception  {
		
		ignoreJstlSystemUri();
		Server server = new Server(8080);
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setErrorHandler(null);
		ClassPathResource resource = new ClassPathResource("webcontext/");
		contextHandler.setResourceBase(resource.getURI().toString());
		contextHandler.setContextPath("/");
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		contextHandler.setClassLoader(currentClassLoader);
		contextHandler.addServlet(JspServlet.class, "*.jsp");
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocations("me.interview.springconfig");
		DispatcherServlet servlet = new DispatcherServlet(context);
		contextHandler.addServlet(new ServletHolder(servlet), "/");
		contextHandler.addFilter(new FilterHolder(new CharacterEncodingFilter("utf8")), "/*", EnumSet.of(REQUEST, FORWARD));
		server.setHandler(contextHandler);
		server.start();
		server.join();
	}

}
