package hulva.luva.wxx.platform.core.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;

/**
 * 调用系统脚本的插件
 */
@PluginMetaData(name = "ShellPlugin", type = PluginType.OTHER, version = 0)
public class ShellPlugin extends Plugin {

	@Param(value = "COMMOND", label = "Command line", format=FieldFormat.shell, sort = 20)
	String commond;

	public ShellPlugin(Context context, PluginConfig config) throws PluginException {
		super(context, config);
	}

	@Override
	public Map<String, String> start(Map<String, String> request) throws PluginException {
		Process process = null;
		String resultLog = "";
		try {
			process = Runtime.getRuntime().exec(commond);
			try (BufferedInputStream bis = new BufferedInputStream(process.getInputStream())) {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(bis))) {
					String line = null;
					while ((line = br.readLine()) != null) {
						resultLog += line + "\n";
					}
				}
			}
			process.waitFor();
		} catch (Exception e) {
			throw new PluginException("commond error", e);
		}
		int exitCode = process.exitValue();
		if (exitCode != 0) {
			throw new PluginException("commond exit by " + exitCode);
		}
		request.put("DATA", resultLog);
        return request;
	}

}
