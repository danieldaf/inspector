package ar.daf.foto.inspector.consulta;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.AlbumDao;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(readOnly=true, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
public class AlbumConsultasImpl implements AlbumConsultas {
	
	@Autowired
	private AlbumDao albumDao;
	
	@Autowired
	private Environment env;
	
	private QServerInfoDto serverInfo;

	@PostConstruct
	public void completarInfoEstatica() {
		serverInfo = new QServerInfoDto();
		serverInfo.setServerVersion(env.getProperty("inspector.server.version"));
		serverInfo.setAlbumVersion(env.getProperty("inspector.albumVersionMayor")+"."+env.getProperty("inspector.albumVersionMenor")+"."+env.getProperty("inspector.albumVersionRevision"));
	}

	@Override
	public QServerInfoDto obtenerMetadatosServer() {
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
	public QAlbumCompletoDto obtenerAlbum(String path, String fileName) {
		QAlbumCompletoDto result = null;
		Album album = albumDao.recuperarAlbum(path, fileName);
		if (album != null) {
			result = QAlbumCompletoDto.fromAlbum(album);
		}
		return result;
	}

}