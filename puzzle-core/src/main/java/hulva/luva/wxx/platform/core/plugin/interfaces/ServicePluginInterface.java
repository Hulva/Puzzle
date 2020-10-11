package hulva.luva.wxx.platform.core.plugin.interfaces;

import java.util.Map;

import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.ServicePlugin.ServiceListener;

public interface ServicePluginInterface extends PluginInterface{

	void thread(Consumer<Map<String, String>> consumer) throws PluginException;
	boolean isAlive();
	public void close();
	
	void addListener(ServiceListener listener);
	void removeListener(ServiceListener listener);
}
