package ar.daf.foto.inspector.album;

import ar.daf.foto.inspector.album.db.FileInspectorDB;
import ar.daf.foto.inspector.album.fs.AlbumInspectorFSImpl;
import ar.daf.foto.inspector.scanner.ServiceConfig;

/**
 * 
 * @author daniel
 *
 */
public class AlbumInspectorBuilder {
	
	public final static String FILE_PREFIX = "file://";
	public final static String DB_PREFIX = "jdbc://";

	public static AlbumInspector buildAlbumInspector(String pathBase, ServiceConfig config) {
		AlbumInspector result = null;
		if (pathBase != null) {
			String path = pathBase.trim();
			int endIndex = path.indexOf("://");
			String protocolo = null;
			if (endIndex > 0) {
				protocolo = path.substring(0, endIndex+3);
				path = path.substring(protocolo.length());
			}
			if (DB_PREFIX.equals(protocolo)) {
				result = new FileInspectorDB(pathBase);
			} if (FILE_PREFIX.equals(protocolo)) {
				result = new AlbumInspectorFSImpl(path, config.getDbTextFileName(), config.getDbFileEncoding(), config.getExtensiones());
			} else {
				//Si no tiene un protocolo identificado 
				result = new AlbumInspectorFSImpl(path, config.getDbTextFileName(), config.getDbFileEncoding(), config.getExtensiones());
			}
		}
		return result;
	}
}
