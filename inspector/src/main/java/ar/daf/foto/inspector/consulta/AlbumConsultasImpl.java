package ar.daf.foto.inspector.consulta;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.AlbumDao;
import ar.daf.foto.inspector.model.Imagen;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(readOnly=true, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
public class AlbumConsultasImpl implements AlbumConsultas {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AlbumDao albumDao;
	
	@Value("${inspector.server.version}")
	private String serverVersion;
	
	@Value("${inspector.albumVersionMayor}.${inspector.albumVersionMenor}.${inspector.albumVersionRevision}")
	private String albumVersion;

	@Override
	public QServerInfoDto obtenerMetadatosServer() {
		QServerInfoDto serverInfo = new QServerInfoDto();
		serverInfo.setServerVersion(serverVersion);
		serverInfo.setAlbumVersion(albumVersion);
		return serverInfo;
	}

	@Override
	public List<QAlbumInfoDto> obtenerAlbumes() {
		List<QAlbumInfoDto> result = new ArrayList<QAlbumInfoDto>();
		List<Album> albumes = albumDao.recuperarAlbumes();
		for(Album inA : albumes) {
			QAlbumInfoDto outA = QAlbumInfoDto.fromAlbum(inA);
			result.add(outA);
		}
		return result;
	}

	@Override
	public QAlbumCompletoDto obtenerAlbum(String hashId) {
		QAlbumCompletoDto result = null;
		Album album = albumDao.recuperarAlbum(hashId);
		if (album != null) {
			result = QAlbumCompletoDto.fromAlbum(album);
		}
		return result;
	}
	
	@Override
	public byte[] obtenerImagen(String hashId, String fileNameImagen) {
		//FIXME TODO Esto quedo dependiente de la carga de imagenes desde archivos (desde el FileSystem)
		//Deberia ser abstracto aqui, pero aun no me convence como implementarlo para manterner la abstraccion lograda en la inspeccion de albumes
		//Una solucion, poco elegante pero paractica, seria decidir aqui en base a 'algo' el tipo de carga a emplear...
		byte[] result = null;
		if (hashId != null && fileNameImagen != null) {
			Imagen imagen = albumDao.recuperarImagen(hashId, fileNameImagen);
			if (imagen != null) {
				String fullPath = imagen.getAlbum().getPathBase()+File.separator+imagen.getAlbum().getPath()+File.separator+imagen.getAlbum().getFileName()+File.separator+imagen.getFileName();
				File file = new File(fullPath);
				if (file != null && file.exists() && file.isFile() && file.canRead()) {
					try {
						FileInputStream fis = new FileInputStream(file);;
						byte[] buffer = new byte[1024];
						int countLeidos = fis.read(buffer);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						while (countLeidos > 0) {
							baos.write(buffer, 0, countLeidos);
							countLeidos = fis.read(buffer);
						}
						fis.close();
						result = baos.toByteArray();
					} catch (FileNotFoundException e) {
						log.error(e.getMessage());
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				} else {
					log.warn("Imagen requerida '"+fullPath+"' no accesible desde el file system.");
				}
			} else {
				log.warn("Imagen requerida '"+hashId+"'"+File.separator+"'"+fileNameImagen+"' no encontrada en la base de datos.");
			}
		}
		return result;
	}

}