package ar.daf.foto.inspector.file;

import ar.daf.foto.inspector.model.Imagen;
import ar.daf.foto.utilidades.json.JsonClass;
import ar.daf.foto.utilidades.json.JsonProperty;

@JsonClass
public class ImagenFile {
	
	@JsonProperty
	private String titulo;
	@JsonProperty
	private String descripcion;
	@JsonProperty
	private String tags;

	@JsonProperty
	private String fileName;
	@JsonProperty
	private String fileNameSmall;
	
	public static ImagenFile fromImagen(Imagen imagen) {
		ImagenFile result = null;
		if (imagen != null) {
			result = new ImagenFile();
			result.setTitulo(imagen.getTitulo());
			result.setDescripcion(imagen.getDescripcion());
			result.setTags(imagen.getTags());
			result.setFileName(imagen.getFileName());
			result.setFileNameSmall(imagen.getFileNameSmall());
		}
		return result;
	}
	
	public static Imagen toImagen(ImagenFile imagen) {
		Imagen result = null;
		if (imagen != null) {
			result = new Imagen();
			result.setTitulo(imagen.getTitulo());
			result.setDescripcion(imagen.getDescripcion());
			result.setTags(imagen.getTags());
			result.setFileName(imagen.getFileName());
			result.setFileNameSmall(imagen.getFileNameSmall());
		}
		return result;
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileNameSmall() {
		return fileNameSmall;
	}
	public void setFileNameSmall(String fileNameSmall) {
		this.fileNameSmall = fileNameSmall;
	}
}
