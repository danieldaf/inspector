package ar.daf.foto.inspector.model;

import java.util.List;

public interface AlbumDao {

	List<Album> actualizarAlbumes(List<Album> listaAlbumes);
	List<Album> recuperarAlbumes();
	List<Album> recuperarAlbumes(List<String> hashIdAlbumes);
	
	Album recuperarAlbum(Long id);
//	Album recuperarAlbum(String path, String fileName);
	Album recuperarAlbum(String hashId);
	Album actualizarAlbum(Album album);
	
//	Imagen recuperarImagen(String path, String fileNameAlbum, String fileNameImagen);
	Imagen recuperarImagen(String hashId, String fileNameImagen);
}
