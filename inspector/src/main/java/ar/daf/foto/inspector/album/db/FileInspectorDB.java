package ar.daf.foto.inspector.album.db;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import ar.daf.foto.inspector.album.AlbumFile;
import ar.daf.foto.inspector.album.AlbumInspector;
import ar.daf.foto.inspector.album.AlbumInspectorImpl;
import ar.daf.foto.inspector.album.dtoIO.AlbumIO;
import ar.daf.foto.inspector.album.dtoIO.DirectorioIO;

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
	protected DirectorioIO buildDirectorioBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AlbumFile construirAlbum(AlbumIO albumIO)
			throws NoSuchAlgorithmException, JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void salvarAlbum(AlbumFile album, AlbumIO albumIO)
			throws IOException {
		// TODO Auto-generated method stub
		
	}
}
