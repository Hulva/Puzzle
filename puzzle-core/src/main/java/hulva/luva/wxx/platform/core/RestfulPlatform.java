package hulva.luva.wxx.platform.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

import hulva.luva.wxx.platform.core.aop.ExecutePoint;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.RestPlugin;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Hulva Luva.H
 * @date 2020-10-11 11:15
 * @since 0.0.1
 *
 */
public class RestfulPlatform implements AutoCloseable{
	
	public static Map<String, RestfulPlatform> servicesByID = new HashMap<String, RestfulPlatform>();
	public static Map<String, RestfulPlatform> servicesByName = new HashMap<String, RestfulPlatform>();
	
	final Context context;
	final RestPlugin plugin;
	
	private RestfulPlatform(String id, String name, String json) throws PluginException {
		this(id, name, json, null);
	}
	
	private RestfulPlatform(String id, String name, String json, Map<String, String> context) throws PluginException {
		this.context = new Context(id, name, json, context);
		this.plugin = BasePluginUtil.getPlugin(this.context, this.context.getRoot(), context, RestPlugin.class);
	}
	
	private RestfulPlatform(String id, String name, PluginConfig root) throws PluginException {
		this(id, name, root, null);
	}
	
	private RestfulPlatform(String id, String name, PluginConfig root, Map<String, String> context) throws PluginException {
		this.context = new Context(id, name, root, context);
		this.plugin = BasePluginUtil.getPlugin(this.context, this.context.getRoot(), context, RestPlugin.class);
	}

	public void close() throws IOException {
		servicesByID.remove(this.context.getId());
		servicesByName.remove(this.context.getName());
		this.plugin.release();
		BasePluginUtil.destory(plugin);
		this.context.close();
	}
	
	public static Map<String, String> onReciveData(String id, HttpServletRequest request, HttpServletResponse response, JSONObject jsonParam) throws PluginException {
		RestfulPlatform platform = servicesByID.get(id);
		if(platform == null) { throw new RuntimeException("can not find job by id:" + id); }
		return onReciveData(platform, request, response, jsonParam);
	}
	
	public static Map<String, String> onReciveDataByName(String name, HttpServletRequest request,HttpServletResponse response, JSONObject jsonParam) throws PluginException {
		RestfulPlatform platform = servicesByName.get(name);
		if(platform == null) { throw new RuntimeException("can not find job by name:" + name); }
		return onReciveData(platform, request, response, jsonParam);
	}
	
	private static Map<String, String> onReciveData(RestfulPlatform platform, HttpServletRequest request,HttpServletResponse response, JSONObject jsonParam) throws PluginException {
		try {
			return platform.plugin.onRequest(request, response, jsonParam);
		} catch (Exception e) {
			ExecutePoint.onExecuteException(platform.plugin.context, platform.plugin.config, "Execute REST failed!", e);
			throw e;
		}
	}
	
	public static boolean start(String id, String name, PluginConfig root, Map<String, String> context) throws PluginException, IOException {
		RestfulPlatform platform = new RestfulPlatform(id, name, root, context);
		platform.plugin.execute(null);
		servicesByID.put(id, platform);
		servicesByName.put(name, platform);
		return true;
	}
	
	public static boolean isAlive(String id) throws IOException {
		return servicesByID.containsKey(id);
	}
	
	public static boolean stop(String id) throws IOException {
		RestfulPlatform platform = servicesByID.get(id);
		servicesByID.remove(id);
		if(platform == null) { return false; }
		platform.close();
		return true;
	}
}
