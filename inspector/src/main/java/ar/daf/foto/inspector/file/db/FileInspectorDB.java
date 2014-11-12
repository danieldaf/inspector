package ar.daf.foto.inspector.file.db;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.core.JsonProcessingException;

import ar.daf.foto.inspector.file.AlbumFile;
import ar.daf.foto.inspector.file.AlbumInspector;
import ar.daf.foto.inspector.file.AlbumInspectorImpl;

/**
 * TODO
 * Esta clase inspecciona una base de datos relacion en busca de imagenes.
 * Con dos tablas planas, Albumes e Imagenes deberia bastar pero hay que pensar en la estructura un poco mas.
 * @author daniel
 *
 */
public class FileInspectorDB extends AlbumInspectorImpl implements
		AlbumInspector {

	public FileInspectorDB(String pathBase) {
		super(pathBase);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String armarPathAlbum(File directorio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AlbumFile actualizarAlbum(AlbumFile album) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AlbumFile construirAlbum(File directorio, File[] archivosImagenes)
			throws NoSuchAlgorithmException, JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
