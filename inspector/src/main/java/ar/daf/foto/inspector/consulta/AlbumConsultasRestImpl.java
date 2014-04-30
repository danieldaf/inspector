package ar.daf.foto.inspector.consulta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/consultas")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AlbumConsultasRestImpl implements AlbumConsultasRest {
	
	@Autowired
	private AlbumConsultas albumConsultas;
	
	@RequestMapping(method={RequestMethod.GET})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	@Override
	public QServerInfoDto obtenerMetadatosServer() {
		return albumConsultas.obtenerMetadatosServer();
	}

	@RequestMapping(method={RequestMethod.GET}, value="/album")
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	@Override
	public List<QAlbumInfoDto> obtenerAlbumes() {
		return albumConsultas.obtenerAlbumes();
	}

	@RequestMapping(method={RequestMethod.GET}, value="/album/{path}/{fileName}")
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	@Override
	public QAlbumCompletoDto obtenerAlbum(@PathVariable("path")String path, @PathVariable("fileName")String fileName) {
		return albumConsultas.obtenerAlbum(path, fileName);
	}
	
	@RequestMapping(method={RequestMethod.GET}, value="/album/{path}/{fileNameAlbum}/{fileNameImagen}.{fileExt}", produces="image/*")
	@Override
	public ResponseEntity<byte[]> obtenerImagen(@PathVariable("path")String path, @PathVariable("fileNameAlbum")String fileNameAlbum, @PathVariable("fileNameImagen")String fileNameImagen, @PathVariable("fileExt")String fileExt) {
		ResponseEntity<byte[]> result = null;
		byte[] bytes = albumConsultas.obtenerImagen(path, fileNameAlbum, fileNameImagen+"."+fileExt);
		if (bytes != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentLength(bytes.length);
			if ("GIF".equalsIgnoreCase(fileExt))
				headers.setContentType(MediaType.IMAGE_GIF);
			else if ("PNG".equalsIgnoreCase(fileExt))
				headers.setContentType(MediaType.IMAGE_PNG);
			else if ("JPG".equalsIgnoreCase(fileExt) || "JPEG".equalsIgnoreCase(fileExt))
				headers.setContentType(MediaType.IMAGE_JPEG);
			result = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
		} else {
			result = new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}
		return result;
	}

}