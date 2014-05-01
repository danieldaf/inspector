package ar.daf.foto.inspector.consulta;

import org.springframework.hateoas.ResourceSupport;

import ar.daf.foto.inspector.model.Imagen;


public class QImagenDto extends ResourceSupport {

	private String fileName;

	private String titulo;
	private String descripcion;
	private String tags;
	
	public static QImagenDto fromImagen(Imagen imagen) {
		QImagenDto result = null;
		if (imagen != null) {
			result = new QImagenDto();
			result.setFileName(imagen.getFileName());
			result.setTitulo(imagen.getTitulo());
			result.setTags(imagen.getTags());
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
}
