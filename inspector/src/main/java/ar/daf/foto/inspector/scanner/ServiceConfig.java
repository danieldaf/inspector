package ar.daf.foto.inspector.scanner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ServiceConfig {

	@JsonIgnoreProperties
	@Value("${inspector.albumVersionMayor}")
	private int albumVersionMayor = 0;
	@JsonIgnoreProperties
	@Value("${inspector.albumVersionMenor}")
	private int albumVersionMenor = 1;
	@JsonIgnoreProperties
	@Value("${inspector.albumVersionRevision}")
	private int albumVersionRevision = 0;
	
	@Value("${inspector.dbTextFileName}")
	private String dbTextFileName = ".infoAlbum.json.txt";
	@Value("${inspector.dbFileEncoding}")
	private String dbFileEncoding = "utf8";
	private List<String> extensiones;
	
	private List<String> paths;
	
	{
		extensiones = new ArrayList<>();
		extensiones.add("JPEG");
		extensiones.add("JPG");
		extensiones.add("PNG");
		extensiones.add("GIF");
		extensiones.add("BMP");
	}

	public int getAlbumVersionMayor() {
		return albumVersionMayor;
	}

	public void setAlbumVersionMayor(int albumVersionMayor) {
		this.albumVersionMayor = albumVersionMayor;
	}

	public int getAlbumVersionMenor() {
		return albumVersionMenor;
	}

	public void setAlbumVersionMenor(int albumVersionMenor) {
		this.albumVersionMenor = albumVersionMenor;
	}

	public int getAlbumVersionRevision() {
		return albumVersionRevision;
	}

	public void setAlbumVersionRevision(int albumVersionRevision) {
		this.albumVersionRevision = albumVersionRevision;
	}

	public String getDbTextFileName() {
		return dbTextFileName;
	}

	public void setDbTextFileName(String dbTextFileName) {
		this.dbTextFileName = dbTextFileName;
	}

	public String getDbFileEncoding() {
		return dbFileEncoding;
	}

	public void setDbFileEncoding(String dbFileEncoding) {
		this.dbFileEncoding = dbFileEncoding;
	}

	public List<String> getExtensiones() {
		return extensiones;
	}
	
	@Value("${inspector.imageExtensions}")
	public void setStrSExtensiones(String exts) {
		if (exts != null && !exts.trim().isEmpty()) {
			String[] tmp = exts.split(",");
			if (tmp != null && tmp.length != 0) {
				this.extensiones.clear();
				for (String e : tmp)
					this.extensiones.add(e.trim().toUpperCase());
			}
		}
	}

	public void setExtensiones(List<String> extensiones) {
		this.extensiones = extensiones;
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}
}
