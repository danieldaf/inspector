package ar.daf.foto.inspector.consulta;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface AlbumConsultasRest {

	public QServerInfoDto obtenerMetadatosServer();
	public List<QAlbumInfoDto> obtenerAlbumes();
	public QAlbumCompletoDto obtenerAlbum(String path, String fileName);
	public ResponseEntity<byte[]> obtenerImagen(String path, String fileNameAlbum, String fileNameImagen, String fileExt);
}
