package ar.daf.foto.inspector.rest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//@Controller
//@RequestMapping("/album")
public class AlbumRestCtrl {
		
	@RequestMapping(method={RequestMethod.GET}, value="/{param}", produces={"text/plain"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public String metodo(@PathVariable(value="param") String param) {
		if (param == null || param.trim().isEmpty())
			param = "desconocido";
		return "Hola "+param+"!";
	}

}
