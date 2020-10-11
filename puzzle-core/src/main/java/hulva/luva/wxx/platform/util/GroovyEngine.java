package hulva.luva.wxx.platform.util;

import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyEngine {
	
	public static String run(String script, Map<String, ?> params) {
		Binding binding = new Binding();
		if(params != null){
			binding = new Binding(params);
		}
        GroovyShell shell = new GroovyShell(binding);
        Object result = shell.parse(script).run();
        if(result == null){ return null; }
        return result.toString();
	}
}
