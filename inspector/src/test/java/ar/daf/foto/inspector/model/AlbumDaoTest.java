package ar.daf.foto.inspector.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.joda.time.DateTime;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import ar.daf.foto.utilidades.HashUtils;

@Test
public class AlbumDaoTest {

	private SessionFactory sessionFactory;
	private AlbumDaoImpl albumDao;
	private Album album;
	
	@SuppressWarnings("deprecation")
	@BeforeTest
	public void setup() throws NoSuchAlgorithmException {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		
		albumDao = new AlbumDaoImpl();
		albumDao.setSessionFactory(sessionFactory);

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
		album = new Album();
		album.setInfo(albumInfo);
		album.setTitulo("Un album de fotos");
		album.setDescripcion("Cuantas fotos manolete!");
		album.setTags("playa,sierra");
		album.setUbicacion(ubicacion);
		album.setFileName("albumcito");
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
	}
	
	@AfterTest
	public void setdown() {
		if (sessionFactory != null)
			sessionFactory.close();
	}
	
	
	@Test
	public void guardarAlbumNuevo() {
		Album albumActualizado = albumDao.actualizarAlbum(album);
		
		Assert.assertNotNull(albumActualizado);
		Assert.assertNotNull(albumActualizado.getId());
		Assert.assertNotNull(albumActualizado.getInfo());
		Assert.assertNotNull(albumActualizado.getUbicacion());
		Assert.assertNotNull(albumActualizado.getImagenes());
		Assert.assertEquals(albumActualizado.getTitulo(), album.getTitulo());
		Assert.assertEquals(albumActualizado.getInfo().getContentHash(), album.getInfo().getContentHash());
		Assert.assertEquals(albumActualizado.getImagenes().size(), album.getImagenes().size());
	}
	
	@Test(dependsOnMethods={"guardarAlbumNuevo"})
	public void cargarAlbumExistente() {
		sessionFactory.getCurrentSession().beginTransaction();
		try {
			sessionFactory.getCurrentSession().merge(album);
			
			Album albumRecargado = albumDao.recuperarAlbum(album.getId());
			
			Assert.assertNotNull(albumRecargado);
			Assert.assertNotNull(albumRecargado.getId());
			Assert.assertNotNull(albumRecargado.getInfo());
			Assert.assertNotNull(albumRecargado.getUbicacion());
			Assert.assertNotNull(albumRecargado.getImagenes());
			Assert.assertEquals(albumRecargado.getTitulo(), album.getTitulo());
			Assert.assertEquals(albumRecargado.getInfo().getContentHash(), album.getInfo().getContentHash());
			Assert.assertNotNull(albumRecargado.getImagenPortada());
			Assert.assertEquals(albumRecargado.getImagenPortada().getFileName(), album.getImagenPortada().getFileName());
			Assert.assertEquals(albumRecargado.getImagenes().size(), album.getImagenes().size());
		} finally {
			sessionFactory.getCurrentSession().getTransaction().commit();
		}
	}

	@Test(dependsOnMethods={"guardarAlbumNuevo"})
	public void modificarAlbumExistente() {
		sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().merge(album);
		
		Album albumRecargado = albumDao.recuperarAlbum(album.getId());

		String nuevoTitulo = "Este es el nuevo titulo";
		albumRecargado.setTitulo(nuevoTitulo);		
		sessionFactory.getCurrentSession().getTransaction().commit();
		
		Album albumActualizado = albumDao.actualizarAlbum(albumRecargado);
		
		Assert.assertNotNull(albumActualizado);
		Assert.assertNotNull(albumActualizado.getId());
		Assert.assertEquals(albumActualizado.getTitulo(), nuevoTitulo);
	}
	
}