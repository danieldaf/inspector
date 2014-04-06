package ar.daf.foto.utilidades.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JsonConverter {
	
	protected static SimpleDateFormat jsonDatefmt = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	@SuppressWarnings("unchecked")
	protected static Object buildJsonFromField(Field field, Object fieldObject) {
		Object result = null;
		Class<?> fclazz = field.getType();
		try {
			if (fclazz.isAnnotationPresent(JsonClass.class)) {
				result = buildJson(fieldObject);
			} else if (fclazz.isArray() || Collection.class.isAssignableFrom(fclazz)) {
				JSONArray jsonObj = new JSONArray();
				if (fieldObject != null) {
					if (fclazz.isArray()) {
						Array array = (Array)fieldObject;
						for (int pos=0; pos<Array.getLength(array); pos++) {
							Object item = Array.get(array, pos);
							JSONObject jsonItem = buildJson(item);
							jsonObj.add(jsonItem);
						}
					} else {
						Collection<Object> coll =  (Collection<Object>)fieldObject;
						for (Object item : coll) {
							JSONObject jsonItem = buildJson(item);
							jsonObj.add(jsonItem);
						}
					}
				}
				result = jsonObj;
			} else {
				result = fieldObject;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject buildJson(Object obj) {
		JSONObject result = null;
		if (obj != null && obj.getClass().isAnnotationPresent(JsonClass.class)) {
			JSONObject jsonObj = new JSONObject();
			
			Field fields[] = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotations().length > 0) {
					String jsonPropName = field.getName();
	
					Object fieldObject = null;
					String methodName = field.getName();
					Method method = null;
					try {
						String getter = "get"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1);
						method = obj.getClass().getMethod(getter);
					} catch (NoSuchMethodException e) {
					} catch (SecurityException e) {
					}
					if (method == null) {
						String getter = "is"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1);
						try {
							method = obj.getClass().getMethod(getter);
						} catch (NoSuchMethodException e) {
						} catch (SecurityException e) {
						}
					}
					if (method != null) {
						try {
							fieldObject = method.invoke(obj);
						} catch (IllegalAccessException e) {
						} catch (IllegalArgumentException e) {
						} catch (InvocationTargetException e) {
						}
					}
	
					if (field.isAnnotationPresent(JsonProperty.class)) {
						JsonProperty annotation = field.getAnnotation(JsonProperty.class);
						if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
							jsonPropName = annotation.name().trim();
						}
						Object jsonPropValue = buildJsonFromField(field, fieldObject);
						jsonObj.put(jsonPropName, jsonPropValue);
					} else if (field.isAnnotationPresent(JsonDateProperty.class)) {
						String jsonPropValue = buildJsonDate(field.getType(), fieldObject);
						JsonDateProperty annotation = field.getAnnotation(JsonDateProperty.class);
						if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
							jsonPropName = annotation.name().trim();
						}
						jsonObj.put(jsonPropName, jsonPropValue);
					}
				}
			}
			result = jsonObj;
		}
		return result;
	}
	
	public static String buildJsonDate(Class<?> dateClass, Object fieldObject) {
		String result = null;	
		if (fieldObject != null) {
			if (Date.class.isAssignableFrom(dateClass)) {
				Date date = (Date)fieldObject;
				DateTimeZone timeZone = DateTimeZone.getDefault();
				result = jsonDatefmt.format(timeZone.convertLocalToUTC(date.getTime(), true));
			} else if (DateTime.class.isAssignableFrom(dateClass)) {
				DateTime date = (DateTime)fieldObject;
				DateTimeZone timeZone = date.getZone();
				result = jsonDatefmt.format(timeZone.convertLocalToUTC(date.getMillis(), true));
			} 
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T buildDateFromJson(Class<T> dateClass, String jsonDate) {
		T result = null;
		if (jsonDate != null && !jsonDate.trim().isEmpty()) {
			try {
				Date dateUTCOnLocalZone = jsonDatefmt.parse(jsonDate);
				DateTimeZone timeZone = DateTimeZone.getDefault();
				DateTime dateTimeUTC  = new DateTime(timeZone.convertUTCToLocal(dateUTCOnLocalZone.getTime()), DateTimeZone.UTC);

				if (Date.class.isAssignableFrom(dateClass)) {
					Date dateOk = new Date(dateTimeUTC.getMillis());
					result = (T)dateOk;
				} else if (DateTime.class.isAssignableFrom(dateClass)) {
					DateTime dateTimeOk = new DateTime(dateTimeUTC.getMillis());
					result = (T)dateTimeOk;
				}
			} catch (ParseException e) {
				//throw
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected static Object buildValueForField(Field field, Object jsonObject) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		Object result = null;
		Class<?> fclazz = field.getType();
		try {
			if (fclazz.isAnnotationPresent(JsonClass.class)) {
				if (jsonObject != null)
					result = buildObject(fclazz, jsonObject.toString());
			} else if (fclazz.isArray() || Collection.class.isAssignableFrom(fclazz)) {				
				Class<?> itemClass = Object.class;

				JSONArray jsonArray = (JSONArray)jsonObject;
				if (jsonArray != null) {
					if (fclazz.isArray()) {
						itemClass = fclazz.getComponentType();
						Object array = Array.newInstance(itemClass, jsonArray.size());
						for (int pos=0; pos<jsonArray.size(); pos++) {
							Object jsonItemObject = jsonArray.get(pos);
							Object itemObject = buildObject(itemClass, jsonItemObject.toString());
							Array.set(array, pos, itemObject);
						}
						result = array;
					} else {
						Type parametrizedTypes[] = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
						if (parametrizedTypes != null && parametrizedTypes.length > 0) {
							Type type = parametrizedTypes[0];
							if (type instanceof Class)
								itemClass = (Class<?>)type;
						}

						Object array = Array.newInstance(itemClass, jsonArray.size());
						for (int pos=0; pos<jsonArray.size(); pos++) {
							Object jsonItemObject = jsonArray.get(pos);
							Object itemObject = buildObject(itemClass, jsonItemObject.toString());
							Array.set(array, pos, itemObject);
						}
						
						if (List.class.isAssignableFrom(fclazz)) {
							@SuppressWarnings("rawtypes")
							ArrayList arrayList = new ArrayList();
							for (int pos=0; pos<Array.getLength(array); pos++) {
								arrayList.add(Array.get(array, pos));
							}
							result = arrayList;
						}
					}
				}				
			} else {
				result = jsonObject;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static <T> T buildObject(Class<T> clazz, String json) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		T result = null;
		Object object = JSONValue.parse(json);
		if (object != null && JSONObject.class.isAssignableFrom(object.getClass())) { 
			JSONObject jsonObject = (JSONObject)object;
			result = clazz.newInstance();
			
			Field fields[] = clazz.getDeclaredFields();
			for (Field field : fields) {
				String propName = field.getName();
				String jsonPropName = propName;
				
				String methodName = field.getName();
				Method method = null;
				try {
					String setter = "set"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1);
					method = clazz.getMethod(setter, field.getType());
				} catch (NoSuchMethodException e) {
				} catch (SecurityException e) {
				}

				if (method != null) {
					if (field.isAnnotationPresent(JsonProperty.class)) {
						JsonProperty annotation = field.getAnnotation(JsonProperty.class);
						if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
							jsonPropName = annotation.name().trim();
						}
						Object jsonPropValue = jsonObject.get(jsonPropName);
						Object propValue = buildValueForField(field, jsonPropValue);
						if (propValue != null && (propValue instanceof Long || propValue instanceof Double)) {
							Object i = null;
							if (field.getType().isPrimitive()) {
								if ("byte".equals(field.getType().getName())) {
									i = Byte.MIN_VALUE;
								} else if ("short".equals(field.getType().getName())) {
									i = Short.MIN_VALUE;
								} else if ("int".equals(field.getType().getName())) {
									i = Integer.MIN_VALUE;
								} else if ("float".equals(field.getType().getName())) {
									i = Float.MIN_VALUE;
								} else if ("double".equals(field.getType().getName())) {
									i = Double.MIN_VALUE;
								}
							} else {
								i = field.getType().newInstance();
							}
							if (propValue instanceof Long) {
								if (i instanceof Integer) {
									i = ((Long) propValue).intValue();
									propValue = i;
								} else if (i instanceof Short) {
									i = ((Long) propValue).shortValue();
									propValue = i;
								} else if (i instanceof Byte) {
									i = ((Long) propValue).byteValue();
									propValue = i;
								} else if (i instanceof Float) {
									i = ((Long) propValue).floatValue();
									propValue = i;
								} else if (i instanceof Double) {
									i = ((Long) propValue).doubleValue();
									propValue = i;
								}

							} else if (propValue instanceof Double) {
								if (i instanceof Float) {
									i = ((Double) propValue).floatValue();
									propValue = i;
								}
							}
						}
						method.invoke(result, propValue);
					} else if (field.isAnnotationPresent(JsonDateProperty.class)) {
						JsonDateProperty annotation = field.getAnnotation(JsonDateProperty.class);
						if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
							jsonPropName = annotation.name().trim();
						}
						Object jsonPropValue = jsonObject.get(jsonPropName);
						Object datePropValue = null;
						if (jsonPropValue != null)
							datePropValue = buildDateFromJson(field.getType(), jsonPropValue.toString());
						method.invoke(result, datePropValue);
					}
				}
			}
		}
		return result;
	}
}