package ar.daf.foto.inspector.album;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.daf.foto.inspector.album.dtoIO.AlbumIO;
import ar.daf.foto.inspector.album.dtoIO.DirectorioIO;
import ar.daf.foto.inspector.album.fs.FileImageComparator;
import ar.daf.foto.utilidades.HashUtils;
import ar.daf.foto.utilidades.JsonConverter;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * TODO
 * Esta clase deberia contener el comportamiento abstracto para tratar con los albumes desde archivos 
 * (archivos o lo que fuese, conceptualmente son archivos aqui).
 * 
 * @author daniel
 *
 */
public abstract class AlbumInspectorImpl implements AlbumInspector {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	protected int albumVersionMayor = 0;
	protected int albumVersionMenor = 1;
	protected int albumVersionRevision = 0;
	
	private FileImageComparator fileImageComparator;

	protected String pathBase;
	protected List<AlbumFile> albumes = new ArrayList<AlbumFile>();
	
	protected Map<String, AlbumId> albumesId = new HashMap<String, AlbumId>();
	protected Map<String, AlbumId> albumesIdTmp = new HashMap<String, AlbumId>();
	protected class AlbumId {
		Long id;
		Map<String, Long> imgsId = new HashMap<String, Long>();
	}
	
	public AlbumInspectorImpl(String pathBase) {
		this.pathBase = pathBase;
		this.fileImageComparator = new FileImageComparator();
	}
	
	protected void logDebug(AlbumIO dir, String msg) {
		log.debug("["+(dir!=null?dir.getNombreCompleto():pathBase)+"] "+msg);
	}
	protected void logInfo(AlbumIO dir, String msg) {
		log.info("["+(dir!=null?dir.getNombreCompleto():pathBase)+"] "+msg);
	}
	protected void logError(AlbumIO dir, String msg) {
		log.error("["+(dir!=null?dir.getNombreCompleto():pathBase)+"] "+msg);
	}
	protected void logWarn(AlbumIO dir, String msg) {
		log.warn("["+(dir!=null?dir.getNombreCompleto():pathBase)+"] "+msg);
	}

	protected void logDebug(DirectorioIO dir, String msg) {
		log.debug("["+(dir!=null?dir.getNombre():pathBase)+"] "+msg);
	}
	protected void logInfo(DirectorioIO dir, String msg) {
		log.info("["+(dir!=null?dir.getNombre():pathBase)+"] "+msg);
	}
	protected void logError(DirectorioIO dir, String msg) {
		log.error("["+(dir!=null?dir.getNombre():pathBase)+"] "+msg);
	}
	protected void logWarn(DirectorioIO dir, String msg) {
		log.warn("["+(dir!=null?dir.getNombre():pathBase)+"] "+msg);
	}

	protected void logDebug(String msg) {
		log.debug("["+pathBase+"] "+msg);
	}
	protected void logInfo(String msg) {
		log.info("["+pathBase+"] "+msg);
	}
	protected void logError(String msg) {
		log.error("["+pathBase+"] "+msg);
	}
	protected void logWarn(String msg) {
		log.warn("["+pathBase+"] "+msg);
	}
	
	protected abstract DirectorioIO buildDirectorioBase();

	/**
	 * El comportamiento es generico y deberia estar codificado aqui.
	 * Hay que abstraerlo del File
	 */
	public synchronized List<AlbumFile> inspeccionar() {
		logDebug("Inspeccionando los albumes.");
		albumes.clear();
		DirectorioIO directorioIO = buildDirectorioBase();
		try {
			albumesIdTmp.clear();
			albumesIdTmp.putAll(albumesId);
			albumesId.clear();
			albumes.addAll(armarAlbum(directorioIO, true));
			albumesIdTmp.clear();
		} catch (JsonProcessingException e) {
			logError(directorioIO, e.getMessage());
		}
		return albumes;
	}

//	/**
//	 * Este metodo si debe ser redefinido por cada implementacion particular por completo
//	 * Debe retornar un path UNICO para el directorio pasado
//	 * @param directorio
//	 * @return
//	 */
//	protected abstract String armarPathAlbum(File directorio);

//	/**
//	 * La logica de este metodo debe estar aqui.
//	 * Abstraer en un metodo independiente propio de cada implementacon, la logica de donde cargar
//	 * el contenido (en este caso mediante FileInputStream)
//	 * 
//	 * @param dbTxt
//	 * @return
//	 * @throws IllegalAccessException
//	 * @throws IllegalArgumentException
//	 * @throws InvocationTargetException
//	 * @throws InstantiationException
//	 * @throws IOException
//	 */
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
	
	/**
	 * Este metodo queda tal cual como esta aqui.
	 * Sacarlo de las subclases
	 * 
	 * @param album
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws JsonProcessingException
	 */
	protected String armarHashAlbum(AlbumFile album) throws NoSuchAlgorithmException, JsonProcessingException {
		String result = null;
		DateTime fechaActPrev = album.getInfo().getFechaActualizacion();
		String hashPrev = album.getInfo().getContentHash();
		album.getInfo().setFechaActualizacion(null);
		album.getInfo().setContentHash(null);
		String contenidoSinHash = JsonConverter.buildJson(album);
		result = HashUtils.armarHashSHA1(contenidoSinHash);
		album.getInfo().setFechaActualizacion(fechaActPrev);
		album.getInfo().setContentHash(hashPrev);
		return result;
	}
	
	/**
	 * Preservar la logica abstrayendose de tratar fon File
	 * 
	 * @param album
	 * @param directorio
	 * @param archivosImagenes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws JsonProcessingException
	 */
	protected boolean haceFaltaRearmarElAlbum(AlbumFile album, AlbumIO albumIO) throws NoSuchAlgorithmException, JsonProcessingException {
		boolean result = false;
		logDebug(albumIO, "Verificando la vigencia de los datos en memoria del album.");
		if (album != null && album.getPathBase() != null && album.getPath() != null && album.getFileName() != null) {
			if (album.getInfo() == null) {
				result = true;
				logWarn(albumIO, "Los datos del album cargado no tiene header info. Se requiere rearmar e album.");
			} else {
				//1.- Revisamos si la fecha del album persistida es mas vieja que la de la carpeta
				if (album.getInfo().getFechaActualizacion() != null && album.getInfo().getFechaActualizacion().isBefore(albumIO.getFechaModificacion())) {
					result = true;
					logDebug(albumIO, "La fecha de actualizacion de la carpeta es posterior a la del actualizacion del contenido del archivo. Se requiere rearmar el album.");
				} else {
					//2.- Revisamos si la cantidad de imagenes del album cambio
					if (albumIO.getCountImagenes() != album.getImagenes().size()) {
						result = true;
						logDebug(albumIO, "La cantidad de imagenes del archivo no coindice con la cantidad de imagenes del directorio. Se requiere rearmar el album.");
					} else {
						//3.- Revisamos si el contenido del archivo de texto del album cambio
						String hashReal = armarHashAlbum(album);
						if (!hashReal.equals(album.getInfo().getContentHash())) {
							result = true;
							logDebug(albumIO, "El hash del contenido del archivo no coincide con su contenido. Se requiere rearmar el album.");
						} else {
							//4.- Revisamos si el album fue movido de lugar
							String hashId = HashUtils.armarHashSHA1(album.getPath()+File.separator+album.getFileName());
							if (!hashId.equals(album.getInfo().getHashId())) {
								result = true;
								logDebug(albumIO, "Aparentemente el album fue movido de carpeta. El hashId almacenado no coincide. Se requiere rearmar el album.");
							} else {
								//5.- Revisamos los archivos imagenes son distintos
								ArrayList<String> imagenes = new ArrayList<String>();
								for (String nombreArchivoImagen : albumIO) {
									int posImg = Collections.binarySearch(imagenes, nombreArchivoImagen, fileImageComparator);
									if (posImg < 0) {
										imagenes.add(-posImg-1, nombreArchivoImagen);
									}
								}
								for (ImagenFile img : album.getImagenes()) {
									String nombre = img.getFileName();
									int posImg = Collections.binarySearch(imagenes, nombre, fileImageComparator);
									if (posImg < 0) {
										result = true;
										logDebug(albumIO, "Las imagenes, por su nombre de archivo, no coinciden con las listadas en el archivo. Se requiere rearmar el album.");
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

//	public abstract AlbumFile actualizarAlbum(AlbumFile album);
	
	/**
	 * Este metodo tiene logica que es generica a todas las subclases.
	 * Abstraerlo de usar File directamente.
	 * 
	 * @param album
	 * @param directorio
	 * @param archivosImagenes
	 * @param completarConId
	 * @param albumId
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws JsonProcessingException
	 */
	protected AlbumFile actualizarAlbum(AlbumFile album, AlbumIO albumIO, boolean completarConId, AlbumId albumId) throws NoSuchAlgorithmException, JsonProcessingException {
		/*
		 * El album a actualizar fue cargado desde el archivo de disco.
		 * Por lo tanto todas sus descripciones son correctas en memoria.
		 * 
		 * Si se cambiaron a mano, en forma externa hay que actualizar la fecha y el hash correspondiente.
		 * 
		 * El album pudo haberse modivo de carpeta, en cuyo caso hay que recalcular su hashId.
		 * 
		 * Otra cosa que pudo haber cambiado sino son las imagenes, los nombres y la cantidad.
		 * Eso hay que revisarlo.
		 */
		//String hashId = HashUtils.armarHashSHA1(album.getPath()+File.separator+album.getFileName());
		String hashId = HashUtils.armarHashSHA1(albumIO.getNombreCompleto());
		album.getInfo().setHashId(hashId);
		
		List<ImagenFile> imagenes = new ArrayList<ImagenFile>();
		for (String nombreImagenArchivo : albumIO) {
			ImagenFile imagenPrev = null;
			for (ImagenFile img : album.getImagenes()) {
				if (nombreImagenArchivo.equals(img.getFileName())) {
					imagenPrev = img;
					break;
				}
			}
			if (imagenPrev == null) {
				//El archivo es nuevo
				imagenPrev = new ImagenFile();
				imagenPrev.setTitulo(nombreImagenArchivo);
				imagenPrev.setDescripcion(null);
				imagenPrev.setTags(null);
				imagenPrev.setFileName(nombreImagenArchivo);
				imagenPrev.setFileNameSmall(null);
			} else {
				//el archivo ya existe y esta catalogado
				album.getImagenes().remove(imagenPrev);
			}
			
			if (completarConId) {
				imagenPrev.setId(albumId.imgsId.get(imagenPrev.getFileName()));
				albumId.imgsId.put(imagenPrev.getFileName(), imagenPrev.getId());
			}
			
			imagenes.add(imagenPrev);
		}
		
		//Las imagenes que quedaron dentro del album ya no existen hay que descartarlas
		album.getImagenes().clear();
		album.getImagenes().addAll(imagenes);
		
		//Ya teniendo acualizadas las imagenes, actualizamos la fecha de actualizacion y el hash
		album.getInfo().setFechaActualizacion(new DateTime(albumIO.getFechaModificacion()));
		album.getInfo().setContentHash(armarHashAlbum(album));
		
		return album;
	}
	
	/**
	 * Metodo que debe ser implementado en su totalidad en las subclases
	 * 
	 * @param directorio
	 * @param archivosImagenes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws JsonProcessingException
	 */
	protected abstract AlbumFile construirAlbum(AlbumIO albumIO) throws NoSuchAlgorithmException, JsonProcessingException;
	
	protected abstract void salvarAlbum(AlbumFile album, AlbumIO albumIO) throws IOException;
//	protected abstract void salvarAlbum(AlbumFile album, File directorio) throws IOException {
//		File archivoAlbum = new File(directorio.getPath()+File.separator+dbTextFileName);
//		if (!archivoAlbum.exists()) {
//			archivoAlbum.createNewFile();
//		}
//		album.getInfo().setFechaActualizacion(new DateTime(directorio.lastModified()));
//		String json = JsonConverter.buildJson(album);
//		FileOutputStream fos = new FileOutputStream(archivoAlbum);
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, dbFileEncoding));
//		bw.write(json);
//		bw.close();
//	}

	/**
	 * Simil al metodo 
	 * public synchronized List<AlbumFile> inspeccionar()
	 * 
	 * CREO QUE NO SE ESTA USADO AUN. NO LO REIMPLEMENTAMOS
	 */
//	public AlbumFile armarAlbum(String path) {
//		AlbumFile result= null;
//		File directorio = new File(path);
//		if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
//			List<AlbumFile> lst;
//			try {
//				lst = armarAlbum(directorio, false, false);
//				if (lst != null && !lst.isEmpty())
//					result = lst.get(0);
//			} catch (JsonProcessingException e) {
//				logError(directorio, e.getMessage());
//			}
//		}
//		return result;
//	}
	
	/**
	 * Simil al metodo 
	 * public synchronized List<AlbumFile> inspeccionar()
	 * 
	 * CREO QUE NO SE ESTA USADO AUN. NO LO REIMPLEMENTAMOS
	 */
//	public List<AlbumFile> armarAlbumes(String path) {
//		List<AlbumFile> result = new ArrayList<AlbumFile>();
//		File directorio = new File(path);
//		if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
//			try {
//				result.addAll(armarAlbum(directorio, true, false));
//			} catch (JsonProcessingException e) {
//				logError(directorio, e.getMessage());
//			}
//		}
//		return result;
//	}
	
	protected AlbumFile armarAlbum(AlbumIO albumIO, final boolean completarConId) throws JsonProcessingException {
		AlbumFile result = null;
		try {
			if (albumIO.hasImagenes()) {
				AlbumFile album = null;
				AlbumId albumId = null;
				String prevHashId = null;
	
				if (albumIO.hasAlbumInfo()) {
					logDebug(albumIO, "Archivo de datos del album encontrado.");
					//1.- cargar album desde db
					try {
						album = albumIO.loadAlbumInfo();
					} catch (Throwable e) {
						logError(albumIO, e.getMessage());
					} finally {
						if (album == null) {
							logWarn(albumIO, "Archivo de album encontrado pero no se pudo cargar. Eliminandolo.");
							albumIO.deleteAlbumInfo();
						}					
					}
	
					if (album != null) {
						logDebug(albumIO, "Archivo de album cargado con exito.");
						//2.- verificar si el hay que actualizar
						boolean sinVigencia = haceFaltaRearmarElAlbum(album, albumIO);
	
						if (completarConId) {
							if (album.getInfo() != null) {
								prevHashId = album.getInfo().getHashId();
								if (prevHashId != null) {
									prevHashId = prevHashId.trim();
									if (prevHashId.isEmpty())
										prevHashId = null;
								}
							}
							if (prevHashId != null)
								albumId = albumesIdTmp.get(prevHashId);
							
							if (albumId == null) {
								albumId = new AlbumId();
								albumId.id = album.getId();
								albumId.imgsId = new HashMap<String, Long>();
							} else {
								album.setId(albumId.id);
							}
						}
						
						//2.1.- si actualizar -> cargar desde disco
						if (sinVigencia) {
							logDebug(albumIO, "Datos de album modificados. Actualizando los datos desde el archivo y sus imagenes.");
							actualizarAlbum(album, albumIO, completarConId, albumId);
							album.setActualizado(true);
						} else {
							logDebug(albumIO, "Los datos del album en memoria siguen siendo vigentes. No se actualizan datos desde el archivo.");
							if (completarConId) {
								album.setId(albumId.id);
								boolean requiereActualzacionForzada = albumId.id == null;
								for (ImagenFile img : album.getImagenes()) {
									img.setId(albumId.imgsId.get(img.getFileName()));
									if (img.getId() == null) {
										requiereActualzacionForzada = true;
									} else {
										albumId.imgsId.put(img.getFileName(), img.getId());
									}
								}
								album.setActualizado(requiereActualzacionForzada);
							} else {
								album.setActualizado(false);
							}
						}
					}
				} else {
					//construir desde disco					
					logDebug(albumIO, "Archivo del album no encontrado. Construyendolo.");
					album = construirAlbum(albumIO);
					album.setActualizado(true);
					
					if (completarConId) {
						albumId = albumesIdTmp.get(album.getInfo().getHashId());
						if (albumId == null) {
							albumId = new AlbumId();
							albumId.id = album.getId();
							albumId.imgsId = new HashMap<String, Long>();
							for (ImagenFile imgF : album.getImagenes()) {
								albumId.imgsId.put(imgF.getFileName(), imgF.getId());
							}
						} else {
							for (ImagenFile imgF : album.getImagenes()) {
								imgF.setId(albumId.imgsId.get(imgF.getFileName()));
								albumId.imgsId.put(imgF.getFileName(), imgF.getId());
							}
						}
					}
				}
				
				//persistir album
				if (album != null) {
					try {
						logDebug(albumIO, "Persistiendo el archivo con los datos del album actualizados.");
						salvarAlbum(album, albumIO);
					} catch (IOException e) {
						logError(albumIO, e.getMessage());
					}
					
					if (completarConId) {
						String albumKey = album.getInfo().getHashId();
						if (prevHashId != null) {
							albumesIdTmp.remove(prevHashId);
						}
						albumesId.put(albumKey, albumId);
					}
					
					result = album;
				}
			} else {
				logWarn(albumIO, "Album sin imagenes. Se omite.");
				if (albumIO.hasAlbumInfo()) {
					logWarn(albumIO, "Eliminando el archivo de datos del album");
					albumIO.deleteAlbumInfo();
				}
			}
		} catch (NoSuchAlgorithmException e) {
			logError(albumIO, e.getMessage());
		}
		return result;
	}

	/**
	 * Metod con MUCHA logica importante y generica.
	 * Hay que abstraerlo de usar File
	 * 
	 * @param directorio
	 * @param recursivo
	 * @param completarConId
	 * @return
	 * @throws JsonProcessingException
	 */
	protected List<AlbumFile> armarAlbum(DirectorioIO directorioIO, final boolean completarConId) throws JsonProcessingException {
		List<AlbumFile> result = new ArrayList<AlbumFile>();
		logDebug(directorioIO, "Armando albumes.");
		//Propio de la implementacion FS
		//File archivosDBTxt[] = directorio.listFiles(fileDataBaseTextFilter);
		for (AlbumIO albumIO : directorioIO) {
			AlbumFile albumFile = armarAlbum(albumIO, completarConId);
			if (albumFile != null)
				result.add(albumFile);
		}
		return  result;
	}

	/**
	 * Este metodo queda tal cual en esta clase.
	 * SAcarlo de las subclases
	 */
	public synchronized void actualizarIds(List<AlbumFile> albumes) {
		for (AlbumFile album : albumes) {
			if (album.isActualizar()) {
				String albumKey = album.getInfo().getHashId();
				if (album.getId() != null) {
					AlbumId albumId = this.albumesId.get(albumKey);
					if (albumId == null)
						albumId = new AlbumId();
					albumId.id = album.getId();
					if (albumId.imgsId == null)
						albumId.imgsId = new HashMap<String, Long>();
					for (ImagenFile imagen : album.getImagenes()) {
						if (imagen.getId() != null) {
							albumId.imgsId.put(imagen.getFileName(), imagen.getId());
						} else {
							logError("Internal: La imagen '"+imagen.getFileName()+"' del album '"+albumKey+"' esta marcado para actualizar su id pero no tiene ninguno asignado.");
						}
					}
					this.albumesId.put(albumKey, albumId);
				} else {
					logError("Internal: El album '"+albumKey+"' esta marcado para actualizar su id pero no tiene ninguno asignado.");
				}
			}
		}
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
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + albumVersionMayor;
//		result = prime * result + albumVersionMenor;
//		result = prime * result + albumVersionRevision;
//		result = prime * result
//				+ ((pathBase == null) ? 0 : pathBase.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		FileInspectorFS other = (FileInspectorFS) obj;
//		if (albumVersionMayor != other.albumVersionMayor)
//			return false;
//		if (albumVersionMenor != other.albumVersionMenor)
//			return false;
//		if (albumVersionRevision != other.albumVersionRevision)
//			return false;
//		if (pathBase == null) {
//			if (other.pathBase != null)
//				return false;
//		} else if (!pathBase.equals(other.pathBase))
//			return false;
//		return true;
//	}

}
