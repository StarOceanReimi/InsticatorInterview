package me.interview;

import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Set;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import me.interview.tools.WhitespaceFilter;

public class App  {
	
	@SuppressWarnings({"rawtypes"})
	private static void ignoreJstlSystemUri() {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class<?> tldScannerClass = loader.loadClass("org.apache.jasper.runtime.TldScanner");
		    Field f = tldScannerClass.getDeclaredField("systemUris");
		    f.setAccessible(true);
		    ((Set)f.get(null)).clear();
		} catch (Exception e) {
		    throw new RuntimeException("Could not clear TLD system uris.",e);
		}
	}

	private static Server createServer(ClassLoader currentClassLoader, int port) throws IOException, URISyntaxException {
		//jstl uri not found problem
		ignoreJstlSystemUri();
		
		Server server = new Server(port);
		server.setStopAtShutdown(false);
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setErrorHandler(null);
		ClassPathResource resource = new ClassPathResource("webcontext/");
		contextHandler.setResourceBase(resource.getURI().toString());
		contextHandler.setContextPath("/");
		contextHandler.setClassLoader(currentClassLoader);
		
		ServletHolder jspServlet = new ServletHolder(new JspServlet());
		jspServlet.setInitParameter("trimSpaces", "true");
		contextHandler.addServlet(jspServlet, "*.jsp");
		
		//spring web context
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocations("me.interview.springconfig");
		context.setServletContext(contextHandler.getServletContext());
		ContextLoaderListener listener = new ContextLoaderListener(context);
		contextHandler.addEventListener(listener);
		
		DispatcherServlet servlet = new DispatcherServlet(context);
		contextHandler.addServlet(new ServletHolder(servlet), "/");
		FilterHolder encodingFilter = new FilterHolder(new CharacterEncodingFilter("UTF8"));
		contextHandler.addFilter(encodingFilter, "/*", EnumSet.of(REQUEST, FORWARD));
		
		OpenEntityManagerInViewFilter entityManagerFilter = new OpenEntityManagerInViewFilter();
		entityManagerFilter.setEntityManagerFactoryBeanName("entityManagerFactory");
		FilterHolder openEntityInViewFilter = new FilterHolder(entityManagerFilter);
		contextHandler.addFilter(openEntityInViewFilter, "/*", EnumSet.of(REQUEST, FORWARD));
		
		//good for ui, and save network band
		FilterHolder whiteSpaceFilter = new FilterHolder(new WhitespaceFilter());
		contextHandler.addFilter(whiteSpaceFilter, "/*", EnumSet.of(REQUEST));
		
		server.setHandler(contextHandler);
		return server;
	}
	
	public static void main(String[] args) throws Exception  {
		
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		Server server = createServer(currentClassLoader, 8080);
		server.start();
		server.join();
	}

}
