package hulva.luva.wxx.platform.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class ObjectUtil {
	
	public static void set(Object obj, Field field, String value) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		Class<?> fieldClazz = field.getType();
		if (isPrimitive(fieldClazz)){//类型不用转换
			if(fieldClazz == boolean.class || fieldClazz == Boolean.class){
				field.set(obj, Boolean.valueOf(value));
			}else if(fieldClazz == short.class || fieldClazz == Short.class){
				field.set(obj, Short.valueOf(value));
			}else if(fieldClazz == int.class || fieldClazz == Integer.class){
				field.set(obj, Integer.valueOf(value));
			}else if(fieldClazz == long.class || fieldClazz == Long.class){
				field.set(obj, Long.valueOf(value));
			}else if(fieldClazz == double.class || fieldClazz == Double.class){
				field.set(obj, Double.valueOf(value));
			}else if(fieldClazz == float.class || fieldClazz == Float.class){
				field.set(obj, Float.valueOf(value));
			}else if(fieldClazz == BigInteger.class){
				field.set(obj, new BigInteger(value));
			}else if(fieldClazz == BigDecimal.class){
				field.set(obj, new BigDecimal(value));
			}else if(fieldClazz == byte.class || fieldClazz == Byte.class){
				field.set(obj, Byte.valueOf(value));
			}else if(fieldClazz == byte[].class || fieldClazz == Byte[].class){
				field.set(obj, value.getBytes());
			}else if(fieldClazz == char.class || fieldClazz == Character.class){
				field.set(obj, value.charAt(0));
			}else{
				field.set(obj, value);
			}
        }else if(fieldClazz == java.util.Date.class){//时间类型
        	long time = Long.valueOf(value);
        	field.set(obj, new java.util.Date(time));
        }else if(fieldClazz == java.sql.Date.class){
        	long time = Long.valueOf(value);
        	field.set(obj, new java.sql.Date(time));
        }else if(fieldClazz.isAssignableFrom(List.class)){
            List<?> list = JSONArray.parseArray(value, fieldClazz);
            field.set(obj, list);
        }else if(fieldClazz.isAssignableFrom(Map.class)){
        	Map<?,?> tmp = JSON.parseObject(value, Map.class);
        	field.set(obj, tmp);
        }else{
        	field.set(obj, JSON.parseObject(value, fieldClazz));
        }
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> deseriallze(byte[] bs) throws IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bs));
		return (Map<String, Object>) ois.readObject();
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
