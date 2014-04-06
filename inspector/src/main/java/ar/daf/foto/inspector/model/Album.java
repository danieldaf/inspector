package ar.daf.foto.inspector.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import ar.daf.foto.utilidades.json.JsonClass;
import ar.daf.foto.utilidades.json.JsonDateProperty;
import ar.daf.foto.utilidades.json.JsonProperty;

@JsonClass
public class Album {
	@JsonProperty
	private AlbumInfo info;
	
	@JsonProperty
	private String titulo;
	@JsonProperty
	private String descripcion;
	@JsonProperty
	private String tags;
	@JsonDateProperty
	private DateTime fecha;
	@JsonProperty
	private Ubicacion ubicacion;
	@JsonProperty
	private String imagenPortada;
	
	@JsonProperty(typeClass=ArrayList.class)
	private List<Imagen> imagenes;
	
	private String path;
	private String filename;

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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getImagenPortada() {
		return imagenPortada;
	}
	public void setImagenPortada(String imagenPortada) {
		this.imagenPortada = imagenPortada;
	}
	public DateTime getFecha() {
		return fecha;
	}
	public void setFecha(DateTime fecha) {
		this.fecha = fecha;
	}
	public Ubicacion getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(Ubicacion ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public List<Imagen> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<Imagen> imagenes) {
		this.imagenes = imagenes;
	}
	public AlbumInfo getInfo() {
		return info;
	}
	public void setInfo(AlbumInfo info) {
		this.info = info;
	}
}
