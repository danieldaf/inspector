package ar.daf.foto.inspector.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(readOnly=true, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
public class AlbumDaoImpl implements AlbumDao {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public List<Album> actualizarAlbumes(List<Album> listaAlbumes) {
		log.debug("Actualizando listado de albumes");
		List<Album> listaAlbumesResult = new ArrayList<Album>();
		if (listaAlbumes != null && !listaAlbumes.isEmpty()) {
			Session session = sessionFactory.getCurrentSession();
			for (Album albumIn : listaAlbumes) {
				Album albumOut = null;
				if (albumIn.getId() != null) {
					log.debug("Actualizando el album '"+albumIn.getFileName()+"' de id="+albumIn.getId());
					albumOut = (Album)session.merge(albumIn);
				} else {
					Album albumDB = this.recuperarAlbum(albumIn.getPath(), albumIn.getFileName());
					if (albumDB != null) {
						albumIn.setId(albumDB.getId());
						log.debug("Actualizando el album '"+albumIn.getFileName()+"' de id="+albumIn.getId());
						albumOut = (Album)session.merge(albumIn);
					} else {
						log.debug("Creando el album '"+albumIn.getFileName()+"'");
						session.save(albumIn);
						albumOut = albumIn;
					}
				}
				listaAlbumesResult.add(albumOut);
			}
		}
		return listaAlbumesResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Album> recuperarAlbumes() {
		List<Album> result = null;
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Album");
		result = query.list();
		return result;
	}

	@Override
	public Album recuperarAlbum(Long id) {
		Album result = null;
		Session session = sessionFactory.getCurrentSession();
		result = (Album)session.load(Album.class, id);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Album recuperarAlbum(String path, String fileName) {
		Album result = null;
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Album where path=:path and fileName=:fileName");
		query.setParameter("path", path);
		query.setParameter("fileName", fileName);
		query.setMaxResults(1);
		List<Album> listResult = query.list();
		if (listResult != null && !listResult.isEmpty())
			result = listResult.get(0);
		return result;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public Album actualizarAlbum(Album album) {
		Session session = sessionFactory.getCurrentSession();
		if (album.getId() != null) {
			log.debug("Actualizando el abum '"+album.getFileName()+"' de id="+album.getId());
			album = (Album)session.merge(album);
		} else {
			Album albumDB = this.recuperarAlbum(album.getPath(), album.getFileName());
			if (albumDB != null) {
				album.setId(albumDB.getId());
				log.debug("Actualizando el abum '"+album.getFileName()+"' de id="+album.getId());
				album = (Album)session.merge(album);
			} else {
				log.debug("Creando el abum '"+album.getFileName()+"'");
				session.save(album);
			}
		}
		return album;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Imagen recuperarImagen(String path, String fileNameAlbum, String fileNameImagen) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Imagen where fileName=:fileNameImagen and album.path=:path and album.fileName=:fileNameAlbum");
		query.setParameter("path", path);
		query.setParameter("fileNameAlbum", fileNameAlbum);
		query.setParameter("fileNameImagen", fileNameImagen);
		query.setMaxResults(1);
		List<Imagen> listResult = query.list();
		Imagen imagen = null;
		if (listResult != null && !listResult.isEmpty())
			imagen = listResult.get(0);
		return imagen;
	}
	
}