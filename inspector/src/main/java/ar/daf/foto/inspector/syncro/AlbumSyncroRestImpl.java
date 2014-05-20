package ar.daf.foto.inspector.syncro;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/syncro")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AlbumSyncroRestImpl {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AlbumSyncro albumSyncro;

	@RequestMapping(method={RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<SyncAlbumInfoDto> obtenerInfoAlbumes() {
		List<SyncAlbumInfoDto> result = albumSyncro.obtenerInfoAlbumes();
		return result;
	}

	@RequestMapping(method={RequestMethod.POST}, value="/albumes",
			produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SyncAlbumDto>> obtenerAlbumes(@RequestBody List<String> hashIdAlbumes) {
		ResponseEntity<List<SyncAlbumDto>> result = null;
		List<SyncAlbumDto> albumes = albumSyncro.obtenerAlbumes(hashIdAlbumes);
		result = new ResponseEntity<List<SyncAlbumDto>>(albumes, HttpStatus.OK);
		return result;
	}

}
