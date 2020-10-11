package hulva.luva.wxx.platform.core.plugin.interfaces;

import java.util.Map;

import hulva.luva.wxx.platform.core.exception.PluginException;

public interface PluginInterface {
	public String key();
	public void initParam(Map<String, String> request) throws PluginException;
	public Map<String, String> execute(Map<String, String> request) throws PluginException;
	public void release();
}
