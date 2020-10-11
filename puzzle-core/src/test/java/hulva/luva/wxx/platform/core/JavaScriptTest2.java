package hulva.luva.wxx.platform.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import javax.script.ScriptException;
import org.apache.commons.io.FileUtils;

import hulva.luva.wxx.platform.util.JavaScrptEngine;

public class JavaScriptTest2 {

	public static void main(String[] args) throws ScriptException, IOException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("arg1", "12121");
		params.put("arg2", "阿百川");
		File file = new File(System.getProperty("user.dir") + "/scripts/javascript/test1.js");
		String script = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
		String result = JavaScrptEngine.run(script, params);
		System.out.println("result:" + result);
	}
}
