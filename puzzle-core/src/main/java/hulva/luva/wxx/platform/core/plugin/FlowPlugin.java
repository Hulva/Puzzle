package hulva.luva.wxx.platform.core.plugin;

import java.util.List;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * 流程控制插件
 */
public abstract class FlowPlugin extends Plugin {

	protected List<PluginConfig> plugins;

	@Param(value = "plugins", required = true, label = "Plugins", format=FieldFormat.plugins, sort = 1)
	String pluginsJson;

	public FlowPlugin(Context context, PluginConfig config) throws PluginException {
		super(context, config);
		
	}
	
	@Override
	public void init() throws PluginException {
		this.plugins = BasePluginUtil.parsePluginConfig(pluginsJson);
		super.init();
	}
}
