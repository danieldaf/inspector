package ar.daf.foto.utilidades;

import ar.daf.foto.inspector.model.Imagen;

import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateException;
import com.github.fge.uritemplate.URITemplateParseException;
import com.github.fge.uritemplate.vars.VariableMap;
import com.github.fge.uritemplate.vars.VariableMapBuilder;

public class ImagenUtils {
	
	public static String armarUrl(Imagen imagen) {
		String result = null;
		if (imagen != null) {
			VariableMapBuilder builder = VariableMap.newBuilder();
			builder.addScalarValue("fileNameImagen", imagen.getFileName());
			builder.addScalarValue("fileNameAlbum", imagen.getAlbum().getFileName());
			if (imagen.getAlbum().getPath() != null && !imagen.getAlbum().getPath().isEmpty())
				builder.addScalarValue("path", imagen.getAlbum().getPath());
			VariableMap vars = builder.freeze();
			URITemplate uriT;
			try {
				uriT = new URITemplate("/consultas/album{/path}/{fileNameAlbum}/{fileNameImagen}");
				result = uriT.toString(vars);
			} catch (URITemplateParseException e) {
				e.printStackTrace();
			} catch (URITemplateException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String armarUrlMiniatura(Imagen imagen) {
		String result = null;
		if (imagen != null) {
			VariableMapBuilder builder = VariableMap.newBuilder();
			builder.addScalarValue("fileNameImagen", imagen.getFileName());
			builder.addScalarValue("fileNameAlbum", imagen.getAlbum().getFileName());
			if (imagen.getAlbum().getPath() != null && !imagen.getAlbum().getPath().isEmpty())
				builder.addScalarValue("path", imagen.getAlbum().getPath());
			VariableMap vars = builder.freeze();
			URITemplate uriT;
			try {
				uriT = new URITemplate("/consultas/album{/path}/{fileNameAlbum}/miniatura/{fileNameImagen}");
				result = uriT.toString(vars);
			} catch (URITemplateParseException e) {
				e.printStackTrace();
			} catch (URITemplateException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
