package hulva.luva.wxx.platform.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSON;

import hulva.luva.wxx.platform.util.PatternReplace;

/**
 * 
 * @author Hulva Luva.H
 * @date 2020-10-11 11:15
 * @since 0.0.1
 *
 */
public class PluginConfig implements Serializable{
	private static final long serialVersionUID = 959673228666710347L;
	
	private String key;// 任务执行的关键字
    private String name;// 插件名称
    private String version;// 插件版本
    private String path;// jar包路径
    private String classPath;// 插件启动类名称
    private Map<String, String> config = new HashMap<>();// 插件在页面的配置内容
    
    public PluginConfig() { }

    public void parse(Context context, Map<String, String> request) {
        if (this.config != null && this.config.size() > 0) {
            this.config.forEach((key, value) -> {
                try {
                    key = PatternReplace.exec(key, context.merge(request));
                    value = PatternReplace.exec(value, context.merge(request));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.config.put(key, value);
            });
        }
    }

    public String get(String name) {
        return config.get(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
    public void setKey(String key) {
		this.key = key;
	}
    public String getKey() {
        return key == null ? name : key;
    }
    public static PluginConfig parse(Map<String, String> config, String json) {
    	if(config != null) {
    		json = PatternReplace.exec(json, config);
    	}
        return JSON.parseObject(json, PluginConfig.class);
    }

	@Override
	public String toString() {
		return String.format("PluginConfig [key=%s, name=%s, version=%s, path=%s, classPath=%s, config=%s]", key, name, version, path, classPath, config);
	}
}
