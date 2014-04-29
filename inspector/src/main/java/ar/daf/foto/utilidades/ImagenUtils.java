package ar.daf.foto.utilidades;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import ar.daf.foto.inspector.model.Imagen;

public class ImagenUtils {
	
	public static String armarUrl(Imagen imagen) {
		String result = null;
		if (imagen != null) {
			//TODO: revisar y mejorar con UriTemplates
			//usar path y fileName como identificador
			try {
				String hash = HashUtils.getHash(imagen.getAlbum().getPath()+File.separator+imagen.getAlbum().getFileName()+File.separator+imagen.getFileName());
				result = "/imagen_"+hash+"_"+imagen.getFileName();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String armarUrlMiniatura(Imagen imagen) {
		String result = null;
		if (imagen != null) {
			//TODO: revisar y mejorar con UriTemplates
			//usar path y fileName como identificador
			try {
				String hash = HashUtils.getHash(imagen.getAlbum().getPath()+File.separator+imagen.getAlbum().getFileName()+File.separator+imagen.getFileName());
				result = "/imagen_mini_"+hash+"_"+imagen.getFileName();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
