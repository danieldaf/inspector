package ar.daf.foto.inspector.syncro;

import java.util.List;

public interface AlbumSyncro {

	public List<SyncAlbumInfoDto> obtenerInfoAlbumes();
	
	public List<SyncAlbumDto> obtenerAlbumes(List<String> hashIdAlbumes);
	
}
