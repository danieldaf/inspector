package ar.daf.foto.inspector.album;

import java.util.List;


/**
 * 
 * @author daniel
 *
 */
public interface AlbumInspector {
	
	public List<AlbumFile> inspeccionar();
	
	public void actualizarIds(List<AlbumFile> albumes);
	
}