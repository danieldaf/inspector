package ar.daf.foto.inspector.file;

import java.util.List;


/**
 * 
 * @author daniel
 *
 */
public interface AlbumInspector {
	
	public List<AlbumFile> inspeccionar();
	
	public AlbumFile actualizarAlbum(AlbumFile album);
	
	public AlbumFile armarAlbum(String path);
	
	public List<AlbumFile> armarAlbumes(String path);
	
	public void actualizarIds(List<AlbumFile> albumes);
	
//	public String getPathBase() {
//		return pathBase;
//	}
//
//	public void setPathBase(String pathBase) {
//		this.pathBase = pathBase;
//	}
//
//	public int getAlbumVersionMayor() {
//		return albumVersionMayor;
//	}
//
//	public int getAlbumVersionMenor() {
//		return albumVersionMenor;
//	}
//
//	public int getAlbumVersionRevision() {
//		return albumVersionRevision;
//	}
//
//	public List<AlbumFile> getAlbumes() {
//		return albumes;
//	}
//
//	public String getDbTextFileName() {
//		return dbTextFileName;
//	}
//
//	public String getDbFileEncoding() {
//		return dbFileEncoding;
//	}
//
//	public String[] getExtensiones() {
//		return extensiones;
//	}
	
}