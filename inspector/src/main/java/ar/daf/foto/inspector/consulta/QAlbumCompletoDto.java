package ar.daf.foto.inspector.consulta;

import java.util.ArrayList;
import java.util.List;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.Imagen;

public class QAlbumCompletoDto {

	private QAlbumInfoDto info;
	private List<QImagenDto> imagenes;
	
	public static QAlbumCompletoDto fromAlbum(Album album) {
		QAlbumCompletoDto result = null;
		if (album != null) {
			result = new QAlbumCompletoDto();
			result.setInfo(QAlbumInfoDto.fromAlbum(album));
			List<QImagenDto> imagenes = new ArrayList<QImagenDto>();
			result.setImagenes(imagenes);
			if (album.getImagenes() != null) {
				for (Imagen iIn : album.getImagenes()) {
					QImagenDto iOut = QImagenDto.fromImagen(iIn);
					imagenes.add(iOut);
				}
			}
		}
		return result;
	}
	
	public QAlbumInfoDto getInfo() {
		return info;
	}
	public void setInfo(QAlbumInfoDto info) {
		this.info = info;
	}
	public List<QImagenDto> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<QImagenDto> imagenes) {
		this.imagenes = imagenes;
	}
}
