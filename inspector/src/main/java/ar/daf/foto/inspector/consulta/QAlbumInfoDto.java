package ar.daf.foto.inspector.consulta;

import org.joda.time.DateTime;
import org.springframework.hateoas.ResourceSupport;

import ar.daf.foto.inspector.model.Album;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"fileNameImagenPortada"})
public class QAlbumInfoDto extends ResourceSupport {

	private String path;
	private String fileName;
	private String hashId;

	private String titulo;
	private String descripcion;
	private String tags;
	private DateTime fecha;
	private QUbicacionDto ubicacion;
	
	private String fileNameImagenPortada;
	
	public static QAlbumInfoDto fromAlbum(Album album) {
		QAlbumInfoDto result = null;
		if (album != null) {
			result = new QAlbumInfoDto();
			result.setPath(album.getPath());
			result.setFileName(album.getFileName());
			result.setHashId(album.getInfo().getHashId());
			result.setTitulo(album.getTitulo());
			result.setDescripcion(album.getDescripcion());
			result.setTags(album.getTags());
			result.setFecha(album.getFecha());
			result.setUbicacion(QUbicacionDto.fromUbicacion(album.getUbicacion()));
			
			if (album.getImagenPortada() != null)
				result.setFileNameImagenPortada(album.getImagenPortada().getFileName());
		}
		return result;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getHashId() {
		return hashId;
	}
	
	public void setHashId(String hashId) {
		this.hashId = hashId;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public DateTime getFecha() {
		return fecha;
	}

	public void setFecha(DateTime fecha) {
		this.fecha = fecha;
	}

	public QUbicacionDto getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(QUbicacionDto ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getFileNameImagenPortada() {
		return fileNameImagenPortada;
	}

	public void setFileNameImagenPortada(String fileNameImagenPortada) {
		this.fileNameImagenPortada = fileNameImagenPortada;
	}

}
