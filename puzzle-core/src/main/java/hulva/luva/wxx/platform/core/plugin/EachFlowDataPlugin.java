package hulva.luva.wxx.platform.core.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.interfaces.Consumer;
import hulva.luva.wxx.platform.core.plugin.interfaces.PluginInterface;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * 	循环流程插件,
 */
public abstract class EachFlowDataPlugin extends FlowPlugin{

    public EachFlowDataPlugin(Context context, PluginConfig config) throws PluginException {
        super(context, config);
    }

    @Override
    public final Map<String, String> start(Map<String, String> request) throws PluginException {
		this.onStart();
		try {
			List<PluginInterface> pluginIntegerList = BasePluginUtil.parsePlugin(context, plugins, request);
			try {
				readData(row->{
					Map<String, String> result = new HashMap<String, String>();
					result.putAll(request);
					result.putAll(row);
			        for (PluginInterface p : pluginIntegerList) {
			        	result = p.execute(result);
			        }
			        return result;
				});
			} finally {
				BasePluginUtil.destory(pluginIntegerList);
			}
			return request;
		} finally {
			this.onStop();
		}
	}
	
	public final void release() {
        super.release();
	}

	/**
	 * 	开启流程
	 */
	protected abstract void onStart() throws PluginException;
	/**
	 * 	流程数据流处理
	 */
	public abstract void readData(Consumer<Map<String, String>> consumer) throws PluginException;
	/**
	 * 	关闭流程
	 */
	protected abstract void onStop();
	
}
