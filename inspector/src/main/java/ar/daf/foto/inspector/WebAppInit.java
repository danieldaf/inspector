package ar.daf.foto.inspector;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import ar.daf.foto.inspector.config.CoreConfig;

public class WebAppInit implements WebApplicationInitializer {
	
	public static String DEFAULT_SERVLET_CONTEXT = "/";

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(CoreConfig.class);
		rootContext.refresh();
		
		ServletRegistration.Dynamic appServlet = servletContext.addServlet("defaultServlet", new DispatcherServlet(rootContext));
		appServlet.setLoadOnStartup(1);
		Set<String> mappingConflicts = appServlet.addMapping(DEFAULT_SERVLET_CONTEXT);
		if (!mappingConflicts.isEmpty()) {
//			for (String s : mappingConflicts) {
//				LOG.error("Mapping conflict: " + s);
//			}
			throw new IllegalStateException("'webservice' cannot be mapped to '"+DEFAULT_SERVLET_CONTEXT+"'");
		}		
		
		servletContext.addListener(new ContextLoaderListener(rootContext));
		servletContext.setInitParameter("defaultHtmlScape",Boolean.TRUE.toString());
	}

}