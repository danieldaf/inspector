package ar.daf.foto.inspector.syncro;

import ar.daf.foto.inspector.model.Imagen;

public class SyncImagenDto {
	
	private String titulo;
	private String descripcion;
	private String tags;
	
	public static SyncImagenDto buildFromImagen(Imagen imagen) {
		SyncImagenDto result = null;
		if (imagen != null) {
			result = new SyncImagenDto();
			result.setTitulo(imagen.getTitulo());
			result.setDescripcion(imagen.getDescripcion());
			result.setTags(imagen.getTags());
		}
		return result;
	}

	private String fileName;
	
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
}
