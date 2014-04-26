package ar.daf.foto.inspector.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.daf.foto.utilidades.HashUtils;
import ar.daf.foto.utilidades.JsonConverter;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Esta clase se encarga de inspecionar un path en particular, y sus subcarpetas.
 * Armando los albumes correspondientes.
 * Este inspector se encarga de verificar si un album precisa ser recargado o no.
 * 
 * @author daniel
 *
 */
public class FileInspector {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private int albumVersionMayor = 0;
	private int albumVersionMenor = 1;
	private int albumVersionRevision = 0;
	
	private String dbTextFileName = ".infoAlbum.json.txt";
	private String dbFileEncoding = "utf8";
	private String extensiones[] = {"JPEG, JPG, PNG, GIF, BMP"};
	
	private FileFilter fileImageFilter;
	private FileFilter directoryFilter;
	private FileFilter fileDataBaseTextFilter;
	private FileImageComparator fileImageComparator;
	
	private String pathBase;
	private List<AlbumFile> albumes = new ArrayList<AlbumFile>();
	
	public FileInspector(String pathBase) {
		this.pathBase = pathBase;
		this.fileDataBaseTextFilter = new FileDataBaseTextFilter(this.dbTextFileName);
		this.fileImageFilter = new OnlyFileImagesFilter(this.extensiones);
		this.directoryFilter = new OnlyDirectoryFilter();
		this.fileImageComparator = new FileImageComparator();
	}
	
	protected void logDebug(String msg) {
		log.debug("["+pathBase+"]"+msg);
	}
	protected void logInfo(String msg) {
		log.info("["+pathBase+"]"+msg);
	}
	protected void logError(String msg) {
		log.error("["+pathBase+"]"+msg);
	}
	protected void logWarn(String msg) {
		log.warn("["+pathBase+"]"+msg);
	}
	
	public synchronized List<AlbumFile> inspeccionar() {
		logDebug("Inspeccionando los albumes.");
		albumes.clear();
		File fileBase = new File(pathBase);
		if (fileBase != null && fileBase.exists() && fileBase.canRead() && fileBase.isDirectory() && fileBase.canExecute()) {
			try {
				albumes.addAll(armarAlbum(fileBase, true));
			} catch (JsonProcessingException e) {
				logError(e.getMessage());
			}
		}
		return albumes;
	}
	
	protected AlbumFile cargarAlbum(File dbTxt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
		AlbumFile result = null;
		try {
			logDebug("Cargando archivo de album");
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
			result = JsonConverter.buildObject(AlbumFile.class, buffer.toString());
		} catch (FileNotFoundException e) {
			logError(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logError(e.getMessage());
		}
		return result;
	}
	
	protected String armarHashAlbum(AlbumFile album) throws NoSuchAlgorithmException, JsonProcessingException {
		String result = null;
		DateTime fechaActPrev = album.getInfo().getFechaActualizacion();
		String hashPrev = album.getInfo().getContentHash();
		album.getInfo().setFechaActualizacion(null);
		album.getInfo().setContentHash(null);
		String contenidoSinHash = JsonConverter.buildJson(album);
		result = HashUtils.getHash(contenidoSinHash);
		album.getInfo().setFechaActualizacion(fechaActPrev);
		album.getInfo().setContentHash(hashPrev);
		return result;
	}
	
	protected boolean verificarVigenciaAlbum(AlbumFile album, File directorio, File archivosImagenes[]) throws NoSuchAlgorithmException, JsonProcessingException {
		boolean result = false;
		logDebug("Verificando la vigencia de los datos en memoria del album '"+directorio.toString()+"'");
		if (album != null && album.getPath() != null && album.getFileName() != null) {
			if (album.getInfo() == null) {
				result = true;
				logWarn("Los datos del album cargado no tiene header info. Se requiere rearmar e album.");
			} else {
				if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
					//1.- Revisamos si la fecha del album persistida es mas vieja que la de la carpeta
					if (album.getInfo().getFechaActualizacion() != null && album.getInfo().getFechaActualizacion().isBefore(directorio.lastModified())) {
						result = true;
						logDebug("La fecha de actualizacion de la carpeta es posterior a la del actualizacion del contenido del archivo. Se requiere rearmar el album.");
					} else {
						//2.- Revisamos si la cantidad de imagenes del album cambio
						if (archivosImagenes.length != album.getImagenes().size()) {
							result = true;
							logDebug("La cantidad de imagenes del archivo no coindice con la cantidad de imagenes del directorio. Se requiere rearmar el album.");
						} else {
							//3.- Revisamos si el contenido del archivo de texto del album cambio
							String hashReal = armarHashAlbum(album);
							if (!hashReal.equals(album.getInfo().getContentHash())) {
								result = true;
								logDebug("El hash del contenido del archivo no coincide con su contenido. Se requiere rearmar el album.");
							} else {
								//4.- Revisamos los archivos imagenes son distintos
								ArrayList<String> imagenes = new ArrayList<String>();
								for (File archivo : archivosImagenes) {
									String nombre = archivo.getName();
									int posImg = Collections.binarySearch(imagenes, nombre, fileImageComparator);
									if (posImg < 0) {
										imagenes.add(-posImg-1, nombre);
									}
								}
								for (ImagenFile img : album.getImagenes()) {
									String nombre = img.getFileName();
									int posImg = Collections.binarySearch(imagenes, nombre, fileImageComparator);
									if (posImg < 0) {
										result = true;
										logDebug("Las imagenes, por su nombre de archivo, no coinciden con las listadas en el archivo. Se requiere rearmar el album.");
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public AlbumFile actualizarAlbum(AlbumFile album) {
		if (album != null) {
			File directorio = new File(album.getPath());
			if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
				File archivosImagenes[] = directorio.listFiles(fileImageFilter);
				try {
					album = actualizarAlbum(album, directorio, archivosImagenes);
				} catch (NoSuchAlgorithmException e) {
					logError(e.getMessage());
				} catch (JsonProcessingException e) {
					logError(e.getMessage());
				}
			}
		}
		return album;
	}
	
	protected AlbumFile actualizarAlbum(AlbumFile album, File directorio, File archivosImagenes[]) throws NoSuchAlgorithmException, JsonProcessingException {
		/*
		 * El album a actualizar fue cargado desde el archivo de disco.
		 * Por lo tanto todas sus descripciones son correctas en memoria.
		 * Si se cambiaron a mano, en forma externa hay que actualizar la fecha y el hash correspondiente.
		 * Otra cosa que pudo haber cambiado sino son las imagenes, los nombres y la cantidad.
		 * Eso hay que revisarlo.
		 */
		List<ImagenFile> imagenes = new ArrayList<ImagenFile>();
		for (File archivo : archivosImagenes) {
			ImagenFile imagenPrev = null;
			for (ImagenFile img : album.getImagenes()) {
				if (archivo.getName().equals(img.getFileName())) {
					imagenPrev = img;
					break;
				}
			}
			if (imagenPrev == null) {
				//El archivo es nuevo
				imagenPrev = new ImagenFile();
				imagenPrev.setTitulo(archivo.getName());
				imagenPrev.setDescripcion(null);
				imagenPrev.setTags(null);
				imagenPrev.setFileName(archivo.getName());
				imagenPrev.setFileNameSmall(null);
			} else {
				//el archivo ya existe y esta catalogado
				album.getImagenes().remove(imagenPrev);
			}
			imagenes.add(imagenPrev);
		}
		//Las imagenes que quedaron dentro del album ya no existen hay que descartarlas
		album.getImagenes().clear();
		album.getImagenes().addAll(imagenes);
		
		//Ya teniendo acualizadas las imagenes, actualizamos la fecha de actualizacion y el hash
		album.getInfo().setFechaActualizacion(new DateTime(directorio.lastModified()));
		album.getInfo().setContentHash(armarHashAlbum(album));
		
		return album;
	}
	
	protected AlbumFile construirAlbum(File directorio, File archivosImagenes[]) throws NoSuchAlgorithmException, JsonProcessingException {
		AlbumFile result = new AlbumFile();
		AlbumInfoFile info = new AlbumInfoFile();
		info.setVersionMayor(albumVersionMayor);
		info.setVersionMenor(albumVersionMenor);
		info.setVersionRevision(albumVersionRevision);
		info.setFechaActualizacion(new DateTime(directorio.lastModified()));
		result.setInfo(info);
		
		result.setTitulo(directorio.getName());
		result.setDescripcion(null);
		result.setImagenPortada(null);
		result.setUbicacion(null);
		result.setTags(null);
		result.setFecha(null);
		result.setPath(directorio.getParent());
		result.setFileName(directorio.getName());
		result.setImagenes(new ArrayList<ImagenFile>());
		
		DateTime firstDate = null;
		for (File archivoImagen : archivosImagenes) {
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
		}
		result.setFecha(firstDate);
		
		info.setContentHash(armarHashAlbum(result));
		return result;
	}
	
	protected void salvarAlbum(AlbumFile album, File directorio) throws IOException {
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
	
	public AlbumFile armarAlbum(String path) {
		AlbumFile result= null;
		File directorio = new File(path);
		if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
			List<AlbumFile> lst;
			try {
				lst = armarAlbum(directorio, false);
				if (lst != null && !lst.isEmpty())
					result = lst.get(0);
			} catch (JsonProcessingException e) {
				logError(e.getMessage());
			}
		}
		return result;
	}
	
	public List<AlbumFile> armarAlbumes(String path) {
		List<AlbumFile> result = new ArrayList<AlbumFile>();
		File directorio = new File(path);
		if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
			try {
				result.addAll(armarAlbum(directorio, true));
			} catch (JsonProcessingException e) {
				logError(e.getMessage());
			}		
		}
		return result;
	}
	
	protected List<AlbumFile> armarAlbum(File directorio, boolean recursivo) throws JsonProcessingException {
		List<AlbumFile> result = new ArrayList<AlbumFile>();
		logDebug("Armando album. Aplicar recursividad="+recursivo);
		try {
			File archivosDBTxt[] = directorio.listFiles(fileDataBaseTextFilter);
			
			File archivosImagenes[] = directorio.listFiles(fileImageFilter);
			if (archivosImagenes != null && archivosImagenes.length > 0) {
				AlbumFile album = null;
				if (archivosDBTxt.length == 1) {
					logDebug("Archivo de datos del album encontrado.");
					//1.- cargar album desde db
					try {
						album = cargarAlbum(archivosDBTxt[0]);
					} catch (IllegalAccessException e) {
						logError(e.getMessage());
					} catch (IllegalArgumentException e) {
						logError(e.getMessage());
					} catch (InvocationTargetException e) {
						logError(e.getMessage());
					} catch (InstantiationException e) {
						logError(e.getMessage());
					} catch (IOException e) {
						logError(e.getMessage());
					} finally {
						if (album == null) {
							logWarn("Archivo de album encontrado pero no se pudo cargar. Eliminandolo.");
							archivosDBTxt[0].delete();
						}
					}
					if (album != null) {
						logDebug("Archivo de album cargado con exito.");
						//2.- verificar si el hay que actualizar
						boolean esVigente = verificarVigenciaAlbum(album, directorio, archivosImagenes);
						//2.1.- si actualizar -> cargar desde disco
						if (!esVigente) {
							logDebug("Datos de album modificados. Actualizando los datos desde el archivo y sus imagenes.");
							actualizarAlbum(album, directorio, archivosImagenes);
						} else {
							logDebug("Los datos del album en memoria siguen siendo vigentes. No se actualizan datos desde el archivo.");
						}
					}
				} else {
					//construir desde disco
					logDebug("Archivo del album no encontrado. Construyendolo.");
					album = construirAlbum(directorio, archivosImagenes);
				}
				
				//persistir album
				if (album != null) {
					try {
						logDebug("Persistiendo el archivo con los datos del album actualizados.");
						salvarAlbum(album, directorio);
					} catch (IOException e) {
						logError(e.getMessage());
					} 
					result.add(album);
				}
			} else {
				logWarn("Path sin imagenes. Omitiendo '"+directorio.toString()+"'");
				if (archivosDBTxt.length == 1) {
					logWarn("Eliminando el archivo de datos del album");
					archivosDBTxt[0].delete();
				}
			}
			
			if (recursivo) {
				File subDirectorios[] = directorio.listFiles(directoryFilter);
				if (subDirectorios != null && subDirectorios.length > 0) {
					for (File subDirectorio : subDirectorios) {
						result.addAll(armarAlbum(subDirectorio, recursivo));
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getPathBase() {
		return pathBase;
	}

	public void setPathBase(String pathBase) {
		this.pathBase = pathBase;
	}

	public int getAlbumVersionMayor() {
		return albumVersionMayor;
	}

	public int getAlbumVersionMenor() {
		return albumVersionMenor;
	}

	public int getAlbumVersionRevision() {
		return albumVersionRevision;
	}

	public List<AlbumFile> getAlbumes() {
		return albumes;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + albumVersionMayor;
		result = prime * result + albumVersionMenor;
		result = prime * result + albumVersionRevision;
		result = prime * result
				+ ((pathBase == null) ? 0 : pathBase.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInspector other = (FileInspector) obj;
		if (albumVersionMayor != other.albumVersionMayor)
			return false;
		if (albumVersionMenor != other.albumVersionMenor)
			return false;
		if (albumVersionRevision != other.albumVersionRevision)
			return false;
		if (pathBase == null) {
			if (other.pathBase != null)
				return false;
		} else if (!pathBase.equals(other.pathBase))
			return false;
		return true;
	}
	
}