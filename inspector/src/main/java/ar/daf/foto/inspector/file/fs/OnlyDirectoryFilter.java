package ar.daf.foto.inspector.file.fs;

import java.io.File;
import java.io.FileFilter;

public class OnlyDirectoryFilter implements FileFilter {

	public boolean accept(File pathname) {
		return pathname.exists() && pathname.isDirectory() && pathname.canRead() && pathname.canExecute() && !pathname.getName().startsWith(".");
	}

}
