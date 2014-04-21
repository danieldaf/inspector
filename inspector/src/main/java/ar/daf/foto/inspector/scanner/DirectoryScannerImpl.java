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
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ar.daf.foto.utilidades.JsonConverter;

@Service
@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DirectoryScannerImpl implements DirectoryScanner {
	
	public static long READ_INTERVAL_INFINITE = -1;
	
	private String homePath = null;
	@Value("${inspector.fileConfigName}")
	private String fileConfig;
	
	@Autowired
	private ServiceConfig config;
	
	@PostConstruct
	public void onPostStringConfigure() {
		homePath = System.getProperty("user.home");
		boolean configFileOk = existsConfigFile();
		boolean loadOk = false;
		if (configFileOk)
			loadOk = loadConfig();
		if (!configFileOk || loadOk) {
			saveConfig();
		}
	}
	
	public boolean validateConfig(ServiceConfig cfg) {
		if (cfg == null) 
			return false;
		
		List<String> extensiones = cfg.getExtensiones();
		if (extensiones == null || extensiones.isEmpty())
			return false;
		
		String tmp = cfg.getDbTextFileName();
		if (tmp != null)
			tmp = tmp.trim();
		
		int pos = tmp.lastIndexOf('.');
		if (pos != -1) {
			String ext = tmp.substring(pos+1).trim().toUpperCase();
			for (String imgExt : extensiones) {
				if (ext.equals(imgExt)) {
					return false;
				}
			}
		}
		
		return true;	
	}
	
	public boolean existsConfigFile() {
		File fis = new File(this.homePath+File.separator+this.fileConfig);
		if (fis != null && fis.exists() && fis.isFile() && fis.canRead())
			return true;
		return false;
	}
	
	public boolean loadConfig() {
		boolean result = false;
		ServiceConfig cfg = null;
		try {
			FileInputStream fis = new FileInputStream(this.homePath+File.separator+this.fileConfig);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis, "utf8"));
			StringBuffer buffer = new StringBuffer();
			String strLinea = br.readLine();
			while (strLinea != null) {
				buffer.append(strLinea);
				strLinea = br.readLine();
			}
			br.close();
			cfg = JsonConverter.buildObject(ServiceConfig.class, buffer.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		if (cfg != null) {
			if (validateConfig(cfg)) {
				config = cfg;
				result = true;
			}
		}
		return result;
	}
	
	public boolean saveConfig() {
		boolean result = false;
		if (homePath != null) {
			try {
				File archivoAlbum = new File(this.homePath+File.separator+this.fileConfig);
				String json = JsonConverter.buildJson(config);
				if (json != null && !json.isEmpty()) {
					if (!archivoAlbum.exists()) {
						archivoAlbum.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(archivoAlbum);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf8"));
					bw.write(json);
					bw.close();
				}
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void scan() {
		for (String path : config.getPaths()) {
			
		}
	}

}
