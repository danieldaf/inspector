package ar.daf.foto.utilidades;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JsonConverter {
	
	protected static ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		//mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
	}
	
	public static String buildJson(Object obj) throws JsonProcessingException {
		String result = null;
		if (obj != null)
			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		return result;
	}

	public static <T> T buildObject(Class<T> clazz, String json) throws JsonParseException, JsonMappingException, IOException {
		T result = null;
		if (clazz != null && json != null) {
			result = mapper.readValue(json, clazz);
		}
		return result;
	}
}