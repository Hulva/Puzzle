package hulva.luva.wxx.platform.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternReplace {

	/**
	 * 正则替换
	 */
	public static String exec(String keyPattern, Map<String, String> map) {
		Set<String> columns = new HashSet<String>();// 获取到所有需要匹配的字段名称
		Pattern pattern = Pattern.compile("\\{\\{.+?\\}\\}");
		Matcher matcher = pattern.matcher(keyPattern);
		while (matcher.find()) {
			columns.add(matcher.group().replace("{{", "").replace("}}", "").trim());
		}
		String str = keyPattern;
		for (String cell : map.keySet()) {
			if (columns.contains(cell)) {
				String value = map.get(cell);
				if (value != null) {
					str = str.replace("{{" + cell + "}}", value);
				}
			}
		}
		columns.clear();
		pattern = Pattern.compile("\\{@\\{.+?\\}}");
		matcher = pattern.matcher(keyPattern);
		while (matcher.find()) {
			columns.add(matcher.group().replace("{@{", "").replace("}}", "").trim());
		}
		for (String cell : map.keySet()) {
			if (columns.contains(cell)) {
				String value = map.get(cell);
				if (value != null) {
					str = str.replace("{@{" + cell + "}}", encoding(value));
				}
			}
		}
		pattern = Pattern.compile("\\#\\{.+?\\}");
		matcher = pattern.matcher(keyPattern);
		while (matcher.find()) {
			columns.add(matcher.group().replace("#{", "").replace("}", "").trim());
		}
		for (String cell : map.keySet()) {
			if (columns.contains(cell)) {
				String value = map.get(cell);
				if (value != null) {
					str = str.replace("#{" + cell + "}", encoding(value));
				}
			}
		}
		return str;
	}
	
	static String encoding(String str) {
		return str.replace("\\", "\\\\").replace("\"", "\\\"");
	}
	
	public static void main(String[] args) {
		String a = "zasdakey{{key}}sdas";
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "1234");
		System.out.println(exec(a, map));
	}
}
