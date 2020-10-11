package hulva.luva.wxx.platform.core.plugin;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.aop.ExecutePoint;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * @author Hulva Luva.H
 * 
 *         由 condition 的值来确定具体的执行插件, condition 与子插件名之间的匹配不分大小写
 *
 */
@PluginMetaData(name = "ConditionPlugin", type = PluginType.OTHER, version = 0)
public class ConditionPlugin extends FlowPlugin {

	@Param(value = "CONDITION", label = "Condition", placeholder = "If condition not matched, first one will be executed.")
	private String condition;

	public ConditionPlugin(Context context, PluginConfig config) throws PluginException {
		super(context, config);
	}

	@Override
	public Map<String, String> start(Map<String, String> request) throws PluginException {
		if (this.plugins == null || this.plugins.size() == 0) {
			throw new PluginException("No child plugin!");
		}
		Map<String, PluginConfig> namePluginConfigMap = this.plugins.stream().collect(Collectors.toMap(pluginConfig -> pluginConfig.getName().toLowerCase(), Function.identity()));
		if (!namePluginConfigMap.containsKey(condition.toLowerCase())) {
			ExecutePoint.onWarning(key(), getContext(), getConfig(),  String.format("No matched condition [%s] in conditions {%s}, will execute the default [%s].", condition, namePluginConfigMap.keySet(), this.plugins.get(0)), null);
		}
		request = BasePluginUtil.execute(getContext(), namePluginConfigMap.getOrDefault(condition, this.plugins.get(0)), request);
		return request;
	}
}
