package ar.daf.foto.inspector.consulta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
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

}