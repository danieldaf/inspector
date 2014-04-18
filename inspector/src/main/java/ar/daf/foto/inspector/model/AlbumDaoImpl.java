package ar.daf.foto.inspector.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public List<Album> actualizarAlbumes(List<Album> listaAlbumes) {
		List<Album> listaAlbumesResult = new ArrayList<Album>();
		if (listaAlbumes != null && !listaAlbumes.isEmpty()) {
			Session session = sessionFactory.getCurrentSession();
			session.beginTransaction();
			for (Album albumIn : listaAlbumes) {
				Album albumOut = null;
				if (albumIn.getId() != null) {
					albumOut = (Album)session.merge(albumIn);
				} else {
					session.save(albumOut);
				}
				listaAlbumesResult.add(albumOut);
			}
			session.getTransaction().commit();
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

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, isolation=Isolation.READ_UNCOMMITTED)
	@Override
	public Album actualizarAlbum(Album album) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		if (album.getId() != null) {
			album = (Album)session.merge(album);
		} else {
			session.save(album);
		}
		session.getTransaction().commit();
		return album;
	}
	
}
