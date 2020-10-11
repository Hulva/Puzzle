package hulva.luva.wxx.platform.core.plugin;

import java.util.List;
import java.util.Map;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;
import hulva.luva.wxx.platform.util.JavaScrptEngine;

/**
 * 选择器插件
 */
@PluginMetaData(name = "ChoosePlugin", type = PluginType.OTHER, version = 0)
public class ChoosePlugin extends Plugin{

	@Param(value = "SCRIPT", label = "Assert Script", format=FieldFormat.javascript, sort = 20)
	String script;
	
	protected List<PluginConfig> pluginsTrue;
	@Param(value = "True", required=true, label = "True", format=FieldFormat.plugins, sort = 999)
	String pluginsTrueJson;
	
	protected List<PluginConfig> pluginsFalse;
	@Param(value = "False", required=true, label = "False", format=FieldFormat.plugins, sort = 999)
	String pluginsFalseJson;
	
	public ChoosePlugin(Context context, PluginConfig config) throws PluginException {
		super(context, config);
		this.pluginsTrue = BasePluginUtil.parsePluginConfig(pluginsTrueJson);
		this.pluginsFalse = BasePluginUtil.parsePluginConfig(pluginsFalseJson);
	}

	@Override
	public Map<String, String> start(Map<String, String> request) throws PluginException {
		try {
			String condition = JavaScrptEngine.run(script, context.merge(request));
			if (condition == null || "false".equals(condition.toLowerCase()) || "0".equals(condition.toLowerCase())) {
				for (PluginConfig c : pluginsFalse) {
					request = BasePluginUtil.execute(getContext(), c, request);
				}
			}else {
				for (PluginConfig c : pluginsTrue) {
					request = BasePluginUtil.execute(getContext(), c, request);
				}
			}
			return request;
		} catch (Exception e) {
			throw new PluginException("javascript run error", e);
		}
	}

}
