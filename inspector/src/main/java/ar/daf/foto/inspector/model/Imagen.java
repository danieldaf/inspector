package ar.daf.foto.inspector.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name="IMAGEN", indexes={@Index(name="IMAGEN_FILENAME_INDEX", columnList="ALBUM_ID,FILENAME", unique=true)})
public class Imagen {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="IMAGEN_ID")
	private Long id;

	@Column(name="TITULO")
	@Length(min=0, max=128)
	private String titulo;
	@Column(name="DESCRIPCION")
	@Length(min=0, max=4096)
	private String descripcion;
	@Column(name="TAGS")
	@Length(min=0, max=1024)
	private String tags;

	@Column(name="FILENAME")
	@NotNull
	@Length(min=0, max=128)
	private String fileName;
	@Column(name="FILENAME_SMALL")
	@Length(min=0, max=128)
	private String fileNameSmall;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="ALBUM_ID")
	@NotNull
	private Album album;

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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Album getAlbum() {
		return album;
	}
	public void setAlbum(Album album) {
		this.album = album;
	}
}
