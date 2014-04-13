package ar.daf.foto.inspector.rest;

import ar.daf.foto.inspector.model.Imagen;

public class ImagenAlbumDto {

	private String fileName;
	private String fileNameSmall;
	
	public static ImagenAlbumDto fromImagen(Imagen imagen) {
		ImagenAlbumDto result = null;
		if (imagen != null) {
			result = new ImagenAlbumDto();
			result.setFileName(imagen.getFileName());
			result.setFileNameSmall(imagen.getFileNameSmall());
		}
		return result;
	}
	
	public static Imagen toImagen(ImagenAlbumDto imagen) {
		Imagen result = null;
		if (imagen != null) {
			result = new Imagen();
			result.setFileName(imagen.getFileName());
			result.setFileNameSmall(imagen.getFileNameSmall());
		}
		return result;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileNameSmall() {
		return fileNameSmall;
	}

	public void setFileNameSmall(String fileNameSmall) {
		this.fileNameSmall = fileNameSmall;
	} 
}
