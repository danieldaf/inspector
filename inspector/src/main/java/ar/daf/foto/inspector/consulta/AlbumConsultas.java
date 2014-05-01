package ar.daf.foto.inspector.consulta;

import java.util.List;

public interface AlbumConsultas {

	public QServerInfoDto obtenerMetadatosServer();
	public List<QAlbumInfoDto> obtenerAlbumes();
	public QAlbumCompletoDto obtenerAlbum(String hashIdString);
	public byte[] obtenerImagen(String hashId, String fileNameImagen);
}
