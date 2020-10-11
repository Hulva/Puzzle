package hulva.luva.wxx.platform.core.plugin;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * Restful
 */
public abstract class RestPlugin extends FlowPlugin{

    public RestPlugin(Context context, PluginConfig config) throws PluginException {
        super(context, config);
    }

    @Override
    public final Map<String, String> start(Map<String, String> request) throws PluginException {
        this.onStart();
        return request;
    }

    public final Map<String, String> onRequest(HttpServletRequest request, HttpServletResponse response, JSONObject jsonParam) throws PluginException {
    	Map<String, String> requestMap = readData(request, response, jsonParam);
        Context ctx = new Context(getContext(), requestMap);
        Map<String, String> result = requestMap;
        for (PluginConfig c : plugins) {
        	result = BasePluginUtil.execute(ctx, c, result);
        }
        return result;
    }

    public final void release() {
        this.onStop();
        super.release();
    }

    /**
     * 	开启流程
     */
    protected abstract void onStart() throws PluginException;

    /**
     * 	流程数据流处理
     */
    public abstract Map<String, String> readData(HttpServletRequest request, HttpServletResponse response, JSONObject jsonParam) throws PluginException;

    /**
     * 	关闭流程
     */
    protected abstract void onStop();

}
