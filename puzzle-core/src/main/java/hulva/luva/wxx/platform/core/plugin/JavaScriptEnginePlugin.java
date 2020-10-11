package hulva.luva.wxx.platform.core.plugin;

import java.util.Map;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;
import hulva.luva.wxx.platform.util.JavaScrptEngine;

/**
 * JavaScript插件
 */
@PluginMetaData(name = "JavaScriptEnginePlugin", type = PluginType.OTHER, version = 0)
public class JavaScriptEnginePlugin extends Plugin {

    @Param(value = "SCRIPT", label = "JavaScript", format = FieldFormat.javascript, sort = 20)
    String script;

    public JavaScriptEnginePlugin(Context context, PluginConfig config) throws PluginException {
        super(context, config);
    }

    @Override
    public Map<String, String> start(Map<String, String> request) throws PluginException {
        try {
            String resultLog = JavaScrptEngine.run(script, context.merge(request));
            request.put("DATA", resultLog);
            return request;
        } catch (Exception e) {
            throw new PluginException("javascript run error", e);
        }
    }
}
