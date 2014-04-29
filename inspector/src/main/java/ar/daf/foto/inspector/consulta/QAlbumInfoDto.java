package ar.daf.foto.inspector.consulta;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.utilidades.ImagenUtils;

public class QAlbumInfoDto {

	private String path;
	private String fileName;

	private String titulo;
	private String descripcion;
	private String tags;
	private DateTime fecha;
	private QUbicacionDto ubicacion;

	private String urlImagenPortada;
	
	public static QAlbumInfoDto fromAlbum(Album album) {
		QAlbumInfoDto result = null;
		if (album != null) {
			result = new QAlbumInfoDto();
			result.setPath(album.getPath());
			result.setFileName(album.getFileName());
			result.setTitulo(album.getTitulo());
			result.setDescripcion(album.getDescripcion());
			result.setTags(album.getTags());
			result.setFecha(album.getFecha());
			result.setUbicacion(QUbicacionDto.fromUbicacion(album.getUbicacion()));
			
			String urlImagenPortada = ImagenUtils.armarUrlMiniatura(album.getImagenPortada());
			
			result.setUrlImagenPortada(urlImagenPortada);
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

	public String getUrlImagenPortada() {
		return urlImagenPortada;
	}

	public void setUrlImagenPortada(String urlImagenPortada) {
		this.urlImagenPortada = urlImagenPortada;
	}
	
}
