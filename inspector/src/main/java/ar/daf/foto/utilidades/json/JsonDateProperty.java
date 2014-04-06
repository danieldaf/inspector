package ar.daf.foto.utilidades.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface JsonDateProperty {
	String name() default "";
	Class<?> type() default String.class;
}
