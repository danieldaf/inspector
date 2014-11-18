package ar.daf.foto.inspector.album.fs;

import java.util.Comparator;

public class FileImageComparator implements Comparator<String> {

	public int compare(String str1, String str2) {
		int result = 0;
		if (str1 == null && str2 == null)
			result = 0;
		else if (str1 == null && str2 != null)
			result = -1;
		else if (str1 != null && str2 == null)	
			result = 1;
		else  
			result = str1.compareTo(str2);
		return result;
	}

}
