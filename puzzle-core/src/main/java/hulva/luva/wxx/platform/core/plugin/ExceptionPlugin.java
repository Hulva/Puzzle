package hulva.luva.wxx.platform.core.plugin;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.exception.PluginException;

/**
 * 异常处理插件
 */
public abstract class ExceptionPlugin extends Plugin{

	public ExceptionPlugin(Context context, PluginConfig config) throws PluginException {
		super(context, config);
	}
}
