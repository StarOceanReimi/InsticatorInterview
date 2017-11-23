package me.interview;

import java.lang.reflect.Field;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class DevSiteInitializer implements WebApplicationInitializer {

	static Logger logger = LoggerFactory.getLogger(DevSiteInitializer.class);
	
	@SuppressWarnings("rawtypes")
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
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		ignoreJstlSystemUri();
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocations("me.interview.springconfig");
		ContextLoaderListener listener = new ContextLoaderListener(context);
		servletContext.addListener(listener);
		servletContext.setInitParameter("defaultHtmlEscape", "true");
		DispatcherServlet dispatcher = new DispatcherServlet(context);
		ServletRegistration.Dynamic registry = servletContext.addServlet("springDispatcher", dispatcher);
		Set<String> conflicts = registry.addMapping("/");
		if (!conflicts.isEmpty()) {
			for (String s : conflicts) {
				logger.error("Mapping conflict: " + s);
			}
			throw new IllegalStateException(
					"'webservice' cannot be mapped to '/'");
		}
	}

}
