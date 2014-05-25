package ar.daf.foto.inspector.jasper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import ar.daf.foto.inspector.model.AlbumDao;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BuildReportesImpl {

	@Autowired
	private AlbumDao albumDao;
	
	public void buildReporte() throws IOException {
		ClassPathResource resXml = new ClassPathResource("classpath:reportes/reporterEso.jasper");
		InputStream reporteXml = resXml.getInputStream();
		JasperPrint
	}
}
