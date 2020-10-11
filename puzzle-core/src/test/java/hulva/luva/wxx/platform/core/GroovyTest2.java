package hulva.luva.wxx.platform.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import javax.script.ScriptException;
import org.apache.commons.io.FileUtils;

import hulva.luva.wxx.platform.util.GroovyEngine;

public class GroovyTest2 {

	public static void main(String[] args) throws ScriptException, IOException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("arg1", "12121");
		params.put("arg2", "阿百川");
		File file = new File(System.getProperty("user.dir") + "/scripts/groovy/test1.groovy");
		String script = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
		Object result = GroovyEngine.run(script, params);
		System.out.println("result:" + result);
	}
}
