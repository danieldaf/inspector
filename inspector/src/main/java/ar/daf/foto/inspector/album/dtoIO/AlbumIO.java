package ar.daf.foto.inspector.album.dtoIO;

import java.io.IOException;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.album.AlbumFile;

/**
 * Esta clase representa un album en su representacion interna dentro del directorio
 * Tiene por finalidad unicamente poder detectar que fotos contiene.
 * 
 * @author daniel
 *
 */
public interface AlbumIO extends Iterable<String> {
	
	DirectorioIO getDirectorio();

	/**
	 * Devuelve el nombre del album (usarlo en toString)
	 * @return
	 */
	String getNombre();
	/**
	 * Devuelve el nombre completo del album. El nombre completo incluye el nombre del direcotorio
	 * mas un delimitador depedendiente de la tecnologia ('.', '/', ':', etc) seguido por el 
	 * nombre del album
	 * @return
	 */
	String getNombreCompleto();
	/**
	 * Devuelve la fecha de modificacion del album.
	 * @return
	 */
	DateTime getFechaModificacion();
	/**
	 * Devuelve true si el album ya tiene generado la descripcion del album.
	 * @return
	 */
	boolean hasAlbumInfo();
	/**
	 * Si hasAlbumInfo es true, devuelve la descripcion.
	 * Null en caso contrario.
	 * @return
	 */
	AlbumFile loadAlbumInfo() throws IOException;
	/**
	 * Permite borrar definitivamente la descripcion del album.
	 * @return
	 */
	boolean deleteAlbumInfo();
	/**
	 * Devuelve true si el album tiene al menos una imagen cargada.
	 * @return
	 */
	boolean hasImagenes();
	/**
	 * Devuelve la cantidad de imagenes disponibles en el album.
	 * @return
	 */
	int getCountImagenes();
	/**
	 * Devuelve una imagen de acuerdo al nombre
	 * @param nombre
	 * @return
	 */
	Object loadImagen(String nombre) throws IOException;
}
