package ar.daf.foto.inspector.file;

import java.io.File;
import java.io.FileFilter;

public class OnlyFileImagesFilter implements FileFilter {
	
	private String extensions[] = {};
	
	public OnlyFileImagesFilter(String[] extensiones) {
		this.extensions = extensiones;
	}

	public boolean accept(File pathname) {
		boolean result = true;
		result = pathname.exists() && pathname.isFile() && pathname.canRead() && !pathname.getName().startsWith(".");;
		if (result) {
			String name = pathname.getName().toUpperCase();
			boolean noImagen = true;
			for (int pos=0; pos<extensions.length && noImagen; pos++) {
				if (name.endsWith(extensions[pos]))
					noImagen = false;
			}
			result = !noImagen;
		}
		return result;
	}

}
