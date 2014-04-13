package ar.daf.foto.inspector.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import ar.daf.foto.utilidades.HashUtils;

@Test
public class DBMSTest {
	
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("deprecation")
	@BeforeTest
	public void setup() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	@AfterTest
	public void setdown() {
		if (sessionFactory != null)
			sessionFactory.close();
	}

	@Test
	public void altaAlbumSinFotos() throws NoSuchAlgorithmException {
		DateTime fechaActual = new DateTime();
		AlbumInfo albumInfo = new AlbumInfo();
		albumInfo.setVersionMayor(2);
		albumInfo.setVersionMenor(1);
		albumInfo.setVersionRevision(34);
		albumInfo.setFechaActualizacion(fechaActual);
		albumInfo.setContentHash(HashUtils.getHash("Test de prueba de modelo de datos"));
		
		Album album = new Album();
		album.setInfo(albumInfo);
		album.setTitulo("Un album de fotos");
		album.setDescripcion("Cuantas fotos manolete!");
		album.setTags("playa,sierra");
		album.setUbicacion(null);
		album.setFileName("albumcitoSinFotos");
		album.setImagenes(new ArrayList<Imagen>());
		album.setImagenPortada(null);
		album.setPath("/home/xxx/imagenes/fotos/albumes");
		album.setFecha(null);
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(album);
		session.getTransaction().commit();
		session.close();
		
		Assert.assertNotNull(album.getId());
		
		session = sessionFactory.openSession();
		session.beginTransaction();
		Album albumDB = (Album)session.load(Album.class, album.getId());
		session.getTransaction().commit();
		
		Assert.assertNotNull(albumDB);
		Assert.assertNotNull(albumDB.getId());
		Assert.assertNotNull(albumDB.getInfo());
		Assert.assertEquals(albumDB.getInfo().getVersionMayor(), album.getInfo().getVersionMayor());
		Assert.assertEquals(albumDB.getInfo().getVersionMenor(), album.getInfo().getVersionMenor());
		Assert.assertEquals(albumDB.getInfo().getVersionRevision(), album.getInfo().getVersionRevision());
		Assert.assertEquals(albumDB.getInfo().getFechaActualizacion(), album.getInfo().getFechaActualizacion());
		Assert.assertEquals(albumDB.getInfo().getContentHash(), album.getInfo().getContentHash());
		Assert.assertNull(albumDB.getUbicacion());
		Assert.assertTrue(albumDB.getImagenes() == null || albumDB.getImagenes().isEmpty());
		Assert.assertEquals(albumDB.getTitulo(), album.getTitulo());
		Assert.assertEquals(albumDB.getDescripcion(), album.getDescripcion());
		Assert.assertEquals(albumDB.getTags(), album.getTags());
		Assert.assertEquals(albumDB.getFileName(), album.getFileName());
		Assert.assertNull(albumDB.getImagenPortada());
		Assert.assertEquals(albumDB.getFileName(), album.getFileName());
		Assert.assertEquals(albumDB.getPath(), album.getPath());
		Assert.assertNull(albumDB.getFecha());

		session.close();
	}

	@Test
	public void altaAlbumConFotos() throws NoSuchAlgorithmException {
		DateTime fechaActual = new DateTime();
		AlbumInfo albumInfo = new AlbumInfo();
		albumInfo.setVersionMayor(2);
		albumInfo.setVersionMenor(1);
		albumInfo.setVersionRevision(34);
		albumInfo.setFechaActualizacion(fechaActual);
		albumInfo.setContentHash(HashUtils.getHash("Test de prueba de modelo de datos"));
		
		Ubicacion ubicacion = new Ubicacion();
		ubicacion.setPosicionamiento(false);
		ubicacion.setDireccion("Los aromos");
		Album album = new Album();
		album.setInfo(albumInfo);
		album.setTitulo("Un album de fotos");
		album.setDescripcion("Cuantas fotos manolete!");
		album.setTags("playa,sierra");
		album.setUbicacion(ubicacion);
		album.setFileName("albumcitoConFotos");
		album.setImagenes(new ArrayList<Imagen>());
		album.setImagenPortada(null);
		album.setPath("/home/xxx/imagenes/fotos/albumes");
		album.setFecha(null);
		
		Imagen img01 = new Imagen();
		img01.setAlbum(album);
		img01.setTitulo("Brindando!");
		img01.setDescripcion("El primer brindis... fallido.");
		img01.setTags("navidad");
		img01.setFileName("DCIM_001.jpeg");
		img01.setFileNameSmall("thumb_0001.png");
		Imagen img02 = new Imagen();
		img02.setAlbum(album);
		img02.setTitulo("Santa!");
		img02.setDescripcion("La eterna espera a la llegada de Santa");
		img02.setTags("navidad");
		img02.setFileName("DCIM_002.jpeg");
		img02.setFileNameSmall("thumb_0002.png");
		album.getImagenes().add(img01);
		album.getImagenes().add(img02);
		album.setImagenPortada(img02);
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(album);
		session.getTransaction().commit();
		session.close();
		
		Assert.assertNotNull(album.getId());
		
		session = sessionFactory.openSession();
		session.beginTransaction();
		Album albumDB = (Album)session.load(Album.class, album.getId());
		session.getTransaction().commit();
		
		Assert.assertNotNull(albumDB);
		Assert.assertNotNull(albumDB.getId());
		Assert.assertNotNull(albumDB.getInfo());
		Assert.assertEquals(albumDB.getInfo().getVersionMayor(), album.getInfo().getVersionMayor());
		Assert.assertEquals(albumDB.getInfo().getVersionMenor(), album.getInfo().getVersionMenor());
		Assert.assertEquals(albumDB.getInfo().getVersionRevision(), album.getInfo().getVersionRevision());
		Assert.assertEquals(albumDB.getInfo().getFechaActualizacion(), album.getInfo().getFechaActualizacion());
		Assert.assertEquals(albumDB.getInfo().getContentHash(), album.getInfo().getContentHash());
		Assert.assertNotNull(albumDB.getUbicacion());
		Assert.assertFalse(albumDB.getUbicacion().isPosicionamiento());
		Assert.assertEquals(albumDB.getUbicacion().getDireccion(), album.getUbicacion().getDireccion());
		Assert.assertEquals(albumDB.getTitulo(), album.getTitulo());
		Assert.assertEquals(albumDB.getDescripcion(), album.getDescripcion());
		Assert.assertEquals(albumDB.getTags(), album.getTags());
		Assert.assertEquals(albumDB.getFileName(), album.getFileName());
		Assert.assertNotNull(albumDB.getImagenPortada());
		Assert.assertEquals(albumDB.getImagenPortada().getFileName(), img02.getFileName());
		Assert.assertEquals(albumDB.getFileName(), album.getFileName());
		Assert.assertEquals(albumDB.getPath(), album.getPath());
		Assert.assertNull(albumDB.getFecha());
		Assert.assertNotNull(albumDB.getImagenes());
		Assert.assertEquals(albumDB.getImagenes().size(), 2);
		Imagen img01DB = albumDB.getImagenes().get(0);
		Assert.assertNotNull(img01DB);
		Assert.assertNotNull(img01DB.getId());
		Assert.assertNotNull(img01DB.getAlbum());
		Assert.assertEquals(img01DB.getTitulo(), img01.getTitulo());
		Assert.assertEquals(img01DB.getDescripcion(), img01.getDescripcion());
		Assert.assertEquals(img01DB.getFileName(), img01.getFileName());
		Assert.assertEquals(img01DB.getFileNameSmall(), img01.getFileNameSmall());
		Assert.assertEquals(img01DB.getTags(), img01.getTags());
		Imagen img02DB = albumDB.getImagenes().get(1);
		Assert.assertNotNull(img02DB);
		Assert.assertNotNull(img02DB.getId());
		Assert.assertNotNull(img02DB.getAlbum());
		Assert.assertEquals(img02DB.getTitulo(), img02.getTitulo());
		Assert.assertEquals(img02DB.getDescripcion(), img02.getDescripcion());
		Assert.assertEquals(img02DB.getFileName(), img02.getFileName());
		Assert.assertEquals(img02DB.getFileNameSmall(), img02.getFileNameSmall());
		Assert.assertEquals(img02DB.getTags(), img02.getTags());

		session.close();
	}
}
