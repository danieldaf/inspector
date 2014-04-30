package ar.daf.foto.inspector.consulta;

import java.util.List;

public interface AlbumConsultas {

	public QServerInfoDto obtenerMetadatosServer();
	public List<QAlbumInfoDto> obtenerAlbumes();
	public QAlbumCompletoDto obtenerAlbum(String path, String fileName);
	public byte[] obtenerImagen(String path, String fileNameAlbum, String fileNameImagen);
}
