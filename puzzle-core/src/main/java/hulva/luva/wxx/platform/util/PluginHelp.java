package hulva.luva.wxx.platform.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.annotation.Param;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginHelp {

	public static List<Field> getParamFields(Class<?> clazz) {
		return getAnnotations(clazz, Param.class, true);
	}
	
	public static <T extends Annotation> List<Field> getAnnotations(Class<?> clazz, Class<T> annotationClazz, boolean traceSuperClass) {
		List<Field> fields = new ArrayList<>();
		Class<?> tempClass = clazz;
		while (tempClass != null) {
			fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass();
			if (!traceSuperClass) {
				break;
			}
		}
		List<Field> paramFields = new ArrayList<>();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			T param = field.getAnnotation(annotationClazz);
			if (param != null) {
				paramFields.add(field);
			}
		}
		return paramFields;
	}

	public static <T extends Plugin> Map<String, String> getPluginParams(Class<T> clazz) {
		Map<String, String> paramTypes = new HashMap<String, String>();
		for (Field field : getParamFields(clazz)) {
			Param param = field.getAnnotation(Param.class);
			paramTypes.put(param.value(), field.getType().getSimpleName().toUpperCase());
		}
		return paramTypes;
	}

	public static <T extends Plugin> Map<String, Object> getPluginParamDefaultValues(Class<T> clazz) {
		Map<String, Object> paramTypes = new HashMap<String, Object>();
		for (Field field : getParamFields(clazz)) {
			Param param = field.getAnnotation(Param.class);
			log.debug("{} - {} - {}", field.getName(), param.value(), param.defaultValue());
			Class<?> type = field.getType();
			if (isPrimitive(type)) {
				if (type == boolean.class || type == Boolean.class) {
					paramTypes.put(param.value(), false);
				} else if (type == short.class || type == Short.class || type == int.class || type == Integer.class) {
					paramTypes.put(param.value(), 0);
				} else {
					paramTypes.put(param.value(), param.defaultValue());
				}
			} else {
				paramTypes.put(param.value(), param.defaultValue());
			}
		}
		return paramTypes;
	}

	public static <T extends Plugin> TreeMap<String, String> getPluginParamDefaultValuesAsString(Class<T> clazz) {
		TreeMap<String, String> paramTypes = new TreeMap<String, String>();
		for (Field field : getParamFields(clazz)) {
			Param param = field.getAnnotation(Param.class);
			log.debug("{} - {} - {}", field.getName(), param.value(), param.defaultValue());
			Class<?> type = field.getType();
			if (isPrimitive(type)) {
				if (type == boolean.class || type == Boolean.class) {
					paramTypes.put(param.value(), "false");
				} else if (type == short.class || type == Short.class || type == int.class || type == Integer.class) {
					paramTypes.put(param.value(), "0");
				} else {
					paramTypes.put(param.value(), param.defaultValue());
				}
			} else {
				paramTypes.put(param.value(), param.defaultValue());
			}
		}
		return paramTypes;
	}

	public static boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive() //
				|| clazz == Boolean.class //
				|| clazz == Character.class //
				|| clazz == Byte.class //
				|| clazz == Short.class //
				|| clazz == Integer.class //
				|| clazz == Long.class //
				|| clazz == Float.class //
				|| clazz == Double.class //
				|| clazz == BigInteger.class //
				|| clazz == BigDecimal.class //
				|| clazz == String.class;
	}
}
