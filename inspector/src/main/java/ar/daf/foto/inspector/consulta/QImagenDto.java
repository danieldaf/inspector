package ar.daf.foto.inspector.consulta;

import ar.daf.foto.inspector.model.Imagen;
import ar.daf.foto.utilidades.ImagenUtils;


public class QImagenDto {

	private String fileName;

	private String titulo;
	private String descripcion;
	private String tags;
	
	private String urlImagen;
	private String urlImagenMiniatura;
	
	public static QImagenDto fromImagen(Imagen imagen) {
		QImagenDto result = null;
		if (imagen != null) {
			result = new QImagenDto();
			result.setFileName(imagen.getFileName());
			result.setTitulo(imagen.getTitulo());
			result.setTags(imagen.getTags());
			
			String urlImagen = ImagenUtils.armarUrl(imagen);
			String urlMiniatura = ImagenUtils.armarUrlMiniatura(imagen);
			result.setUrlImagen(urlImagen);
			result.setUrlImagenMiniatura(urlMiniatura);
		}
		return result;
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
	public String getUrlImagen() {
		return urlImagen;
	}
	public void setUrlImagen(String urlImagen) {
		this.urlImagen = urlImagen;
	}
	public String getUrlImagenMiniatura() {
		return urlImagenMiniatura;
	}
	public void setUrlImagenMiniatura(String urlImagenMiniatura) {
		this.urlImagenMiniatura = urlImagenMiniatura;
	}
}
