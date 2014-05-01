package ar.daf.foto.inspector.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

@Entity
@Table(name="ALBUM", indexes={@Index(name="ALBUM_FILENAME_INDEX", columnList="PATH,FILENAME", unique=true), @Index(name="ALBUM_HASH_ID_INDEX", columnList="HASH_ID", unique=true)})
public class Album {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="ALBUM_ID")
	private Long id;

	@Embedded
	private AlbumInfo info;

	@Column(name="TITULO")
	@NotNull
	@Length(min=0, max=128)
	private String titulo;
	@Column(name="DESCRIPCION")
	@Length(min=0, max=4096)
	private String descripcion;
	@Column(name="TAGS")
	@Length(min=0, max=1024)
	private String tags;
	@Column(name="FECHA")
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fecha;
	@Embedded
	private Ubicacion ubicacion;

	@ManyToOne(optional=true)
	@JoinColumn(name="IMAGEN_PORTADA_ID")
	private Imagen imagenPortada;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="album")
	@NotNull
	private List<Imagen> imagenes;
	
	@Column(name="PATH_BASE")
	@NotNull
	@Length(min=0, max=1024)
	private String pathBase;
	@Column(name="PATH")
	@NotNull
	@Length(min=0, max=1024)
	private String path;
	@Column(name="FILENAME")
	@NotNull
	@Length(min=0, max=128)
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
	public String getPathBase() {
		return pathBase;
	}
	public void setPathBase(String pathBase) {
		this.pathBase = pathBase;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Imagen getImagenPortada() {
		return imagenPortada;
	}
	public void setImagenPortada(Imagen imagenPortada) {
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
