package hulva.luva.wxx.platform.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.ServicePlugin;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * 	插件执行器 
 * @author fy07
 */
public class ServicePlatform implements AutoCloseable{
	
	public static Map<String, ServicePlatform> services = new HashMap<String, ServicePlatform>();
	
	final Context context;
	final ServicePlugin plugin;
	
	private ServicePlatform(String id, String name, String json) throws PluginException {
		this(id, name, json, null);
	}
	
	private ServicePlatform(String id, String name, String json, Map<String, String> context) throws PluginException {
		this.context = new Context(id, name, json, context);
		this.plugin = BasePluginUtil.getPlugin(this.context, this.context.getRoot(), context, ServicePlugin.class);
	}
	
	private ServicePlatform(String id, String name, PluginConfig root) throws PluginException {
		this(id, name, root, null);
	}
	
	private ServicePlatform(String id, String name, PluginConfig root, Map<String, String> context) throws PluginException {
		this.context = new Context(id, name, root, context);
		this.plugin = BasePluginUtil.getPlugin(this.context, this.context.getRoot(), context, ServicePlugin.class);
	}

	public void close() throws IOException {
		this.plugin.close();
		BasePluginUtil.destory(plugin);
		this.context.close();
		services.remove(this.context.getId());
	}
	
	public static boolean start(String id, String name, PluginConfig root, Map<String, String> context) throws PluginException, IOException {
		if(services.containsKey(id)) { return false; }
		ServicePlatform platform = new ServicePlatform(id, name, root, context);
		platform.plugin.execute(null);
		services.put(id, platform);
		platform.plugin.addListener(()->{
			services.remove(id);
		});
		return true;
	}
	
	public static boolean isAlive(String id) throws IOException {
		if(!services.containsKey(id)) { return false; }
		ServicePlatform platform = services.get(id);
		boolean flag = platform.plugin.isAlive();
		if(!flag) { platform.close(); }
		return flag;
	}
	
	public static boolean stop(String id) throws IOException {
		if(!isAlive(id)) { return true; }
		ServicePlatform platform = services.get(id);
		if(platform == null) { return false; }
		platform.close();
		return true;
	}
}
