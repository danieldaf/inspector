package ar.daf.foto.utilidades.json;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class JsonConverterTest {
	
	@Test
	public void buildJsonDate() {
		DateTime fechaLocal = new DateTime(1982, 12, 13, 14, 34, 44);
		DateTime fechaUTC = new DateTime(fechaLocal.getMillis(), DateTimeZone.UTC);
		DateTime fechaGMT5 = new DateTime(fechaLocal.getMillis(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT-5")));
		Date fechaL = fechaLocal.toDate();
		
		String strFechaLocal = JsonConverter.buildJsonDate(DateTime.class, fechaLocal);
		String strFechaUTC = JsonConverter.buildJsonDate(DateTime.class, fechaUTC);
		String strFechaGMT5 = JsonConverter.buildJsonDate(DateTime.class, fechaGMT5);
		String strFechaL = JsonConverter.buildJsonDate(Date.class, fechaL);
		String strFechaNula = JsonConverter.buildJsonDate(Date.class, null);
		
		Assert.assertEquals(strFechaLocal, "1982-12-13T17:34:44.000Z");
		Assert.assertEquals(strFechaUTC, "1982-12-13T14:34:44.000Z");
		Assert.assertEquals(strFechaGMT5, "1982-12-13T19:34:44.000Z");
		Assert.assertEquals(strFechaL, "1982-12-13T17:34:44.000Z");
		Assert.assertNull(strFechaNula);
	}
	
	@Test
	public void buildDateFromJson() {
		DateTime fechaUTC = new DateTime(1982, 12, 13, 14, 34, 44, DateTimeZone.UTC);
		String strFechaUTC = "1982-12-13T14:34:44.000Z";
		
		DateTime fechaLocal = new DateTime(fechaUTC.getMillis());
		Date fechaL = fechaLocal.toDate();
		
		DateTime _fechaLocal = JsonConverter.buildDateFromJson(DateTime.class, strFechaUTC);
		Date _fechaL = JsonConverter.buildDateFromJson(Date.class, strFechaUTC);
		Date _fechaNula = JsonConverter.buildDateFromJson(Date.class, null);
		
		Assert.assertEquals(_fechaLocal, fechaLocal);		
		Assert.assertEquals(_fechaL, fechaL);
		Assert.assertNull(_fechaNula);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void buildJson() throws NoSuchFieldException, SecurityException {
		JSONObject jsonTest;
		String strTest;
		
		UbicacionTest u = new UbicacionTest();
		u.setDireccion(null);
		u.setPosicionamiento(true);
		u.setLatitud(13.0);
		u.setLongitud(45.23);
		JSONObject ju = new JSONObject();
		ju.put("direccion", u.getDireccion());
		ju.put("posicionamiento", u.isPosicionamiento());
		ju.put("latitud", u.getLatitud());
		ju.put("longitud", u.getLongitud());
		
		//objeto simple
		jsonTest = JsonConverter.buildJson(u);
		Assert.assertNotNull(jsonTest);
		strTest = jsonTest.toJSONString();
		Assert.assertEquals(strTest, ju.toJSONString());
		
		AlbumTest a = new AlbumTest();
		a.setTitulo("Navidad 1980");
		a.setDescripcion("Una navidad entre amigos");
		a.setTags("navidad,amigos");
		a.setImagenPortada(null);
		a.setPath("/home/user/fotos/navidad");
		a.setFecha(new DateTime(1980, 12, 24, 22, 33));
		a.setFechaActualizacion(new DateTime());
		a.setFilename("1980");
		a.setUbicacion(u);
		a.setContentHash("48e67e47a6f4b4156a428f5bed3e24a6ce93237d");
		a.setImagenes(null);
		JSONObject ja = new JSONObject();
		ja.put("titulo", a.getTitulo());
		ja.put("descripcion", a.getDescripcion());
		ja.put("tags", a.getTags());
		ja.put("imagenPortada", a.getImagenPortada());
		ja.put("fecha", JsonConverter.buildJsonDate(DateTime.class, a.getFecha()));
		ja.put("ubicacion", ju);
		ja.put("imagenes", new JSONArray());
		
		//objeto compuesto por objetos json y no json
		jsonTest = JsonConverter.buildJson(a);
		Assert.assertNotNull(jsonTest);
		strTest = jsonTest.toJSONString();
		Assert.assertEquals(strTest, ja.toJSONString());
				
		ImagenTest img01 = new ImagenTest();
		img01.setTitulo("Brindando!");
		img01.setDescripcion("El primer brindis... fallido.");
		img01.setTags("navidad");
		img01.setFileName("DCIM_001.jpeg");
		img01.setFileNameSmall("thumb_0001.png");
		ImagenTest img02 = new ImagenTest();
		img02.setTitulo("Santa!");
		img02.setDescripcion("La eterna espera a la llegada de Santa");
		img02.setTags("navidad");
		img02.setFileName("DCIM_002.jpeg");
		img02.setFileNameSmall("thumb_0002.png");
		ArrayList<ImagenTest> i = new ArrayList<ImagenTest>();
		i.add(img01);
		i.add(img02);
		a.setImagenes(i);
		JSONArray ji = new JSONArray();
		ji.add(JsonConverter.buildJson(img01));
		ji.add(JsonConverter.buildJson(img02));
		ja.put("imagenes", ji);
		
		//objeto compuesto por arreglos
		jsonTest = JsonConverter.buildJson(a);
		Assert.assertNotNull(jsonTest);
		strTest = jsonTest.toJSONString();
		Assert.assertEquals(strTest, ja.toJSONString());
	}
	
	@Test
	public void buildObject() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		String strJsonUbicacion = "{\"direccion\":null,\"posicionamiento\":true,\"latitud\":13.0,\"longitud\":45.23}";	
		UbicacionTest uResult = JsonConverter.buildObject(UbicacionTest.class, strJsonUbicacion);
		
		Assert.assertNotNull(uResult);
		Assert.assertNull(uResult.getDireccion());
		Assert.assertEquals(uResult.getLatitud(), 13.0);
		Assert.assertEquals(uResult.getLongitud(), 45.23);
		
		String strJsonAlbum = "{\"tags\":\"navidad,amigos\",\"imagenes\":[],\"titulo\":\"Navidad 1980\",\"fecha\":\"1980-12-25T01:33:00.000Z\",\"descripcion\":\"Una navidad entre amigos\",\"imagenPortada\":null,\"ubicacion\":{\"direccion\":null,\"posicionamiento\":true,\"latitud\":13.0,\"longitud\":45.23}}";
		AlbumTest aResult = JsonConverter.buildObject(AlbumTest.class, strJsonAlbum);

		Assert.assertNotNull(aResult);
		Assert.assertEquals(aResult.getTitulo(), "Navidad 1980");
		Assert.assertEquals(aResult.getDescripcion(), "Una navidad entre amigos");
		Assert.assertEquals(aResult.getTags(), "navidad,amigos");
		Assert.assertEquals(aResult.getImagenPortada(), null);
		Assert.assertEquals(aResult.getFecha(), new DateTime(1980, 12, 24, 22, 33));
		Assert.assertNotNull(aResult.getUbicacion());
		Assert.assertNull(aResult.getUbicacion().getDireccion());
		Assert.assertEquals(aResult.getUbicacion().getLatitud(), 13.0);
		Assert.assertEquals(aResult.getUbicacion().getLongitud(), 45.23);
		Assert.assertNotNull(aResult.getImagenes());
		Assert.assertEquals(aResult.getImagenes().size(), 0);
		
		strJsonAlbum = "{\"tags\":\"navidad,amigos\",\"imagenes\":[{\"fileNameSmall\":\"thumb_0001.png\",\"tags\":\"navidad\",\"titulo\":\"Brindando!\",\"fileName\":\"DCIM_001.jpeg\",\"descripcion\":\"El primer brindis... fallido.\"},{\"fileNameSmall\":\"thumb_0002.png\",\"tags\":\"navidad\",\"titulo\":\"Santa!\",\"fileName\":\"DCIM_002.jpeg\",\"descripcion\":\"La eterna espera a la llegada de Santa\"}],\"titulo\":\"Navidad 1980\",\"fecha\":\"1980-12-25T01:33:00.000Z\",\"descripcion\":\"Una navidad entre amigos\",\"imagenPortada\":null,\"ubicacion\":{\"direccion\":null,\"posicionamiento\":true,\"latitud\":13.0,\"longitud\":45.23}}";
		aResult = JsonConverter.buildObject(AlbumTest.class, strJsonAlbum);
		
		Assert.assertNotNull(aResult);
		Assert.assertEquals(aResult.getTitulo(), "Navidad 1980");
		Assert.assertEquals(aResult.getDescripcion(), "Una navidad entre amigos");
		Assert.assertEquals(aResult.getTags(), "navidad,amigos");
		Assert.assertEquals(aResult.getImagenPortada(), null);
		Assert.assertEquals(aResult.getFecha(), new DateTime(1980, 12, 24, 22, 33));
		Assert.assertNotNull(aResult.getUbicacion());
		Assert.assertNull(aResult.getUbicacion().getDireccion());
		Assert.assertEquals(aResult.getUbicacion().getLatitud(), 13.0);
		Assert.assertEquals(aResult.getUbicacion().getLongitud(), 45.23);
		Assert.assertNotNull(aResult.getImagenes());
		Assert.assertEquals(aResult.getImagenes().size(), 2);
		ImagenTest img01 = aResult.getImagenes().get(0);
		ImagenTest img02 = aResult.getImagenes().get(1);
		Assert.assertEquals(img01.getTitulo(), "Brindando!");
		Assert.assertEquals(img01.getDescripcion(), "El primer brindis... fallido.");
		Assert.assertEquals(img01.getTags(), "navidad");
		Assert.assertEquals(img01.getFileName(), "DCIM_001.jpeg");
		Assert.assertEquals(img01.getFileNameSmall(), "thumb_0001.png");
		Assert.assertEquals(img02.getTitulo(), "Santa!");
		Assert.assertEquals(img02.getDescripcion(), "La eterna espera a la llegada de Santa");
		Assert.assertEquals(img02.getTags(), "navidad");
		Assert.assertEquals(img02.getFileName(), "DCIM_002.jpeg");
		Assert.assertEquals(img02.getFileNameSmall(), "thumb_0002.png");
	}
}
