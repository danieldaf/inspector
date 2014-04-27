package ar.daf.foto.inspector.file;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.Imagen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={"id", "actualizado", "actualizar", "path", "fileName"})
public class AlbumFile {
	
	private Long id = null;
	private boolean actualizado = false;
	private boolean actualizar = false;
	
	private AlbumInfoFile info;

	private String titulo;
	private String descripcion;
	private String tags;
	private DateTime fecha;
	private UbicacionFile ubicacion;
	private String imagenPortada;
	
	private List<ImagenFile> imagenes;
	
	private String path;
	private String fileName;
	
	public static AlbumFile fromAlbum(Album album) {
		AlbumFile result = null;
		if (album != null) {
			result = new AlbumFile();
			result.setId(album.getId());
			result.setInfo(AlbumInfoFile.fromAlbumInfo(album.getInfo()));
			result.setTitulo(album.getTitulo());
			result.setDescripcion(album.getDescripcion());
			result.setTags(album.getTags());
			result.setFecha(album.getFecha());
			result.setUbicacion(UbicacionFile.fromUbicacion(album.getUbicacion()));
			result.setImagenPortada(album.getImagenPortada()!=null?album.getImagenPortada().getFileName():null);
			result.setPath(album.getPath());
			result.setFileName(album.getFileName());
			result.setImagenes(new ArrayList<ImagenFile>());
			if (album.getImagenes() != null && !album.getImagenes().isEmpty()) {
				for (Imagen img : album.getImagenes()) {
					ImagenFile imgF = ImagenFile.fromImagen(img);
					result.getImagenes().add(imgF);
				}
			}
		}
		return result;
	}

	public static Album toAlbum(AlbumFile album) {
		Album result = null;
		if (album != null) {
			result = new Album();
			result.setId(album.getId());
			result.setInfo(AlbumInfoFile.toAlbumInfo(album.getInfo()));
			result.setTitulo(album.getTitulo());
			result.setDescripcion(album.getDescripcion());
			result.setTags(album.getTags());
			result.setFecha(album.getFecha());
			result.setUbicacion(UbicacionFile.toUbicacion(album.getUbicacion()));
			if (album.getImagenPortada()!=null) {
				Imagen imgP = new Imagen();
				imgP.setFileName(album.getImagenPortada());
				result.setImagenPortada(imgP);
			} else {
				result.setImagenPortada(null);
			}
			result.setPath(album.getPath());
			result.setFileName(album.getFileName());
			result.setImagenes(new ArrayList<Imagen>());
			if (album.getImagenes() != null && !album.getImagenes().isEmpty()) {
				for (ImagenFile imgF : album.getImagenes()) {
					Imagen img = ImagenFile.toImagen(imgF);
					img.setAlbum(result);
					result.getImagenes().add(img);
				}
			}
		}
		return result;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isActualizado() {
		return actualizado;
	}
	public void setActualizado(boolean actualizado) {
		this.actualizado = actualizado;
	}
	public boolean isActualizar() {
		return actualizar;
	}
	public void setActualizar(boolean actualizar) {
		this.actualizar = actualizar;
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
	public UbicacionFile getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(UbicacionFile ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<ImagenFile> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<ImagenFile> imagenes) {
		this.imagenes = imagenes;
	}
	public AlbumInfoFile getInfo() {
		return info;
	}
	public void setInfo(AlbumInfoFile info) {
		this.info = info;
	}
}
