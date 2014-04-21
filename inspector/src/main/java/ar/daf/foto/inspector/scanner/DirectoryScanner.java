package ar.daf.foto.inspector.scanner;

public interface DirectoryScanner {

	public boolean validateConfig(ServiceConfig config);
	public boolean loadConfig();
	public boolean saveConfig();
	public void scan();

}
