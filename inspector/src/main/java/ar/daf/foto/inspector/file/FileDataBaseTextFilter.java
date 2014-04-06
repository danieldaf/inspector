package ar.daf.foto.inspector.file;

import java.io.File;
import java.io.FileFilter;

public class FileDataBaseTextFilter implements FileFilter {
	
	private String dbTextFile;
	
	public FileDataBaseTextFilter(String dbTextFile) {
		this.dbTextFile = dbTextFile; 
	}
	
	public boolean accept(File pathname) {
		boolean result = true;
		result = pathname.exists() && pathname.isFile() && pathname.canRead();
		if (result) {
			String name = pathname.getName().toUpperCase();
			result = dbTextFile.equals(name);
		}
		return result;
	}

}
