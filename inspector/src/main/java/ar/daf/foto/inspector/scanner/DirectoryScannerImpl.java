package ar.daf.foto.inspector.scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import ar.daf.foto.inspector.album.AlbumFile;
import ar.daf.foto.inspector.album.AlbumInspector;
import ar.daf.foto.inspector.album.AlbumInspectorBuilder;
import ar.daf.foto.inspector.album.ImagenFile;
//import ar.daf.foto.inspector.file.FileInspector;
import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.AlbumDao;
import ar.daf.foto.inspector.model.Imagen;
import ar.daf.foto.utilidades.JsonConverter;

@Service
@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DirectoryScannerImpl implements DirectoryScanner {
	
	private final Logger log = LoggerFactory.getLogger(getClass()); 
	
//	private String homePath = null;
//	@Value("${inspector.dirConfig}")
//	private String dirConfig;
	@Value("${inspector.fileConfigName}")
	private String fileConfig;
	
	@Autowired
	private ServiceConfig config;	
	@Autowired
	private AlbumDao albumDao;
	
	protected List<AlbumInspector> inspectores = new ArrayList<AlbumInspector>();
	
	@PostConstruct
	public void onPostStringConfigure() {
		/*
		 * Andre: Aca es donde se busca y carga el archivo.
		 * Nuevamente como te decia en CoreConfig.java, crea una version del archivo XX en reources
		 * y cambia aca para que lo busque y cargue de ahi.
		 * 
		 * Yo ya te deje una version por defecto del arhivo en reources y modifico el codigo para que busque 
		 * ese archivo.
		 */
//		homePath = System.getProperty("user.home");
//		log.info("HOME_DIR='"+homePath+"'");
//		log.info("CONFIG_DIR='"+dirConfig+"'");
		log.info("CONFIG_FILE='"+fileConfig+"'");
		boolean configFileOk = existsConfigFile();
		boolean loadOk = false;
		if (configFileOk) {
			loadOk = loadConfig();
			if (loadOk) {
				log.debug("Archivo de configuracion cargado con exito.");
			} else {
				log.error("Error de validacion del archivo de configuracion.");
				throw new RuntimeException("Error de validacion del archivo de configuracion.");
			}
		} else {
			log.warn("Archivo de configuracion no existe.");
		}
		if (!configFileOk || loadOk) {
			log.debug("Persistiendo configuracion cargda.");
			saveConfig();
		}
		log.info("Configuracion inicial cargada.");
		this.updateInspectors();
	}
	
	public boolean validateConfig(ServiceConfig cfg) {
		if (cfg == null) 
			return false;
		
		List<String> extensiones = cfg.getExtensiones();
		if (extensiones == null || extensiones.isEmpty()) {
			log.warn("No hay extensiones validas para identificar los archvios de tipo imagen.");
			return false;
		}
		
		String tmp = cfg.getDbTextFileName();
		if (tmp != null)
			tmp = tmp.trim();
		
		int pos = tmp.lastIndexOf('.');
		if (pos != -1) {
			String ext = tmp.substring(pos+1).trim().toUpperCase();
			for (String imgExt : extensiones) {
				if (ext.equals(imgExt)) {
					log.warn("La extension del archivo de configuracion '"+tmp+"' no puede estar incluida dentro de las extensiones definidas para los archivos de tipo imagen.");
					return false;
				}
			}
		}
		
		return true;	
	}
	
	protected String getFullPathFileConfig() {
		/*
		 * Andre: Aca cambio para buscar y cargar el archivo desde el path de los recursos (el classpath)
		 */
//		return this.homePath+File.separator+this.dirConfig+File.separator+this.fileConfig;
		ClassPathResource fileConfigResource = new ClassPathResource(this.fileConfig);
		String homePath = null;
		try {
			homePath = fileConfigResource.getFile().getCanonicalPath();
		} catch (IOException e) {
			log.error(e.getMessage(),  e);
		} 
		 return homePath;
	}
	
	public boolean existsConfigFile() {
		File fis = new File(getFullPathFileConfig());
		if (fis != null && fis.exists() && fis.isFile() && fis.canRead())
			return true;
		return false;
	}
	
	public boolean loadConfig() {
		boolean result = false;
		ServiceConfig cfg = null;
		try {
			FileInputStream fis = new FileInputStream(getFullPathFileConfig());
			log.debug("Leyendo archivo de configuracion: '"+getFullPathFileConfig()+"'");
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis, "utf8"));
			StringBuffer buffer = new StringBuffer();
			String strLinea = br.readLine();
			while (strLinea != null) {
				buffer.append(strLinea);
				strLinea = br.readLine();
			}
			br.close();
			log.debug("Configuracion leido con exito.");
			cfg = JsonConverter.buildObject(ServiceConfig.class, buffer.toString());
			log.debug("Configuracion parseado desde su formato JSON.");
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		}
		
		if (cfg != null) {
			if (validateConfig(cfg)) {
				log.debug("Configuracion valida.");
				config = cfg;
				result = true;
			} else {
				log.warn("Configuracion no valida.");
			}
		} else {
			log.warn("No se pudo leer el archivo de configuracion.");
		}
		return result;
	}
	
	protected boolean createDirIfNotExists(String dir) {
		boolean result = false;
		File fDir = new File(dir);
		if (!fDir.exists()) {
			result = fDir.mkdir();
		}
		return result;
	}
	
	public boolean saveConfig() {
		boolean result = false;
//		if (homePath != null) {
			try {
//				createDirIfNotExists(this.homePath+File.separator+this.dirConfig);
				File archivoAlbum = new File(getFullPathFileConfig());
				String json = JsonConverter.buildJson(config);
				if (json != null && !json.isEmpty()) {
					log.debug("Guardando archivo de configuracion '"+archivoAlbum.toString()+"'");
					if (!archivoAlbum.exists()) {
						archivoAlbum.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(archivoAlbum);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf8"));
					bw.write(json);
					bw.close();
				} else {
					log.warn("No hay ninguna configuracion para guardar.");
				}
				result = true;
			} catch (IOException e) {
				log.error(e.getMessage());
			}
//		}
		return result;
	}
	
	public void updateInspectors() {
		log.info("Actualizando los inspectores de directorios.");
		List<AlbumInspector> newInspectores= new ArrayList<AlbumInspector>();
		List<AlbumInspector> prevInspectores = new ArrayList<AlbumInspector>();
		synchronized (inspectores) {
			prevInspectores.addAll(inspectores);
		}
		if (config.getPaths() != null) {
			for (String path : config.getPaths()) {
				File pathDir = new File(path);
				if (!pathDir.exists()) {
					log.warn("Omitiendo la inspeccion del directorio '"+path+"' porque no existe.");
				} else if (!pathDir.isDirectory()) {
					log.warn("Omitiendo la inspeccion del directorio '"+path+"' porque no es un directorio.");
				} else if (pathDir.getParent() == null) {
					log.warn("Omitiendo la inspeccion del directorio '"+path+"' poque es un directorio raiz. Solo se puede configurar subdirectorios como carpetas de albumes.");
				} else {
					try {
						path = pathDir.getCanonicalPath();
					} catch (IOException e) {
						log.warn("No se pudo determianr el path unico para '"+path+"':"+ e.getMessage());
					}
					AlbumInspector inspector = AlbumInspectorBuilder.buildAlbumInspector(path, config);
					if (prevInspectores.contains(inspector)) {
						inspector = prevInspectores.get(prevInspectores.indexOf(inspector));
						log.debug("Reusando el inspector para el directorio: '"+path+"'");
					} else {
						log.debug("Agregado un inspector nuevo para el directorio: '"+path+"'");
					}
					newInspectores.add(inspector);
				}
			}
		}
		synchronized (inspectores) {
			inspectores.clear();
			inspectores.addAll(newInspectores);
		}
		log.info("Inspectores de directorios actualizados");
	}
	
	public void scan() {
		log.info("Actualizando todos los albumes.");
		List<AlbumInspector> tmpfi = new ArrayList<AlbumInspector>();
		synchronized (inspectores) {
			tmpfi.addAll(inspectores);
		}
		for (AlbumInspector inspector : tmpfi) {
			List<AlbumFile> albumesF = inspector.inspeccionar();
			mergeAlbumes(albumesF);
			inspector.actualizarIds(albumesF);
		}
		log.info("Albumes actualizados.");
	}
	
	protected void mergeAlbumes(List<AlbumFile> inAlbumes) {
		List<Album> outAlbumes = new ArrayList<Album>();
		Map<String, AlbumFile> inAlbumesMap = new HashMap<String, AlbumFile>();
		Map<String, Map<String, ImagenFile>> inImagenesMap = new HashMap<String, Map<String, ImagenFile>>();
		
		for (AlbumFile inA : inAlbumes) {
			Album outA = AlbumFile.toAlbum(inA);
			if (inA.isActualizado()) {
				outAlbumes.add(outA);
				
				String strKey = inA.getInfo().getHashId();
				inAlbumesMap.put(strKey, inA);
				Map<String, ImagenFile> inImap = new HashMap<String, ImagenFile>();
				for (ImagenFile inI : inA.getImagenes()) {
					inImap.put(inI.getFileName(), inI);
				}
				inImagenesMap.put(strKey, inImap);
			}
		}
		if (!outAlbumes.isEmpty()) {
			outAlbumes = albumDao.actualizarAlbumes(outAlbumes);
			for (Album outA : outAlbumes) {
				String strKey = outA.getInfo().getHashId();
				AlbumFile inA = inAlbumesMap.get(strKey);
				if (inA != null) {
					inA.setId(outA.getId());
					Map<String, ImagenFile> inImap = inImagenesMap.get(strKey);
					for (Imagen outI : outA.getImagenes()) {
						ImagenFile inI = inImap.get(outI.getFileName());
						inI.setId(outI.getId());
					}
					inA.setActualizar(true);
				}
			}
		}
	}

}
