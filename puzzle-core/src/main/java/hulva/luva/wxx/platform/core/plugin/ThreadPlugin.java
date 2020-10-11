 package hulva.luva.wxx.platform.core.plugin;

import java.util.Map;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * 单一步骤执行插件
 */
@PluginMetaData(name = "ThreadPlugin", type = PluginType.OTHER, version = 0)
public class ThreadPlugin extends FlowPlugin{
	
	public ThreadPlugin(Context context, PluginConfig config) throws PluginException {
		super(context, config);
	}

	@Override
	public Map<String, String> start(Map<String, String> request) throws PluginException {
		for (PluginConfig c : plugins) {
			request = BasePluginUtil.execute(context, c, request);
		}
		return request;
	}
}
