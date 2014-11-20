package ar.daf.foto.inspector.album.fs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.album.AlbumFile;
import ar.daf.foto.inspector.album.dtoIO.AlbumIO;
import ar.daf.foto.inspector.album.dtoIO.DirectorioIO;
import ar.daf.foto.utilidades.JsonConverter;

public class AlbumFS implements AlbumIO {

	private String pathBase;
	private DirectorioIO directorio;
	private FileFilter fileImageFilter;
	private FileFilter fileDataBaseTextFilter;
	private String dbFileEncoding;
	private File dir;
	
	public AlbumFS(DirectorioIO directorio, String pathBase, String path, FileFilter fileImageFilter, FileFilter fileDataBaseTextFilter, String dbFileEncoding) {
		this.directorio = directorio;
		this.pathBase = pathBase;
		this.dir = new File(path);
		this.fileImageFilter = fileImageFilter;
		this.fileDataBaseTextFilter = fileDataBaseTextFilter;
		this.dbFileEncoding = dbFileEncoding;
		if (dir.exists() && !dir.isDirectory())
			throw new RuntimeException("'"+path+"' debe ser un directorio.");
	}
	
	public File getFile() {
		return dir;
	}

	@Override
	public Iterator<String> iterator() {
		ArrayList<String> result = new ArrayList<String>();
		if (dir != null && dir.exists() && dir.isDirectory() && dir.canRead() && dir.canExecute()) {
			File files[] = dir.listFiles(fileImageFilter);
			for (File file : files) { 
				result.add(file.getName());
			}
		}
		return result.iterator();
	}

	@Override
	public DirectorioIO getDirectorio() {
		return directorio;
	}

	@Override
	public String getNombre() {
		return dir.getName();
	}

	@Override
	public String getNombreCompleto() {
		String result = null;
		try {
			result = dir.getCanonicalPath();
		} catch (IOException e) {
			result = dir.getAbsolutePath();
		}
		return result;
	}

	@Override
	public DateTime getFechaModificacion() {
		DateTime result = null;
		if (dir != null && dir.exists())
			result = new DateTime(dir.lastModified());
		return result;
	}

	@Override
	public boolean hasAlbumInfo() {
		boolean result = false;
		if (dir != null && dir.isDirectory() && dir.canRead() && dir.canExecute()) {
			File files[] = dir.listFiles(fileDataBaseTextFilter);
			if (files.length > 0)
				result = true;
		}
		return result;
	}
	
	protected String armarPathAlbum() {
		return armarPathAlbum(dir);
	}
	
	protected String armarPathAlbum(File directorio) {
		String path = "";
		if (this.pathBase.equals(directorio.getAbsolutePath())) {
			path = ".."+File.separator+directorio.getName()+File.separator+"..";
		} else {
			path = directorio.getParentFile().getAbsolutePath().substring(this.pathBase.length());
			if (path.endsWith(File.separator))
				path = path.substring(0, path.length()-1);
			if (path.startsWith(File.separator))
				path = path.substring(1);
		}
		return path;
	}	

	protected AlbumFile cargarAlbum(File dbTxt) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException, IOException {
		AlbumFile result = null;
		try {
//			logDebug(dbTxt.getParentFile(), "Cargando archivo de album");
			FileInputStream fis = new FileInputStream(dbTxt);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis, dbFileEncoding));
			StringBuffer buffer = new StringBuffer();
			String strLinea = br.readLine();
			while (strLinea != null) {
				buffer.append(strLinea);
				strLinea = br.readLine();
			}
			br.close();
			result = JsonConverter.buildObject(AlbumFile.class,
					buffer.toString());
			result.setPathBase(this.pathBase);
			result.setPath(armarPathAlbum(dbTxt.getParentFile()));
			result.setFileName(dbTxt.getParentFile().getName());
		} catch (FileNotFoundException e) {
//			logError(dbTxt.getParentFile(), e.getMessage());
		} catch (UnsupportedEncodingException e) {
//			logError(dbTxt.getParentFile(), e.getMessage());
		}
		return result;
	}
	
	@Override
	public AlbumFile loadAlbumInfo() throws IOException {
		AlbumFile result = null;
		if (dir != null && dir.isDirectory() && dir.canRead() && dir.canExecute()) {
			File files[] = dir.listFiles(fileDataBaseTextFilter);
			if (files.length > 0) {
				try {
	//				logDebug(dbTxt.getParentFile(), "Cargando archivo de album");
					FileInputStream fis = new FileInputStream(files[0]);
					DataInputStream dis = new DataInputStream(fis);
					BufferedReader br = new BufferedReader(new InputStreamReader(dis, dbFileEncoding));
					StringBuffer buffer = new StringBuffer();
					String strLinea = br.readLine();
					while (strLinea != null) {
						buffer.append(strLinea);
						strLinea = br.readLine();
					}
					br.close();
					result = JsonConverter.buildObject(AlbumFile.class, buffer.toString());
					result.setPathBase(this.pathBase);
					result.setPath(armarPathAlbum(files[0].getParentFile()));
					result.setFileName(files[0].getParentFile().getName());
				} catch (FileNotFoundException e) {
//					logError(files[0].getParentFile(), e.getMessage());
				} catch (UnsupportedEncodingException e) {
//					logError(files[0].getParentFile(), e.getMessage());
				}
			}
		}
		return result;
	}

	@Override
	public boolean deleteAlbumInfo() {
		boolean result = false;
		if (dir != null && dir.isDirectory() && dir.canRead() && dir.canExecute()) {
			File files[] = dir.listFiles(fileDataBaseTextFilter);
			if (files.length > 0) {
				for (File f : files) {
					f.delete();
				}
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean hasImagenes() {
		boolean result = false;
		//cant read dire
		if (dir != null && dir.isDirectory() && dir.canRead() && dir.canExecute()) {
			File files[] = dir.listFiles(fileImageFilter);
			if (files.length > 0)
				result = true;
		}
		return result;
	}

	@Override
	public int getCountImagenes() {
		int result = 0;
		if (dir != null && dir.isDirectory() && dir.canRead() && dir.canExecute()) {
			File files[] = dir.listFiles(fileImageFilter);
			result = files.length;
		}
		return result;
	}

	@Override
	public Object loadImagen(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	public File loadImagenFile(String nombre) {
		File result = new File(dir.getAbsolutePath()+File.separator+nombre);
		return result;
	}

}
