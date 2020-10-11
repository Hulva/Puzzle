package hulva.luva.wxx.platform.core.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.aop.ExecutePoint;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.interfaces.Consumer;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * 	服务类插件
 */
public abstract class ServicePlugin extends FlowPlugin {
	
	@Param(value = "before", required = false, label = "before", format=FieldFormat.plugins, sort = 990)
    String beforePluginsJson;
	
	@Param(value = "after", required = false, label = "after", format=FieldFormat.plugins, sort = 992)
    String afterPluginsJson;
	
	protected Thread thread;
	private List<ServiceListener> listeners = new ArrayList<ServicePlugin.ServiceListener>();

    public ServicePlugin(Context context, PluginConfig config) throws PluginException {
        super(context, config);
    }
    
    public final boolean onProcess() throws PluginException {
    	if(thread != null) { return false; }
		return true;
    }

	@Override
	public final Map<String, String> start(Map<String, String> request) throws PluginException {
		if(thread != null) { return null; }
		this.onStart();
		if(beforePluginsJson != null) {
    		List<PluginConfig> plugins = BasePluginUtil.parsePluginConfig(this.beforePluginsJson);
    		for (PluginConfig c : plugins) {
    			request = BasePluginUtil.execute(getContext(), c, request);
			}
    	}
		this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
            	try {
            		thread(requestMap -> {
            			Context ctx = new Context(getContext(), requestMap);
            			Map<String, String> result = requestMap;
            	        for (PluginConfig c : plugins) {
            	        	result = BasePluginUtil.execute(ctx, c, result);
            	        }
            	        return result;
                    });
				} catch (Exception e) {
					ExecutePoint.onExecuteException(getContext(), getConfig(), "Service plugin error: ", e);
				}finally {
					close();
				}
            }
        });
		this.thread.setDaemon(false);
		this.thread.start();
		return request;
	}

	/**
	 * 	开启服务
	 */
	protected abstract void onStart() throws PluginException;
	/**
	 * 	服务数据流处理
	 */
	protected abstract void thread(Consumer<Map<String, String>> consumer) throws PluginException;
	/**
	 * 	关闭服务
	 */
	protected abstract void onStop();
	
	public boolean isAlive() {
		if(this.thread == null || this.thread.isInterrupted()) { return false; }
		return true;
	}
	
	public final void close() {
        if (thread == null) { return; }
        thread.interrupt();
        try {
        	if(afterPluginsJson != null) {
        		List<PluginConfig> plugins = BasePluginUtil.parsePluginConfig(this.afterPluginsJson);
        		Map<String, String> result = null;
    	        for (PluginConfig c : plugins) {
    	        	result = BasePluginUtil.execute(context, c, result);
    	        }
        	}
		} catch (Exception e) {
			ExecutePoint.onWarning(key(), getContext(), getConfig(), "after on close service error", e);
		}
        this.onStop();
    	listeners.forEach(s->{
        	try { s.onClose();  } catch (Exception e) { ExecutePoint.onWarning(key(), getContext(), getConfig(), "listener on close service error", e); }
        });
        this.thread = null;
        release();
	}
	
	public final void addListener(ServiceListener listener) {
		listeners.add(listener);
	}
	
	public final void removeListener(ServiceListener listener) {
		listeners.remove(listener);
	}
	
	public final void release() { super.release(); }
	
	@FunctionalInterface
	public interface ServiceListener{
		void onClose();
	}
}
