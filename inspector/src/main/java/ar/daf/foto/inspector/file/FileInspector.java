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
import org.json.simple.JSONObject;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.AlbumInfo;
import ar.daf.foto.inspector.model.Imagen;
import ar.daf.foto.utilidades.HashUtils;
import ar.daf.foto.utilidades.json.JsonConverter;

public class FileInspector {
	
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
	private List<Album> albumes = new ArrayList<Album>();
	
	public FileInspector(String pathBase) {
		this.pathBase = pathBase;
		this.fileDataBaseTextFilter = new FileDataBaseTextFilter(this.dbTextFileName);
		this.fileImageFilter = new OnlyFileImagesFilter(this.extensiones);
		this.directoryFilter = new OnlyDirectoryFilter();
		this.fileImageComparator = new FileImageComparator();
	}

	public List<Album> inspeccionar() {
		albumes.clear();
		File fileBase = new File(pathBase);
		if (fileBase != null && fileBase.exists() && fileBase.canRead() && fileBase.isDirectory() && fileBase.canExecute()) {
			albumes.addAll(armarAlbum(fileBase, true));
		}
		return albumes;
	}
	
	protected Album cargarAlbum(File dbTxt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
		Album result = null;
		try {
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
			result = JsonConverter.buildObject(Album.class, buffer.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	protected String armarHashAlbum(Album album) throws NoSuchAlgorithmException {
		String result = null;
		DateTime fechaActPrev = album.getInfo().getFechaActualizacion();
		String hashPrev = album.getInfo().getContentHash();
		album.getInfo().setFechaActualizacion(null);
		album.getInfo().setContentHash(null);
		String contenidoSinHash = JsonConverter.buildJson(result).toJSONString();
		result = HashUtils.getHash(contenidoSinHash);
		album.getInfo().setFechaActualizacion(fechaActPrev);
		album.getInfo().setContentHash(hashPrev);
		return result;
	}
	
	protected boolean verificarVigenciaAlbum(Album album, File directorio, File archivosImagenes[]) throws NoSuchAlgorithmException {
		boolean result = false;
		if (album != null && album.getPath() != null && album.getFilename() != null) {
			if (album.getInfo() == null) {
				result = true;
			} else {
				if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
					//1.- Revisamos si la fecha del album persistida es mas vieja que la de la carpeta
					if (album.getInfo().getFechaActualizacion() != null && album.getInfo().getFechaActualizacion().isBefore(directorio.lastModified())) {
						result = true;
					} else {
						//2.- Revisamos si la cantidad de imagenes del album cambio
						if (archivosImagenes.length != album.getImagenes().size()) {
							result = true;
						} else {
							//3.- Revisamos si el contenido del archivo de texto del album cambio
							String hashReal = armarHashAlbum(album);
							if (!hashReal.equals(album.getInfo().getContentHash())) {
								result = true;
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
								for (Imagen img : album.getImagenes()) {
									String nombre = img.getFileName();
									int posImg = Collections.binarySearch(imagenes, nombre, fileImageComparator);
									if (posImg < 0) {
										result = true;
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
	
	public Album actualizarAlbum(Album album) {
		if (album != null) {
			File directorio = new File(album.getPath());
			if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
				File archivosImagenes[] = directorio.listFiles(fileImageFilter);
				try {
					album = actualizarAlbum(album, directorio, archivosImagenes);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		}
		return album;
	}
	
	protected Album actualizarAlbum(Album album, File directorio, File archivosImagenes[]) throws NoSuchAlgorithmException {
		/*
		 * El album a actualizar fur cargado desde el archivo de disco.
		 * Por lo tanto todas sus descripciones son correctas en memoria.
		 * Si se cambiaron a mano, en forma externa hay que actualizar la fecha y el hash correspondiente.
		 * Otra cosa que pudo haber cambiado sino son las imagenes, los nombres y la cantidad.
		 * Eso hay que revisarlo.
		 */
		List<Imagen> imagenes = new ArrayList<Imagen>();
		for (File archivo : archivosImagenes) {
			Imagen imagenPrev = null;
			for (Imagen img : album.getImagenes()) {
				if (archivo.getName().equals(img.getFileName())) {
					imagenPrev = img;
					break;
				}
			}
			if (imagenPrev == null) {
				//El archivo es nuevo
				imagenPrev = new Imagen();
				imagenPrev.setTitulo(archivo.getName());
				imagenPrev.setDescripcion(null);
				imagenPrev.setTags(null);
				imagenPrev.setFileName(archivo.getName());
				imagenPrev.setFileNameSmall(null);
			} else {
				//el archivo ya existe yesta catalogado
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
	
	protected Album construirAlbum(File directorio, File archivosImagenes[]) throws NoSuchAlgorithmException {
		Album result = new Album();
		AlbumInfo info = new AlbumInfo();
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
		result.setFilename(directorio.getName());
		result.setImagenes(new ArrayList<Imagen>());
		
		DateTime firstDate = null;
		for (File archivoImagen : archivosImagenes) {
			Imagen imagen = new Imagen();
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
	
	protected void salvarAlbum(Album album, File directorio) throws IOException {
		File archivoAlbum = new File(album.getPath()+File.separator+album.getFilename());
		if (!archivoAlbum.exists()) {
			archivoAlbum.createNewFile();
		}
		album.getInfo().setFechaActualizacion(new DateTime(directorio.lastModified()));
		JSONObject json = JsonConverter.buildJson(album);
		FileOutputStream fos = new FileOutputStream(archivoAlbum);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, dbFileEncoding));
		bw.write(json.toJSONString());
		bw.close();
	}
	
	public Album armarAlbum(String path) {
		Album result= null;
		File directorio = new File(path);
		if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
			List<Album> lst = armarAlbum(directorio, true);
			if (lst != null && lst.isEmpty())
				result = lst.get(0);
		}
		return result;
	}
	
	public List<Album> armarAlbumes(String path) {
		List<Album> result = new ArrayList<Album>();
		File directorio = new File(path);
		if (directorio != null && directorio.exists() && directorio.isDirectory() && directorio.canRead() && directorio.canExecute()) {
			result.addAll(armarAlbum(directorio, true));		
		}
		return result;
	}
	
	protected List<Album> armarAlbum(File directorio, boolean recursivo) {
		List<Album> result = new ArrayList<Album>();
		try {
			File archivosDBTxt[] = directorio.listFiles(fileDataBaseTextFilter);
			
			File archivosImagenes[] = directorio.listFiles(fileImageFilter);
			if (archivosImagenes != null && archivosImagenes.length > 0) {
				Album album = null;
				if (archivosDBTxt.length == 1) {
					//1.- cargar album desde db
					try {
						album = cargarAlbum(archivosDBTxt[0]);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (album == null) {
							archivosDBTxt[0].delete();
						}
					}
					if (album != null) {
						//2.- verificar si el hay que actualizar
						boolean esVigente = verificarVigenciaAlbum(album, directorio, archivosImagenes);
						//2.1.- si actualizar -> cargar desde disco
						if (!esVigente) {
							actualizarAlbum(album, directorio, archivosImagenes);
						}
					}
				} else {
					//construir desde disco
					album = construirAlbum(directorio, archivosImagenes);
				}
				
				//persistir album
				if (album != null) {
					try {
						salvarAlbum(album, directorio);
					} catch (IOException e) {
						e.printStackTrace();
					} 
					result.add(album);
				}
			} else {
				if (archivosDBTxt.length == 1) {
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

	public List<Album> getAlbumes() {
		return albumes;
	}
	
}