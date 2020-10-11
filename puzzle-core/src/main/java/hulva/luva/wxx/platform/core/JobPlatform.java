package hulva.luva.wxx.platform.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * 
 * @author Hulva Luva.H
 * @date 2020-10-11 11:15
 * @since 0.0.1
 *
 */
public class JobPlatform implements Closeable{
	
	final Context context;
	
	public JobPlatform(String id, String name, String json) throws PluginException {
		this(id, name, json, null);
	}
	
	public JobPlatform(String id, String name, String json, Map<String, String> context) throws PluginException {
		this.context = new Context(id, name, json, context);
	}
	
	public JobPlatform(String id, String name, PluginConfig root) throws PluginException {
		this(id, name, root, null);
	}
	
	public JobPlatform(String id, String name, PluginConfig root, Map<String, String> context) throws PluginException {
		this.context = new Context(id, name, root, context);
	}
	
	public String getId() {
		return context.getId();
	}
	
	public Context getContext() {
		return context;
	}
	
	public void start() throws Exception {
		BasePluginUtil.execute(context, this.context.getRoot(), null);
	}

	@Override
	public void close() throws IOException {
		this.context.close();
	}
}
