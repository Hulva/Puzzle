package hulva.luva.wxx.platform.core.plugin;

import java.util.Map;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;

/**
 * @author Hulva Luva.H
 * 
 *         由 condition 的值来确定具体的执行插件, condition 与子插件名之间的匹配不分大小写
 *
 */
@PluginMetaData(name = "ConditionPluginWithGroovy", type = PluginType.OTHER, version = 0)
public class ConditionPluginWithGroovy extends FlowPlugin {
	private final GroovyClassLoader classLoader = new GroovyClassLoader();
	private GroovyObject conditionProcesser;

	@Param(value = "CONDITION_PROCESS_SCRIPT", label = "Condition Process Script", format = FieldFormat.groovy, defaultValue = "import com.newegg.flow.core.PluginConfig\n" +
			"import com.newegg.flow.core.plugin.ConditionPluginWithGroovy\n" +
			"\n" +
			"class ConditionProcesser {\n" +
			"    private String someDynamicKey = \"\"\n" +
			"    void process(ConditionPluginWithGroovy plugin) {\n" +
			"        Map<String, PluginConfig> namePluginConfigMap = plugin.plugins.collectEntries{[it.name, it]}\n" +
			"        if (!namePluginConfigMap.containsKey(someDynamicKey)) {\n" +
			"            plugin.logger.warn(String.format(\"No matched condition [%s] in conditions {%s}, will execute the default [%s].\",\n" +
			"                    someDynamicKey, namePluginConfigMap.keySet(), plugin.plugins.get(0)));\n" +
			"        }\n" +
			"        plugin.execute(plugin.context, plugin.config, namePluginConfigMap.getOrDefault(someDynamicKey, plugin.plugins.get(0)));\n" +
			"    }\n" +
			"}")
	private String conditionProcessScript;

	public ConditionPluginWithGroovy(Context context, PluginConfig config) throws PluginException {
		super(context, config);
	}

	@Override
	public Map<String, String> start(Map<String, String> request) throws PluginException {
		if (this.plugins == null || this.plugins.size() == 0) {
			throw new PluginException("No child plugin!");
		}
		try {
			if (conditionProcessScript != null && !"".equals(conditionProcessScript)) {
				Class<?> groovy = classLoader.parseClass(conditionProcessScript);
				conditionProcesser = (GroovyObject) groovy.newInstance();
				conditionProcesser.invokeMethod("process", new Object[]{this});
			}
			return request;
		} catch (Exception e) {
			throw new PluginException("execute groovy script error", e);
		}
	}

}
