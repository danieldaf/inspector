package ar.daf.foto.inspector.consulta;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
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
public class AlbumConsultasRestImpl {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AlbumConsultas albumConsultas;
	
	@RequestMapping(method={RequestMethod.GET})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public QServerInfoDto obtenerMetadatosServer() {
		QServerInfoDto result = albumConsultas.obtenerMetadatosServer();
		result.add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerMetadatosServer()).withSelfRel());
		result.add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerAlbumes()).withRel("albumes"));
		return result;
	}

	@RequestMapping(method={RequestMethod.GET}, value="/album")
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<QAlbumInfoDto> obtenerAlbumes() {
		List<QAlbumInfoDto> result = albumConsultas.obtenerAlbumes();
		if (result != null && !result.isEmpty()) {
			for (QAlbumInfoDto album : result) {
				String imgFileName = null;
				String imgFileExt = null;
				if (album.getFileNameImagenPortada() != null) {
					imgFileName = album.getFileNameImagenPortada();
					int pos = imgFileName.lastIndexOf('.');
					if (pos >=0 ) {
						imgFileExt = imgFileName.substring(pos+1);
						imgFileName = imgFileName.substring(0, pos);
					} else {
						imgFileExt = "";
					}
				}						
				album.add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerAlbum(album.getHashId())).withSelfRel());
				album.add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerImagen(album.getHashId(), imgFileName, imgFileExt) ).withRel("imagenPortada"));
				album.add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerImagenMiniatura(album.getHashId(), imgFileName, imgFileExt) ).withRel("miniaturaPortada"));
			}
		}
		return result;
	}

	@RequestMapping(method={RequestMethod.GET}, value="/album/{hashId}")
	public ResponseEntity<QAlbumCompletoDto> obtenerAlbum(@PathVariable("hashId")String hashId) {
		ResponseEntity<QAlbumCompletoDto> result = null;
		QAlbumCompletoDto resultDto = null;
		resultDto = albumConsultas.obtenerAlbum(hashId);
		if (resultDto != null) {
			String imgFileName = null;
			String imgFileExt = null;
			if (resultDto.getInfo().getFileNameImagenPortada() != null) {
				imgFileName = resultDto.getInfo().getFileNameImagenPortada();
				int pos = imgFileName.lastIndexOf('.');
				if (pos >=0 ) {
					imgFileExt = imgFileName.substring(pos+1);
					imgFileName = imgFileName.substring(0, pos);
				} else {
					imgFileExt = "";
				}
			}						
			resultDto.getInfo().add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerAlbum(resultDto.getInfo().getHashId())).withSelfRel());
			resultDto.getInfo().add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerImagen(resultDto.getInfo().getHashId(), imgFileName, imgFileExt) ).withRel("imagenPortada"));
			resultDto.getInfo().add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerImagenMiniatura(resultDto.getInfo().getHashId(), imgFileName, imgFileExt) ).withRel("miniaturaPortada"));

			if (resultDto.getImagenes() != null && !resultDto.getImagenes().isEmpty()) {
				for (QImagenDto imagen : resultDto.getImagenes()) {
					imgFileName = imagen.getFileName();
					imgFileExt = "";
					int pos = imgFileName.lastIndexOf('.');
					if (pos >=0 ) {
						imgFileExt = imgFileName.substring(pos+1);
						imgFileName = imgFileName.substring(0, pos);
					}
					imagen.add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerImagen(resultDto.getInfo().getHashId(), imgFileName, imgFileExt)).withRel("imagen"));
					imagen.add(linkTo(methodOn(AlbumConsultasRestImpl.class).obtenerImagenMiniatura(resultDto.getInfo().getHashId(), imgFileName, imgFileExt)).withRel("miniatura"));
				}
			}
			
			result = new ResponseEntity<QAlbumCompletoDto>(resultDto, HttpStatus.OK);
		} else {
			result = new ResponseEntity<QAlbumCompletoDto>(HttpStatus.NOT_FOUND);
		}
		return result;
	}
	
	@RequestMapping(method={RequestMethod.GET}, value="/album/{hashId}/{fileNameImagen}.{fileExt}", produces="image/*")
	public ResponseEntity<byte[]> obtenerImagen(@PathVariable("hashId")String hashId, @PathVariable("fileNameImagen")String fileNameImagen, @PathVariable("fileExt")String fileExt) {
		ResponseEntity<byte[]> result = null;
		try {
			fileNameImagen=URLDecoder.decode(fileNameImagen, "utf-8");
			byte[] bytes = albumConsultas.obtenerImagen(hashId, fileNameImagen+"."+fileExt);
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
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	@RequestMapping(method={RequestMethod.GET}, value="/album/{hashId}/miniatura/{fileNameImagen}.{fileExt}", produces="image/*")
	public ResponseEntity<byte[]> obtenerImagenMiniatura(@PathVariable("hashId")String hashId, @PathVariable("fileNameImagen")String fileNameImagen, @PathVariable("fileExt")String fileExt) {
		ResponseEntity<byte[]> result = null;
		try {
			fileNameImagen=URLDecoder.decode(fileNameImagen, "utf-8");
			byte[] bytes = albumConsultas.obtenerImagen(hashId, fileNameImagen+"."+fileExt);
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
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}
}