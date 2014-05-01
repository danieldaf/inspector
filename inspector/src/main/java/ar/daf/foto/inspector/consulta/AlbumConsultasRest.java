package ar.daf.foto.inspector.consulta;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface AlbumConsultasRest {

	public QServerInfoDto obtenerMetadatosServer();
	public List<QAlbumInfoDto> obtenerAlbumes();
	public ResponseEntity<QAlbumCompletoDto> obtenerAlbum(String hashId);
	public ResponseEntity<byte[]> obtenerImagen(String hashId, String fileNameImagen, String fileExt);
	public ResponseEntity<byte[]> obtenerImagenMiniatura(@PathVariable("hashId")String hashId, @PathVariable("fileNameImagen")String fileNameImagen, @PathVariable("fileExt")String fileExt);
}
