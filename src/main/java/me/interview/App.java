package me.interview;

import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
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
	
	private static class ServerRestarter implements Runnable {

		private WatchService service;
		
		private Server server;
		
		private volatile boolean stop = false;
		
		public ServerRestarter(Server server, URI path) {
			super();
			this.server = server;
			try {
				service = FileSystems.getDefault().newWatchService();
				Path root = Paths.get(path);
				Files.walk(root).filter(Files::isDirectory)
					 .forEach(p->{
						try {
							p.register(service, StandardWatchEventKinds.ENTRY_CREATE,
												StandardWatchEventKinds.ENTRY_DELETE,  
												StandardWatchEventKinds.ENTRY_MODIFY);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			
			while(!stop) {
			
				WatchKey key = null;
				try {
					while((key = service.take()) != null) {
						for(WatchEvent<?> event : key.pollEvents()) {
							System.out.printf("Event kind: %s, Filed affected: %s\n", event.kind(), event.context());
						}
						key.reset();
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
			
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
		FilterHolder encodingFilter = new FilterHolder(new CharacterEncodingFilter("UTF8"));
		contextHandler.addFilter(encodingFilter, "/*", EnumSet.of(REQUEST, FORWARD));
		server.setHandler(contextHandler);
		ServerRestarter restarter = new ServerRestarter(server, currentClassLoader.getResource("").toURI());
		new Thread(restarter).start();
		
		server.start();
		server.join();
		restarter.stop = true;
	}

}
