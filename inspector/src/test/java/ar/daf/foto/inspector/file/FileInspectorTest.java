package ar.daf.foto.inspector.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.utilidades.json.JsonConverter;

@Test
public class FileInspectorTest {
	
	Path pathBase;
	Path pathNavidad, pathVacaciones, pathVacaciones1998, pathVacaciones2010;	
	File fotoYo, fotoPerro, fotoGato;
	File fotoNavidad001, fotoNavidad002, fotoNavidad003, fotoNavidad004;
	File fotoVacacion98_01, fotoVacacion98_02, fotoVacacion98_03;
	File fotoVacacion10_01, fotoVacacion10_02, fotoVacacion10_03;
	FileInspector inspector;
	
	@BeforeTest
	public void crearArchivosTemporales() throws IOException {
		pathBase = Files.createTempDirectory("armarAlbumFotoInspectorTest");
		pathNavidad = Paths.get(pathBase.toString(), "Navidad");
		pathVacaciones = Paths.get(pathBase.toString(), "Vacaciones");
		pathVacaciones1998 = Paths.get(pathBase.toString(), "Vacaciones", "1998");
		pathVacaciones2010 = Paths.get(pathBase.toString(), "Vacaciones", "2010");
		
		Files.createDirectories(pathNavidad);
		Files.createDirectories(pathVacaciones1998);
		Files.createDirectories(pathVacaciones2010);
		
		fotoYo = new File(pathBase.toString()+File.separator+"fotoYo.png");
		fotoPerro = new File(pathBase.toString()+File.separator+"FotoPerro.PNG");
		fotoGato = new File(pathBase.toString()+File.separator+"Fotogato.pNG");
		fotoYo.createNewFile();
		fotoPerro.createNewFile();
		fotoGato.createNewFile();
		
		fotoNavidad001 = new File(pathNavidad.toString()+File.separator+"cenando.png");
		fotoNavidad002 = new File(pathNavidad.toString()+File.separator+"bridando.png");
		fotoNavidad003 = new File(pathNavidad.toString()+File.separator+"festejando.png");
		fotoNavidad004 = new File(pathNavidad.toString()+File.separator+"limpiando.png");
		fotoNavidad001.createNewFile();
		fotoNavidad002.createNewFile();
		fotoNavidad003.createNewFile();
		fotoNavidad004.createNewFile();
		
		fotoVacacion98_01 = new File(pathVacaciones1998.toString()+File.separator+"foto001.jpg");
		fotoVacacion98_02 = new File(pathVacaciones1998.toString()+File.separator+"foto002.jpg");
		fotoVacacion98_03 = new File(pathVacaciones1998.toString()+File.separator+"foto003.jpg");
		fotoVacacion98_01.createNewFile();
		fotoVacacion98_02.createNewFile();
		fotoVacacion98_03.createNewFile();

		fotoVacacion10_01 = new File(pathVacaciones2010.toString()+File.separator+"foto001.JPG");
		fotoVacacion10_02 = new File(pathVacaciones2010.toString()+File.separator+"foto002.jpeg");
		fotoVacacion10_03 = new File(pathVacaciones2010.toString()+File.separator+"foto003.jPEG");
		fotoVacacion10_01.createNewFile();
		fotoVacacion10_02.createNewFile();
		fotoVacacion10_03.createNewFile();
		
		inspector = new FileInspector(pathBase.toString());
	}

	@AfterTest
	public void borrarArchivosTemporales() throws IOException {
		fotoVacacion10_01.delete();
		fotoVacacion10_02.delete();
		fotoVacacion10_03.delete();
		Files.delete(pathVacaciones2010);
		
		fotoVacacion98_01.delete();
		fotoVacacion98_02.delete();
		fotoVacacion98_03.delete();
		Files.delete(pathVacaciones1998);
		
		Files.delete(pathVacaciones);
		
		fotoNavidad001.delete();
		fotoNavidad002.delete();
		fotoNavidad003.delete();
		fotoNavidad004.delete();
		Files.delete(pathNavidad);
		
		fotoYo.delete();
		fotoPerro.delete();
		fotoGato.delete();
		Files.delete(pathBase);
	}

	@Test
	public void armarAlbum() throws IOException {
		File fileAlbum = new File(pathBase.toString()+File.separator+inspector.getDbTextFileName());
		if (fileAlbum.exists()) {
			fileAlbum.delete();
		}
		
		try {
			Album albumBase = inspector.armarAlbum(pathBase.toString());
			Assert.assertNotNull(albumBase);
			Assert.assertNotNull(albumBase.getInfo());
			Assert.assertEquals(albumBase.getImagenes().size(), 3);
			
			//String strJsonOriginal = JsonConverter.buildJson(albumBase).toJSONString();
			//Estos parmetros son dinamicos asi que los ponemos en valores conocidos para comparar
			albumBase.setTitulo("armarAlbumFotoInspectorTest");
			albumBase.setFecha(null);
			albumBase.getInfo().setContentHash("b33aa2440c6e479003cae01f25f14c8014036e67");
			albumBase.getInfo().setFechaActualizacion(null);
			
			String strJson = JsonConverter.buildJson(albumBase).toJSONString();
			Assert.assertEquals(strJson, "{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Fotogato.pNG\",\"fileName\":\"Fotogato.pNG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"FotoPerro.PNG\",\"fileName\":\"FotoPerro.PNG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"fotoYo.png\",\"fileName\":\"fotoYo.png\",\"descripcion\":null}],\"titulo\":\"armarAlbumFotoInspectorTest\",\"fecha\":null,\"descripcion\":null,\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
			
			Assert.assertTrue(fileAlbum.exists());
			Assert.assertTrue(fileAlbum.isFile());
			Assert.assertTrue(fileAlbum.canRead());
		} finally {
			if (fileAlbum.exists())
				fileAlbum.delete();
		}		
	}
	
	@Test(dependsOnMethods={"armarAlbum"})
	public void armarAlbumConActualizacion() throws IOException {
		File fileAlbum = new File(pathBase.toString()+File.separator+inspector.getDbTextFileName());
		if (fileAlbum.exists()) {
			fileAlbum.delete();
		}
		fileAlbum.createNewFile();
		FileOutputStream fos = new FileOutputStream(fileAlbum);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, inspector.getDbFileEncoding()));
		bw.write("{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Gato\",\"fileName\":\"Fotogato.pNG\",\"descripcion\":\"un lindo gatito\"},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Perro\",\"fileName\":\"FotoPerro.PNG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Yo\",\"fileName\":\"fotoYo.png\",\"descripcion\":\"Un foto de perfil\"}],\"titulo\":\"armarAlbumFotoInspectorTest\",\"fecha\":\"2014-04-06T17:39:02.000Z\",\"descripcion\":\"Album simplecon fotos de prueba\",\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
		bw.close();
		long albumFechaModificacion = fileAlbum.lastModified();
		try {
			// se introduce una pausa de un segundo por si el sistema de archivos no 
			// almacena la fechade ultima modificacion con presicion de milisegundos
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		try {
			Album albumBase = inspector.armarAlbum(pathBase.toString());
			Assert.assertNotNull(albumBase);
			Assert.assertNotNull(albumBase.getInfo());
			
			Assert.assertNotEquals(albumBase.getInfo().getContentHash(), "a199cc822ba0d68f787783c2e751a7ce90830249");
			Assert.assertNotEquals(JsonConverter.buildJsonDate(DateTime.class, albumBase.getInfo().getFechaActualizacion()), "2014-04-06T17:39:07.000Z");
			Assert.assertEquals(albumBase.getTitulo(), "armarAlbumFotoInspectorTest");
			Assert.assertEquals(albumBase.getDescripcion(), "Album simplecon fotos de prueba");
			Assert.assertEquals(JsonConverter.buildJsonDate(DateTime.class, albumBase.getFecha()), "2014-04-06T17:39:02.000Z");
			Assert.assertEquals(albumBase.getImagenes().size(), 3);
			
			//String strJsonOriginal = JsonConverter.buildJson(albumBase).toJSONString();
			//Estos parmetros son dinamicos asi que los ponemos en valores conocidos para comparar
			albumBase.getInfo().setContentHash("b33aa2440c6e479003cae01f25f14c8014036e67");
			albumBase.getInfo().setFechaActualizacion(null);
			
			String strJson = JsonConverter.buildJson(albumBase).toJSONString();
			Assert.assertEquals(strJson, "{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Gato\",\"fileName\":\"Fotogato.pNG\",\"descripcion\":\"un lindo gatito\"},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Perro\",\"fileName\":\"FotoPerro.PNG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Yo\",\"fileName\":\"fotoYo.png\",\"descripcion\":\"Un foto de perfil\"}],\"titulo\":\"armarAlbumFotoInspectorTest\",\"fecha\":\"2014-04-06T17:39:02.000Z\",\"descripcion\":\"Album simplecon fotos de prueba\",\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
			
			Assert.assertTrue(fileAlbum.exists());
			Assert.assertTrue(fileAlbum.isFile());
			Assert.assertTrue(fileAlbum.canRead());
			DateTime newTimeStamp = new DateTime(fileAlbum.lastModified());
			Assert.assertTrue(newTimeStamp.isAfter(albumFechaModificacion));
		} finally {
			if (fileAlbum.exists())
				fileAlbum.delete();
		}		
		
	}

	@Test(dependsOnMethods={"armarAlbumConActualizacion"})
	public void armarAlbumConActualizacionDeImagenes() throws IOException {
		File fileAlbum = new File(pathBase.toString()+File.separator+inspector.getDbTextFileName());
		if (fileAlbum.exists()) {
			fileAlbum.delete();
		}
		fileAlbum.createNewFile();
		FileOutputStream fos = new FileOutputStream(fileAlbum);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, inspector.getDbFileEncoding()));
		bw.write("{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Gato\",\"fileName\":\"Fotogato.pNG\",\"descripcion\":\"un lindo gatito\"},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Perro\",\"fileName\":\"FotoPerro.PNG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Yo\",\"fileName\":\"fotoYo.png\",\"descripcion\":\"Un foto de perfil\"}],\"titulo\":\"armarAlbumFotoInspectorTest\",\"fecha\":\"2014-04-06T17:39:02.000Z\",\"descripcion\":\"Album simplecon fotos de prueba\",\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
		bw.close();
		long albumFechaModificacion = fileAlbum.lastModified();
		try {
			// se introduce una pausa de un segundo por si el sistema de archivos no 
			// almacena la fechade ultima modificacion con presicion de milisegundos
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		File fotoDumbo = new File(fotoPerro.getParent()+File.separator+"FotoDumbo.gif");
		fotoPerro.renameTo(fotoDumbo);
		
		try {
			Album albumBase = inspector.armarAlbum(pathBase.toString());
			Assert.assertNotNull(albumBase);
			Assert.assertNotNull(albumBase.getInfo());
			Assert.assertEquals(albumBase.getImagenes().size(), 3);
			
			//String strJsonOriginal = JsonConverter.buildJson(albumBase).toJSONString();
			//Estos parmetros son dinamicos asi que los ponemos en valores conocidos para comparar
			albumBase.getInfo().setContentHash("b33aa2440c6e479003cae01f25f14c8014036e67");
			albumBase.getInfo().setFechaActualizacion(null);
			
			String strJson = JsonConverter.buildJson(albumBase).toJSONString();
			Assert.assertEquals(strJson, "{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"FotoDumbo.gif\",\"fileName\":\"FotoDumbo.gif\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Gato\",\"fileName\":\"Fotogato.pNG\",\"descripcion\":\"un lindo gatito\"},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Yo\",\"fileName\":\"fotoYo.png\",\"descripcion\":\"Un foto de perfil\"}],\"titulo\":\"armarAlbumFotoInspectorTest\",\"fecha\":\"2014-04-06T17:39:02.000Z\",\"descripcion\":\"Album simplecon fotos de prueba\",\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
			
			Assert.assertTrue(fileAlbum.exists());
			Assert.assertTrue(fileAlbum.isFile());
			Assert.assertTrue(fileAlbum.canRead());
			DateTime newTimeStamp = new DateTime(fileAlbum.lastModified());
			Assert.assertTrue(newTimeStamp.isAfter(albumFechaModificacion));
		} finally {
			if (fileAlbum.exists())
				fileAlbum.delete();
			if (fotoDumbo.exists()) {
				fotoDumbo.renameTo(fotoPerro);
			}
		}		
	}
	
	@Test
	public void inspeccionar() {
		File fileAlbum = new File(pathBase.toString()+File.separator+inspector.getDbTextFileName());
		File fileAlbumNavidad = new File(pathNavidad.toString()+File.separator+inspector.getDbTextFileName());
		File fileAlbumVacaciones1998 = new File(pathVacaciones1998.toString()+File.separator+inspector.getDbTextFileName());
		File fileAlbumVacaciones2010 = new File(pathVacaciones2010.toString()+File.separator+inspector.getDbTextFileName());
		if (fileAlbum.exists())
			fileAlbum.delete();
		if (fileAlbumNavidad.exists())
			fileAlbumNavidad.delete();
		if (fileAlbumVacaciones1998.exists())
			fileAlbumVacaciones1998.delete();
		if (fileAlbumVacaciones2010.exists())
			fileAlbumVacaciones2010.delete();
		try {
			List<Album> albumes = inspector.inspeccionar();
			Assert.assertNotNull(albumes);
			Assert.assertEquals(albumes.size(), 4);
			
			boolean albumBaseVerificado = false;
			boolean albumNavidadVerificado = false;
			boolean albumVacaciones1998Verificado = false;
			boolean albumVacaciones2010Verificado = false;
			
			for (Album album : albumes) {
				Assert.assertNotNull(album);
				Assert.assertNotNull(album.getInfo());
				Assert.assertNotNull(album.getImagenes());
				Assert.assertNotEquals(album.getImagenes().size(), 0);
				if (!albumBaseVerificado && album.getFileName().equals(pathBase.getFileName().toString())) {
					albumBaseVerificado = true;
					Assert.assertEquals(album.getImagenes().size(), 3);
					album.setTitulo("armarAlbumFotoInspectorTest");
					album.setFecha(null);
					album.getInfo().setContentHash("b33aa2440c6e479003cae01f25f14c8014036e67");
					album.getInfo().setFechaActualizacion(null);
					String strJson = JsonConverter.buildJson(album).toJSONString();
					Assert.assertEquals(strJson, "{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"Fotogato.pNG\",\"fileName\":\"Fotogato.pNG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"FotoPerro.PNG\",\"fileName\":\"FotoPerro.PNG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"fotoYo.png\",\"fileName\":\"fotoYo.png\",\"descripcion\":null}],\"titulo\":\"armarAlbumFotoInspectorTest\",\"fecha\":null,\"descripcion\":null,\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
					
				} else if (!albumNavidadVerificado && album.getFileName().equals(pathNavidad.getFileName().toString())) {
					albumNavidadVerificado = true;
					Assert.assertEquals(album.getImagenes().size(), 4);
					album.setFecha(null);
					album.getInfo().setContentHash("b33aa2440c6e479003cae01f25f14c8014036e67");
					album.getInfo().setFechaActualizacion(null);
					String strJson = JsonConverter.buildJson(album).toJSONString();
					Assert.assertEquals(strJson, "{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"limpiando.png\",\"fileName\":\"limpiando.png\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"festejando.png\",\"fileName\":\"festejando.png\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"bridando.png\",\"fileName\":\"bridando.png\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"cenando.png\",\"fileName\":\"cenando.png\",\"descripcion\":null}],\"titulo\":\"Navidad\",\"fecha\":null,\"descripcion\":null,\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
					
				} else if (!albumVacaciones1998Verificado && album.getFileName().equals(pathVacaciones1998.getFileName().toString())) {
					albumVacaciones1998Verificado = true;
					Assert.assertEquals(album.getImagenes().size(), 3);
					album.setFecha(null);
					album.getInfo().setContentHash("b33aa2440c6e479003cae01f25f14c8014036e67");
					album.getInfo().setFechaActualizacion(null);
					String strJson = JsonConverter.buildJson(album).toJSONString();
					Assert.assertEquals(strJson, "{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"foto003.jpg\",\"fileName\":\"foto003.jpg\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"foto002.jpg\",\"fileName\":\"foto002.jpg\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"foto001.jpg\",\"fileName\":\"foto001.jpg\",\"descripcion\":null}],\"titulo\":\"1998\",\"fecha\":null,\"descripcion\":null,\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
					
				} else if (!albumVacaciones2010Verificado && album.getFileName().equals(pathVacaciones2010.getFileName().toString())) {
					albumVacaciones2010Verificado = true;
					Assert.assertEquals(album.getImagenes().size(), 3);
					album.setFecha(null);
					album.getInfo().setContentHash("b33aa2440c6e479003cae01f25f14c8014036e67");
					album.getInfo().setFechaActualizacion(null);
					String strJson = JsonConverter.buildJson(album).toJSONString();
					Assert.assertEquals(strJson, "{\"tags\":null,\"imagenes\":[{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"foto003.jPEG\",\"fileName\":\"foto003.jPEG\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"foto002.jpeg\",\"fileName\":\"foto002.jpeg\",\"descripcion\":null},{\"fileNameSmall\":null,\"tags\":null,\"titulo\":\"foto001.JPG\",\"fileName\":\"foto001.JPG\",\"descripcion\":null}],\"titulo\":\"2010\",\"fecha\":null,\"descripcion\":null,\"imagenPortada\":null,\"ubicacion\":null,\"info\":{\"versionRevision\":0,\"versionMenor\":1,\"fechaActualizacion\":null,\"versionMayor\":0,\"contentHash\":\"b33aa2440c6e479003cae01f25f14c8014036e67\"}}");
					
				} else {
					Assert.assertNull(album, "Este album no conicide con ninguno de los esperados");
				}
			}
			
			Assert.assertTrue(albumBaseVerificado);
			Assert.assertTrue(albumNavidadVerificado);
			Assert.assertTrue(albumVacaciones1998Verificado);
			Assert.assertTrue(albumVacaciones2010Verificado);
			
			Assert.assertTrue(fileAlbum.exists());
			Assert.assertTrue(fileAlbum.isFile());
			Assert.assertTrue(fileAlbum.canRead());
			Assert.assertTrue(fileAlbumNavidad.exists());
			Assert.assertTrue(fileAlbumNavidad.isFile());
			Assert.assertTrue(fileAlbumNavidad.canRead());
			Assert.assertTrue(fileAlbumVacaciones1998.exists());
			Assert.assertTrue(fileAlbumVacaciones1998.isFile());
			Assert.assertTrue(fileAlbumVacaciones1998.canRead());
			Assert.assertTrue(fileAlbumVacaciones2010.exists());
			Assert.assertTrue(fileAlbumVacaciones2010.isFile());
			Assert.assertTrue(fileAlbumVacaciones2010.canRead());
		} finally {
			if (fileAlbum.exists())
				fileAlbum.delete();
			if (fileAlbumNavidad.exists())
				fileAlbumNavidad.delete();
			if (fileAlbumVacaciones1998.exists())
				fileAlbumVacaciones1998.delete();
			if (fileAlbumVacaciones2010.exists())
				fileAlbumVacaciones2010.delete();
		}		
	}
}
