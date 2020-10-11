package hulva.luva.wxx.platform.core;

import java.util.HashMap;

import javax.script.ScriptException;

import hulva.luva.wxx.platform.util.GroovyEngine;

public class GroovyTest {

	public static void main(String[] args) throws ScriptException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("arg1", "12121");
		params.put("arg2", "阿百川");
		Object result = GroovyEngine.run("println 'hello world, you send code:' + arg1; return 'test is return ' + arg2; ", params);
		System.out.println("result:" + result);
	}
}
