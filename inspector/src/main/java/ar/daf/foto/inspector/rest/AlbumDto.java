package ar.daf.foto.inspector.rest;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.AlbumInfo;
import ar.daf.foto.inspector.model.Imagen;

public class AlbumDto {

	private String pathBase;
	private String fileName;
	private String contentHash;
	private DateTime fechaActualizacion;

	private String titulo;
	private String descripcion;
	private String tags;
	private DateTime fecha;
	private UbicacionDto ubicacion;
	private ImagenAlbumDto imagenPortada;
	private List<ImagenAlbumDto> imagenes;
	
	public static AlbumDto fromAlbum(Album album) {
		AlbumDto result = null;
		if (album != null) {
			result = new AlbumDto();
			result.setPathBase(album.getPathBase());
			result.setFileName(album.getFileName());
			result.setContentHash(album.getInfo().getContentHash());
			result.setFechaActualizacion(album.getInfo().getFechaActualizacion());
			result.setTitulo(album.getTitulo());
			result.setDescripcion(album.getDescripcion());
			result.setTags(album.getTags());
			result.setFecha(result.getFecha());
			result.setUbicacion(UbicacionDto.fromUbicacion(album.getUbicacion()));
//			result.setImagenPortada(ImagenAlbumDto.fromImagen(album.getImagenPortada()));
			result.setImagenes(new ArrayList<ImagenAlbumDto>());
			if (album.getImagenes() != null && !album.getImagenes().isEmpty()) {
				for (Imagen img : album.getImagenes()) {
					ImagenAlbumDto imgDto = ImagenAlbumDto.fromImagen(img);
					result.getImagenes().add(imgDto);
				}
			}
		}
		return result;
	}
	
	public static Album toAlbum(AlbumDto album) {
		Album result = null;
		if (album != null) {
			result = new Album();
			result.setPathBase(album.getPathBase());
			result.setFileName(album.getFileName());
			AlbumInfo info = new AlbumInfo();
			info.setContentHash(album.getContentHash());
			info.setFechaActualizacion(album.getFechaActualizacion());
			result.setInfo(info);
			result.setTitulo(album.getTitulo());
			result.setDescripcion(album.getDescripcion());
			result.setTags(album.getTags());
			result.setFecha(album.getFecha());
			result.setUbicacion(UbicacionDto.toUbicacion(album.getUbicacion()));
//			result.setImagenPortada(ImagenAlbumDto.toImagen(albumDto.getImagenPortada()));
			result.setImagenes(new ArrayList<Imagen>());
			if (album.getImagenes() != null && !album.getImagenes().isEmpty()) {
				for (ImagenAlbumDto imgDto : album.getImagenes()) {
					Imagen img = ImagenAlbumDto.toImagen(imgDto);
					img.setAlbum(result);
					result.getImagenes().add(img);
				}
			}
		}
		return result;
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

	public DateTime getFecha() {
		return fecha;
	}

	public void setFecha(DateTime fecha) {
		this.fecha = fecha;
	}

	public UbicacionDto getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(UbicacionDto ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getPathBase() {
		return pathBase;
	}
	
	public void setPathBase(String pathBase) {
		this.pathBase = pathBase;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DateTime getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(DateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public ImagenAlbumDto getImagenPortada() {
		return imagenPortada;
	}

	public void setImagenPortada(ImagenAlbumDto imagenPortada) {
		this.imagenPortada = imagenPortada;
	}

	public List<ImagenAlbumDto> getImagenes() {
		return imagenes;
	}

	public void setImagenes(List<ImagenAlbumDto> imagenes) {
		this.imagenes = imagenes;
	}		
}
