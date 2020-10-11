package hulva.luva.wxx.platform.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.Result;
import hulva.luva.wxx.platform.core.aop.ExecutePoint;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.interfaces.PluginInterface;
import hulva.luva.wxx.platform.util.ObjectUtil;
import hulva.luva.wxx.platform.util.PluginHelp;

@Result({})
public abstract class Plugin implements PluginInterface {
	@Param(value="log", defaultValue="true", required = false, label = "log", format=FieldFormat.switchs, sort = 999)
	boolean log;
	
    protected final Context context;
    protected final PluginConfig config;

    public Context getContext() { return context; }
    public PluginConfig getConfig() { return config; }
    
    public Plugin(Context context, PluginConfig config) throws PluginException {
        this.context = context;
        this.config = config;
    }
    
    public final void initParam(Map<String, String> request) throws PluginException {
    	config.parse(context, request);
        Class<? extends Plugin> clazz = this.getClass();
        Map<String, String> all = new HashMap<String, String>();
        all.putAll(request);
        all.putAll(this.config.getConfig());
        for (Field field : PluginHelp.getParamFields(clazz)) {
            Param param = field.getAnnotation(Param.class);
            String value = all.get(param.value());
            if (value == null && param.required() && "".equals(param.defaultValue())) {
                throw new PluginException("can not find param [" + param.value() + "] by plugin:" + key());
            }
            try {
                if (value != null) {
                    ObjectUtil.set(this, field, value);
                } else if (!"".equals(param.defaultValue())) {
                    ObjectUtil.set(this, field, param.defaultValue());
                }
            } catch (Exception e) {
                throw new PluginException("param [" + param.value() + "] argument error by plugin:" + key());
            }
        }
        if(log) {ExecutePoint.onInit(this.config.getKey(), this.context, this.config, request);}
        this.init();
    }

    public final String key() {
        if (config == null) { return getClass().getName(); }
        return config.getKey();
    }

    /**
     * 	提交全局配置
     */
    public final void commitContext(String value){ this.context.commitContext(key(), null, value); }
    public final void commitContext(String key, String value){ this.context.commitContext(key(), key, value); }

    /**
     * 	开始执行插件
     */
    @Override
    public final Map<String, String> execute(Map<String, String> request) throws PluginException {
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String key = key();
        if(request == null) { request = new HashMap<String, String>(); }
        try {
            Thread.currentThread().setName(key);
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            Map<String, String> result = this.start(request);
            if(result == null) { result = new HashMap<String, String>(); }
            if(log) { ExecutePoint.onExecuteResult(key, this.context, this.config, result); }
            return result;
        } catch (Exception e) {
            throw new PluginException("Plugin execute error", e);
        }finally {
        	Thread.currentThread().setContextClassLoader(loader);
		}
    }

    public void init() throws PluginException {}

    public abstract Map<String, String> start(Map<String, String> request) throws PluginException;

    @Override
    public void release() { }
}
