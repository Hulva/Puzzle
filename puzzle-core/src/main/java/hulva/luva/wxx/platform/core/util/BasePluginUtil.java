package hulva.luva.wxx.platform.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.interfaces.PluginInterface;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginFieldMetadata;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata;
import hulva.luva.wxx.platform.util.PluginHelp;

public class BasePluginUtil {
	protected static Logger logger = LoggerFactory.getLogger(BasePluginUtil.class);

    /**
     * 	热加载插件
     */
    @SuppressWarnings("unchecked")
    public static <T extends PluginInterface> T getPlugin(Context context, PluginConfig config, Map<String, String> request, Class<T> clazz) throws PluginException{
    	ClassLoader parent_loader = Thread.currentThread().getContextClassLoader();
    	ClassLoader loader = null;
    	if(config.getPath() != null && !"".equals(config.getPath())){
    		loader = PluginClassLoader.get(config.getPath(), context.getClassLoader());
    	}else{
    		loader = PluginClassLoader.defaultLoader();
    	}
    	if(request == null) { request = new HashMap<String, String>(); }
        try {
        	Thread.currentThread().setContextClassLoader(loader);
        	Class<? extends PluginInterface> c = (Class<? extends PluginInterface>) loader.loadClass(config.getClassPath());
            Constructor<? extends PluginInterface> constructor = c.getConstructor(Context.class, PluginConfig.class);
            T result = (T) constructor.newInstance(context, config);
            result.initParam(request);
            return result;
        } catch (Exception e) {
            throw new PluginException("无法加载指定插件[" + config.getKey() + "]", e);
        }finally {
        	Thread.currentThread().setContextClassLoader(parent_loader);
		}
    }
    
    public static Map<String, String> execute(Context context, PluginConfig config, Map<String, String> request) throws PluginException {
    	PluginInterface plugin = null;
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
    	try {
    		plugin = getPlugin(context, config, request, PluginInterface.class);
    		return plugin.execute(request);
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
			destory(plugin);
        }
	}
    
    public static <T extends PluginInterface> void destory(T plugin) {
    	if(plugin == null) { return; }
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
    	Thread.currentThread().setName(plugin.key());
    	Thread.currentThread().setContextClassLoader(plugin.getClass().getClassLoader());
    	plugin.release();
    	ClassLoader tmp_loader = plugin.getClass().getClassLoader();
    	plugin = null;
    	if(tmp_loader instanceof PluginClassLoader) {
    		PluginClassLoader pluginLoader = (PluginClassLoader) tmp_loader;
    		if(pluginLoader != null && pluginLoader.isRemote()) {
    			try { pluginLoader.close(); } catch (Exception e) { }
    		}
    	}
    	Thread.currentThread().setContextClassLoader(loader);
    }
    
    public static <T extends PluginInterface> void destory(List<T> plugins) {
    	if(plugins == null) { return; }
    	for (PluginInterface plugin : plugins) {
            destory(plugin);
        }
	}
    
    public static List<PluginInterface> parsePlugin(Context context, List<PluginConfig> pluginConfigs, Map<String, String> request) throws PluginException {
        List<PluginInterface> plugins = new ArrayList<PluginInterface>();
        try {
        	for (PluginConfig c : pluginConfigs) {
        		PluginInterface plugin = BasePluginUtil.getPlugin(context, c, request, PluginInterface.class);
    			plugins.add(plugin);
    		}
		} catch (Exception e) {
			destory(plugins);
			throw e;
		}
		return plugins;
    }
    
    public static List<PluginConfig> parsePluginConfig(String pluginsJson){
        return JSONArray.parseArray(pluginsJson, PluginConfig.class);
    }
    
    public static PluginMetadata getPluginMetadata(Class<?> pluginClass) {
    	Field pluginMetadataFiled = PluginHelp.getAnnotations(pluginClass, PluginMetaData.class, false).get(0);
    	PluginMetaData pluginMetaData = pluginMetadataFiled.getAnnotation(PluginMetaData.class);
    	return PluginMetadata.builder()
    		.name(pluginMetaData.name())
    		.aliasName(pluginMetaData.aliasName())
    		.jarName(pluginMetaData.jarName())
    		.className(pluginClass.getName())
    		.type(pluginMetaData.type())
    		.version(pluginMetaData.version())
    		.build();
	}
    
	/**
	 * 获取插件的 Param 字段
	 */
    public static List<PluginFieldMetadata> getPluginFieldMetadatas(Class<?> pluginClass) {
		List<Field> pluginFields = PluginHelp.getParamFields(pluginClass);
		List<PluginFieldMetadata> pluginFieldMetadatas = new ArrayList<PluginFieldMetadata>();
		for (Field field : pluginFields) {
			Param param = field.getAnnotation(Param.class);
			PluginFieldMetadata pluginFieldMetadata = PluginFieldMetadata.builder()
				.format(param.format().name())
				.label(param.label())
				.value(param.value())
				.placeholder(param.placeholder())
				.required(param.required())
				.sort(param.sort())
				.readOnly(param.readOnly())
				.build();
			if (param.value() == null || "".equals(param.value())) {
				pluginFieldMetadata.setValue(param.defaultValue());
			}
			// TODO 还有其他需要添加元数据的 format
			if (param.format().equals(FieldFormat.select)) {
				pluginFieldMetadata.setSource(param.source());
			}
			pluginFieldMetadatas.add(pluginFieldMetadata);
		}
		pluginOutputFields(pluginFieldMetadatas, pluginClass);
		return pluginFieldMetadatas;
	}

	/**
	 * 获取插件的输出字段
	 */
    public static void pluginOutputFields(List<PluginFieldMetadata> pluginFieldEntities, Class<?> pluginClass) {
		Param outputParam = pluginClass.getAnnotation(Param.class);
		if (outputParam != null && FieldFormat.output.equals(outputParam.format())) {
			PluginFieldMetadata pluginFieldEntity = PluginFieldMetadata.builder()
					.format(outputParam.format().name())
					.value(outputParam.value())
					.required(outputParam.required())
					.readOnly(outputParam.readOnly())
					.sort(outputParam.sort())
					.build();
			pluginFieldEntities.add(pluginFieldEntity);
		}

		Class<?> superClass = pluginClass.getSuperclass();
		if (Plugin.class.isAssignableFrom(superClass)) {
			pluginOutputFields(pluginFieldEntities, superClass);
		}
	}
}
