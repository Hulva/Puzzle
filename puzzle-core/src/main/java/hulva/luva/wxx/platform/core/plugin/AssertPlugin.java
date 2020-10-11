package hulva.luva.wxx.platform.core.plugin;

import java.util.Map;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;
import hulva.luva.wxx.platform.util.JavaScrptEngine;

/**
 * 断言插件
 */
@PluginMetaData(name = "AssertPlugin", type = PluginType.OTHER, version = 0)
public class AssertPlugin extends ExceptionPlugin {

    @Param(value = "SCRIPT", label = "Assert Script", format = FieldFormat.javascript, sort = 20)
    String script;

    public AssertPlugin(Context context, PluginConfig config) throws PluginException {
        super(context, config);
    }

    @Override
    public Map<String, String> start(Map<String, String> request) throws PluginException {
        try {
            String result = JavaScrptEngine.run(script, context.merge(request));
            if (result == null || "false".equals(result.toLowerCase()) || "0".equals(result.toLowerCase())) {
                throw new IllegalStateException("Assert Error");
            }
            return request;
        } catch (Exception e) {
            throw new PluginException("javascript run error", e);
        }
    }

}
