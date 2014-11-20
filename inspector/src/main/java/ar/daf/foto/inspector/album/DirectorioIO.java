package ar.daf.foto.inspector.album;

import org.joda.time.DateTime;

/**
 * Esta clase representa el punto de entrada a partir del cual consultar los albumes disponibles.
 * Conviene redefinir el toString para que muestre el identificado del directorio en modo texto (para los logs)
 * 
 * @author daniel
 *
 */
public interface DirectorioIO extends Iterable<AlbumIO> {

	/**
	 * Retorna el nombre del directorio (usarlo en toString)
	 * @return
	 */
	String getNombre();
	/**
	 * Retorna la fecha de modificacion del album.
	 * 
	 * @return
	 */
	DateTime getFechaModificacion();

}
