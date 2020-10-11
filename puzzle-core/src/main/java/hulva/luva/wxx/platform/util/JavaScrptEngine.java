package hulva.luva.wxx.platform.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScrptEngine {

	static ScriptEngineManager manager = new ScriptEngineManager();
	static ScriptEngine engine = manager.getEngineByName("nashorn");
	
	public static String run(String script, Map<String, String> params) throws ScriptException {
		Bindings bindings = engine.createBindings();
		if(params != null){
			bindings.putAll(params);
		}
		return engine.eval(replaceLine(script), bindings).toString();
	}
	
	static String replaceLine(String str){
		Pattern p = Pattern.compile("(?s)`(.*?)`");
		Matcher m = p.matcher(str);
		while(m.find()) {
			String group = m.group();
			String group1 = group.replace("\r", "").replace("\n", "").replace("'", "\\'").replace("`", "'");
			str = str.replace(group, group1);
		}
		return str;
	}
}
