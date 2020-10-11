package hulva.luva.wxx.platform.core;

import java.util.HashMap;
import javax.script.ScriptException;

import hulva.luva.wxx.platform.util.JavaScrptEngine;

public class JavaScriptTest {

	public static void main(String[] args) throws ScriptException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("arg1", "12121");
		params.put("arg2", "阿百川");
		String result = JavaScrptEngine.run("print('hello world, you send code:' + arg1); 'test is return ' + arg2; ", params);
		System.out.println("result:" + result);
	}
}
