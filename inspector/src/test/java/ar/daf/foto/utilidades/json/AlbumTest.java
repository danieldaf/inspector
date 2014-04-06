package ar.daf.foto.utilidades.json;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import ar.daf.foto.utilidades.json.JsonClass;
import ar.daf.foto.utilidades.json.JsonDateProperty;
import ar.daf.foto.utilidades.json.JsonProperty;

@JsonClass
public class AlbumTest {
	@JsonProperty
	private String titulo;
	@JsonProperty
	private String descripcion;
	@JsonProperty
	private String tags;
	@JsonDateProperty
	private DateTime fecha;
	@JsonProperty
	private UbicacionTest ubicacion;
	@JsonProperty
	private String imagenPortada;
	
	@JsonProperty(typeClass=ArrayList.class)
	private List<ImagenTest> imagenes;
	
	private String path;
	private String filename;
	private DateTime fechaActualizacion;
	private String contentHash;

	public DateTime getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void setFechaActualizacion(DateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getContentHash() {
		return contentHash;
	}
	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
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
	public UbicacionTest getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(UbicacionTest ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public List<ImagenTest> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<ImagenTest> imagenes) {
		this.imagenes = imagenes;
	}
}
