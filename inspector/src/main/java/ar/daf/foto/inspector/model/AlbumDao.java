package ar.daf.foto.inspector.model;

import java.util.List;

public interface AlbumDao {

	List<Album> actualizarAlbumes(List<Album> listaAlbumes);
	List<Album> recuperarAlbumes();
	
	Album recuperarAlbum(Long id);
	Album recuperarAlbum(String path, String fileName);
	Album actualizarAlbum(Album album);
}
