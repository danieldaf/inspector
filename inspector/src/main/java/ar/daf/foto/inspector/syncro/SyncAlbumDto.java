package ar.daf.foto.inspector.syncro;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.Imagen;

public class SyncAlbumDto {
	
	private SyncAlbumInfoDto info;

	private String titulo;
	private String descripcion;
	private String tags;
	private DateTime fecha;
	private SyncUbicacionDto ubicacion;

	private String imagenPortada;

	private List<SyncImagenDto> imagenes;
	
	private String path;
	private String fileName;
	
	public static SyncAlbumDto fromAlbum(Album album) {
		SyncAlbumDto result = null;
		if (album != null) {
			result = new SyncAlbumDto();
			result.setInfo(SyncAlbumInfoDto.fromAlbum(album.getInfo()));
			result.setTitulo(album.getTitulo());
			result.setTags(album.getTags());
			result.setFecha(album.getFecha());
			result.setUbicacion(SyncUbicacionDto.fromUbicacion(album.getUbicacion()));
			if (album.getImagenPortada() != null) {
				result.setImagenPortada(album.getImagenPortada().getFileName());
			}
			result.setPath(album.getPath());
			result.setFileName(album.getFileName());
			result.setImagenes(new ArrayList<SyncImagenDto>());
			for (Imagen imagen : album.getImagenes()) {
				result.getImagenes().add(SyncImagenDto.buildFromImagen(imagen));
			}
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
	public SyncUbicacionDto getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(SyncUbicacionDto ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<SyncImagenDto> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<SyncImagenDto> imagenes) {
		this.imagenes = imagenes;
	}
	public SyncAlbumInfoDto getInfo() {
		return info;
	}
	public void setInfo(SyncAlbumInfoDto info) {
		this.info = info;
	}
}
