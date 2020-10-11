package hulva.luva.wxx.platform.core;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.util.PluginClassLoader;

/**
 * 上下文对象
 *
 * @author fy07
 */
public class Context implements Serializable, Closeable {
    private static final long serialVersionUID = 8341709723978245116L;

    //任务ID
    private final String id;
    private final String name;
    //classloader
    private final PluginClassLoader parentClassLoader;
    //任务执行的版本
    private final long version;
    //keychain
    private final String keychain;
    //系统初始化配置内容
    private final PluginConfig root;
    //任务执行的全局结果
    private final Map<String, String> systemcontext = new HashMap<String, String>();
    private final Map<String, String> context = new HashMap<String, String>();

    public Context(String id, String name, String config, Map<String, String> context) throws PluginException {
        this(id, name, PluginConfig.parse(context, config), context, PluginClassLoader.get());
    }
    
    public Context(Context ctx, Map<String, String> context) throws PluginException {
        this(ctx.id, ctx.name, ctx.root, ctx.context, ctx.parentClassLoader);
        if(context != null) {
        	context.forEach((k,v)->{
            	this.commitContext(k, null, v);
            });
        }
    }

    public Context(String id, String name, PluginConfig root, Map<String, String> context) throws PluginException {
    	this(id, name, root, context, PluginClassLoader.get());
    }
    
    public Context(String id, String name, PluginConfig root, Map<String, String> context, PluginClassLoader parentClassLoader) throws PluginException {
    	if (id == null || name == null || root == null) { throw new IllegalArgumentException(); }
        this.id = id;
        this.name = name;
        this.keychain = UUID.randomUUID().toString();
        this.version = System.currentTimeMillis();
        this.root = root;
        this.parentClassLoader = parentClassLoader;
        this.systemcontext.putAll(System.getenv());
        System.getProperties().forEach((key, value) -> {
            this.systemcontext.put(key.toString(), value.toString());
        });
        if (context != null && !context.isEmpty()) {
            this.context.putAll(context);
        }
    }

    public Map<String, String> all() {
    	Map<String, String> ctx = new HashMap<String, String>();
    	ctx.putAll(systemcontext);
    	ctx.putAll(context);
        return ctx;
    }
    
    public void commitContext(String step, String key, String value) {
        if (key == null) {
            this.context.put(step, value);
        } else {
            this.context.put(step + "." + key, value);
        }
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public PluginConfig getRoot() {
        return root;
    }
    public String getKeychain() {
		return keychain;
	}
    public long getVersion() {
        return version;
    }
    public PluginClassLoader getClassLoader() {
        return parentClassLoader;
    }
    @Override
    public void close() throws IOException {
        parentClassLoader.close();
    }

	public Map<String, String> ctx() {
		return context;
	}
    public Map<String, String> merge(Map<String, String> req) {
    	Map<String, String> ctx = new HashMap<String, String>();
    	ctx.putAll(systemcontext);
    	ctx.putAll(context);
    	if(req != null) {
    		ctx.putAll(req);
    	}
        return ctx;
    }

	@Override
	public String toString() {
		return String.format("Context [id=%s, name=%s, version=%s, keychain=%s, context=%s]", id, name, version, keychain, context);
	}
}
