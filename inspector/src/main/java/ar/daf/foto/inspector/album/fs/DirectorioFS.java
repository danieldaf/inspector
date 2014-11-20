package ar.daf.foto.inspector.album.fs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.album.dtoIO.AlbumIO;
import ar.daf.foto.inspector.album.dtoIO.DirectorioIO;
import ar.daf.foto.inspector.album.fs.AlbumInspectorFSImpl.DtoFactory;

public class DirectorioFS implements DirectorioIO {
	
	private FileFilter directoryFilter;
	private File dir;
	
	public DirectorioFS(String pathBase,FileFilter directoryFilter) {
		this.dir = new File(pathBase);
		this.directoryFilter = directoryFilter;
		if (dir.exists() && !dir.isDirectory())
			throw new RuntimeException("'"+pathBase+"' debe ser un directorio.");
	}
	
	public File getFile() {
		return dir;
	}
	
	private void addAlbumes(File dir, ArrayList<AlbumIO> lista) {
		File files[] = dir.listFiles(directoryFilter);
		for (File file : files) {
			AlbumIO album = DtoFactory.getInstance().createAlbum(this, file.getAbsolutePath());
			lista.add(album);
		}
	}

	@Override
	public Iterator<AlbumIO> iterator() {
		ArrayList<AlbumIO> result = new ArrayList<AlbumIO>();
		if (dir != null && dir.exists() && dir.isDirectory() && dir.canRead() && dir.canExecute()) {
			File files[] = dir.listFiles(directoryFilter);
			for (File file : files) { 
				AlbumIO album = DtoFactory.getInstance().createAlbum(this, file.getAbsolutePath());
				result.add(album);
				addAlbumes(file, result);
			}
		}
		return result.iterator();
	}

	@Override
	public String getNombre() {
		return dir.getName();
	}

	@Override
	public DateTime getFechaModificacion() {
		return dir!=null&&dir.exists()?new DateTime(dir.lastModified()):null;
	}

}
