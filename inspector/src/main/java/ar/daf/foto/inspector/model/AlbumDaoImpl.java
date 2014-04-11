package ar.daf.foto.inspector.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class AlbumDaoImpl implements AlbumDao {
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
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
		Query query = session.createQuery("from album");
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
