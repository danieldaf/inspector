package ar.daf.foto.inspector.scanner;

public interface DirectoryScanner {

	/**
	 * Metodo que se encarga de validar y dar el ok para la configuracion pasada como parametro.
	 * @param config
	 * @return
	 * 	true si la configuracion es valida
	 */
	public boolean validateConfig(ServiceConfig config);
	
	/**
	 * Metodo que se encarga de establecer la configuracion actual cargandola desde el archivo 
	 * por defecto.
	 * Unicamente se carga si la configuracion del archivo es valida.
	 * @return
	 *  true si la configuracion peude cargarse
	 */
	public boolean loadConfig();
	
	/**
	 * Metodo que se encarga de persistir la configuracion actual en el archivo pro defecto.
	 * @return
	 *   true si la configuracion puede ser persistida
	 */
	public boolean saveConfig();

	/**
	 * Metodo que se encarga de actualizar la lista de inpsectores activos para los paths 
	 * que se estan revisando.
	 * Es necesario invocar este metodo cuando se agregan o quitan las carpetas a inspecionar.
	 */
	public void updateInspectors();
	
	/**
	 * Metodo que se encarga de actualizar los albumes segun los inspectores configurados.
	 * Este metodo deberia invocarse regularmente para actualizar los albumes.
	 */
	public void scan();

}
