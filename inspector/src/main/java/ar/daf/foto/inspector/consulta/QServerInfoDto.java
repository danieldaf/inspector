package ar.daf.foto.inspector.consulta;

import org.springframework.hateoas.ResourceSupport;

public class QServerInfoDto extends ResourceSupport {

	private String serverVersion;
	private String albumVersion;

	public String getServerVersion() {
		return serverVersion;
	}
	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}
	public String getAlbumVersion() {
		return albumVersion;
	}
	public void setAlbumVersion(String albumVersion) {
		this.albumVersion = albumVersion;
	}
	
}
