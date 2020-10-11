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
import hulva.luva.wxx.platform.util.GroovyEngine;

/**
 * Groovy插件
 */
@PluginMetaData(name = "GroovyEnginePlugin", type = PluginType.OTHER, version = 0)
public class GroovyEnginePlugin extends Plugin {

    @Param(value = "SCRIPT", label = "Groovy Script", format = FieldFormat.groovy, sort = 20)
    String script;

    public GroovyEnginePlugin(Context context, PluginConfig config) throws PluginException {
        super(context, config);
    }

    @Override
    public Map<String, String> start(Map<String, String> request) throws PluginException {
        try {
            String result = GroovyEngine.run(script, context.merge(request));
            request.put("DATA", result);
            return request;
        } catch (Exception e) {
            throw new PluginException("javascript run error", e);
        }
    }
}
