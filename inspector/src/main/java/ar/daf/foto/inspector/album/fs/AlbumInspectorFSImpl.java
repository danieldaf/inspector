package ar.daf.foto.inspector.album.fs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.daf.foto.inspector.album.AlbumFile;
import ar.daf.foto.inspector.album.AlbumIO;
import ar.daf.foto.inspector.album.AlbumInfoFile;
import ar.daf.foto.inspector.album.AlbumInspector;
import ar.daf.foto.inspector.album.AlbumInspectorImpl;
import ar.daf.foto.inspector.album.DirectorioIO;
import ar.daf.foto.inspector.album.ImagenFile;
import ar.daf.foto.utilidades.HashUtils;
import ar.daf.foto.utilidades.JsonConverter;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Esta clase se encarga de inspecionar un path en particular, y sus subcarpetas.
 * Armando los albumes correspondientes.
 * Este inspector se encarga de verificar si un album precisa ser recargado o no.
 * 
 * TODO IMPORTANTE: Se modifico como subclase de AlbumInspectoImpl
 * No obstante esta clase sigue definiendo todo el comportamiento por si sola 
 * para que funcione ok.
 * Hay que repartir las responsabilidades entre ella y su padre. 
 * 
 * @author daniel
 *
 */
public class AlbumInspectorFSImpl extends AlbumInspectorImpl implements AlbumInspector {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private String dbTextFileName = ".infoAlbum.json.txt";
	private String dbFileEncoding = "utf8";
	private String extensiones[] = {"JPEG", "JPG", "PNG", "GIF", "BMP"};
	
	private FileFilter fileImageFilter;
	private FileFilter directoryFilter;
	private FileFilter fileDataBaseTextFilter;
	
	public AlbumInspectorFSImpl(String pathBase, String dbTextFileName, String dbFileEncoding, List<String> extensiones) {
		super(pathBase);
		this.dbTextFileName = dbTextFileName;
		this.dbFileEncoding = dbFileEncoding;
		if (extensiones != null)
			this.extensiones = extensiones.toArray(this.extensiones);
		this.fileDataBaseTextFilter = new FileDataBaseTextFilter(this.dbTextFileName);
		this.fileImageFilter = new OnlyFileImagesFilter(this.extensiones);
		this.directoryFilter = new OnlyDirectoryFilter();
		new DtoFactory(this);
	}

	@Override
	protected DirectorioIO buildDirectorioBase() {
		return DtoFactory.getInstance().createDirectorio();
	}

	//??Queda aca
	//Creo que deberia estar definido el metodo en forma abstracta e implementado en el padre
//	protected String armarPathAlbum(File directorio) {
//		String path = "";
//		if (this.pathBase.equals(directorio.getAbsolutePath())) {
//			path = ".."+File.separator+directorio.getName()+File.separator+"..";
//		} else {
//			path = directorio.getParentFile().getAbsolutePath().substring(this.pathBase.length());
//			if (path.endsWith(File.separator))
//				path = path.substring(0, path.length()-1);
//			if (path.startsWith(File.separator))
//				path = path.substring(1);
//		}
//		return path;
//	}
	
//	protected AlbumFile cargarAlbum(File dbTxt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
//		AlbumFile result = null;
//		try {
//			logDebug(dbTxt.getParentFile(), "Cargando archivo de album");
//			FileInputStream fis = new FileInputStream(dbTxt);
//			DataInputStream dis = new DataInputStream(fis);
//			BufferedReader br = new BufferedReader(new InputStreamReader(dis, dbFileEncoding));
//			StringBuffer buffer = new StringBuffer();
//			String strLinea = br.readLine();
//			while (strLinea != null) {
//				buffer.append(strLinea);
//				strLinea = br.readLine();
//			}
//			br.close();
//			result = JsonConverter.buildObject(AlbumFile.class, buffer.toString());
//			result.setPathBase(this.pathBase);
//			result.setPath(armarPathAlbum(dbTxt.getParentFile()));
//			result.setFileName(dbTxt.getParentFile().getName());
//		} catch (FileNotFoundException e) {
//			logError(dbTxt.getParentFile(), e.getMessage());
//		} catch (UnsupportedEncodingException e) {
//			logError(dbTxt.getParentFile(), e.getMessage());
//		}
//		return result;
//	}
	
//	public AlbumFile actualizarAlbum(AlbumFile album) {
//		if (album != null) {
//			String fullPath = album.getPathBase()+File.separator+album.getPath();
//			if (album.getPath().isEmpty())
//				fullPath = album.getPathBase(); //TODO tal vez, inspeccionar el album raiz desde este metodo deberia estar prohibido...
//			File directorio = new File(fullPath);
//			if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
//				File archivosImagenes[] = directorio.listFiles(fileImageFilter);
//				try {
//					album = actualizarAlbum(album, directorio, archivosImagenes, false, null);
//				} catch (NoSuchAlgorithmException e) {
//					logError(directorio, e.getMessage());
//				} catch (JsonProcessingException e) {
//					logError(directorio, e.getMessage());
//				}
//			}
//		}
//		return album;
//	}

	@Override
	protected AlbumFile construirAlbum(AlbumIO albumIO) throws NoSuchAlgorithmException, JsonProcessingException {
		AlbumFile result = new AlbumFile();

		AlbumInfoFile info = new AlbumInfoFile();
		info.setVersionMayor(albumVersionMayor);
		info.setVersionMenor(albumVersionMenor);
		info.setVersionRevision(albumVersionRevision);
		info.setFechaActualizacion(albumIO.getFechaModificacion());
		result.setInfo(info);
		
		result.setTitulo(albumIO.getNombre());
		result.setDescripcion(null);
		result.setImagenPortada(null);
		result.setUbicacion(null);
		result.setTags(null);
		result.setFecha(null);
		result.setPathBase(this.pathBase);
//		result.setPath(armarPathAlbum(((AlbumFS)albumIO).getFile()));
		result.setPath(((AlbumFS)albumIO).armarPathAlbum());
		result.setFileName(albumIO.getNombre());
		result.setImagenes(new ArrayList<ImagenFile>());
		
		String hashId = result.getPath()+File.separator+result.getFileName();
		hashId = HashUtils.armarHashSHA1(hashId);
		info.setHashId(hashId);

		DateTime firstDate = null;
		for (String nombreImagen : albumIO) {
			File archivoImagen = ((AlbumFS)albumIO).loadImagenFile(nombreImagen);
			
			ImagenFile imagen = new ImagenFile();
			imagen.setTitulo(archivoImagen.getName());
			imagen.setDescripcion(null);
			imagen.setTags(null);
			imagen.setFileName(archivoImagen.getName());
			imagen.setFileNameSmall(null);
			if (firstDate == null || firstDate.isAfter(archivoImagen.lastModified())) {
				firstDate = new DateTime(archivoImagen.lastModified());
			}
			
			result.getImagenes().add(imagen);
			if (result.getImagenPortada() == null) 
				result.setImagenPortada(imagen.getFileName());
		}
		result.setFecha(firstDate);
		
		info.setContentHash(armarHashAlbum(result));
		return result;
	}
	
	@Override
	protected void salvarAlbum(AlbumFile album, AlbumIO albumIO) throws IOException {
		File directorio = ((AlbumFS)albumIO).getFile();
		File archivoAlbum = new File(directorio.getPath()+File.separator+dbTextFileName);
		if (!archivoAlbum.exists()) {
			archivoAlbum.createNewFile();
		}
		album.getInfo().setFechaActualizacion(new DateTime(directorio.lastModified()));
		String json = JsonConverter.buildJson(album);
		FileOutputStream fos = new FileOutputStream(archivoAlbum);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, dbFileEncoding));
		bw.write(json);
		bw.close();
	}

	public String getDbTextFileName() {
		return dbTextFileName;
	}

	public String getDbFileEncoding() {
		return dbFileEncoding;
	}

	public String[] getExtensiones() {
		return extensiones;
	}
	
	static class DtoFactory {
		
		private static AlbumInspectorFSImpl padre = null;				
		private static DtoFactory instance = null;
		private DtoFactory(AlbumInspectorFSImpl padre) {
			DtoFactory.padre = padre;
			DtoFactory.instance = this;
		}
		public static DtoFactory getInstance() {
			return instance;
		}
		
		DirectorioIO createDirectorio() {
			return new DirectorioFS(padre.pathBase, padre.directoryFilter);
		}
		AlbumIO createAlbum(DirectorioIO directorio, String path) {
			return new AlbumFS(directorio, padre.pathBase, path, padre.fileImageFilter, padre.fileDataBaseTextFilter, padre.dbFileEncoding);
		}
	}
	
}