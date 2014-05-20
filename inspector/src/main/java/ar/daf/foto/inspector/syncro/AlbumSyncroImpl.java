package ar.daf.foto.inspector.syncro;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.model.AlbumDao;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(readOnly=true, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
public class AlbumSyncroImpl implements AlbumSyncro {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AlbumDao albumDao;
	
	@Override
	public List<SyncAlbumInfoDto> obtenerInfoAlbumes() {
		List<Album> albumes = albumDao.recuperarAlbumes();
		List<SyncAlbumInfoDto> result = new ArrayList<SyncAlbumInfoDto>();
		if (albumes != null && !albumes.isEmpty()) {
			for (Album album : albumes) {
				result.add(SyncAlbumInfoDto.fromAlbum(album.getInfo()));
			}
		}
		return result;
	}

	@Override
	public List<SyncAlbumDto> obtenerAlbumes(List<String> hashIdAlbumes) {
		List<Album> albumes = albumDao.recuperarAlbumes(hashIdAlbumes);
		List<SyncAlbumDto> result = new ArrayList<SyncAlbumDto>();
		if (albumes != null && !albumes.isEmpty()) {
			for (Album album : albumes) {
				result.add(SyncAlbumDto.fromAlbum(album));
			}
		}
		return result;
	}

}
