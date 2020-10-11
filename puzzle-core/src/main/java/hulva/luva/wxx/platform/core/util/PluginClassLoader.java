package hulva.luva.wxx.platform.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import hulva.luva.wxx.platform.core.Plugin;
import hulva.luva.wxx.platform.core.plugin.interfaces.PluginInterface;

public final class PluginClassLoader extends URLClassLoader{
	private static final Logger logger = LoggerFactory.getLogger(Plugin.class);
	private static final String PROCESS_REAPER = "process reaper";
	private static final boolean IS_NOT_WINDOWS = !(System.getProperty("os.name").startsWith("Windows"));

    static PluginClassLoader loader = new PluginClassLoader(PluginClassLoader.class.getClassLoader());
    static Timer cleanTimer;

    /**
     * 	开启纯净模式
     * 	此模式开启后, 每个Job的每一个步骤都会创建classLoader
     */
    static boolean pure = false;
    static Map<String, PluginClassLoader> classloaderStack = new HashMap<String, PluginClassLoader>();

    static {
    	cleanup();
    }

    static void cleanup() {
    	if(cleanTimer != null) { cleanTimer.cancel(); }
    	if(!pure) { return; }
    	long delay = 60 * 60 * 1000; // 1 hour
    	cleanTimer = new Timer(true);
    	cleanTimer.schedule(new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
				for (Thread t : maps.keySet()) {
					if(t.isInterrupted() || !t.isDaemon()){ continue; }
					if (isGC(t.getContextClassLoader())) {
						// ignore process reaper thread
						if(IS_NOT_WINDOWS && PROCESS_REAPER.equals(t.getName())) {
							t.setContextClassLoader(loader);
							continue;
						}
						// interrupt daemon thread
						try {
							t.interrupt();
							logger.info("[timer] close thread: " + t.getName());
						} catch (Exception e) {
							logger.error("[timer] interrupt thread error: " + t.getName(), e);
						}
						// force stop thread
						try {
							if (t != null) { t.stop(); }
						} catch (Exception e) {
							logger.error("[timer] stop thread error: ", e);
						}
					}
				}
			}
		}, delay, delay);
    	logger.info("daemon thread cleaner started");
    }

    static boolean isGC(ClassLoader loader) {
    	if(loader == null) { return false; }
    	if(loader instanceof PluginClassLoader) {
    		PluginClassLoader pl = (PluginClassLoader) loader;
    		if(pl.gc) { return true; }
    	}
    	if(loader.getParent() != null){
    		return isGC(loader.getParent());
    	}
    	return false;
	}
    
    static PluginClassLoader loader(String jar, PluginClassLoader parentClassLoader) {
    	File file = new File(jar);
    	if(!file.exists() || !file.isFile()) {
    		throw new RuntimeException("can not find jar in path:" + jar);
    	}
    	try {
    		return new PluginClassLoader(file.toURI().toURL(), parentClassLoader, true);
		} catch (Exception e) {
			throw new RuntimeException("can not find jar in path:" + jar);
		}
    }

  //======================================================
    /**
     * 	获取默认加载器
     */
    public static PluginClassLoader defaultLoader(){
    	return loader;
    }
    /**
     * 	获取新加载器
     */
    public static PluginClassLoader get() {
    	if(pure) {
    		return new PluginClassLoader();
    	}else {
    		return defaultLoader();
    	}
    }
    /**
     *	加载一个指定jar包的加载器
     */
    public static PluginClassLoader get(String jar, PluginClassLoader parentClassLoader) {
    	PluginClassLoader loader = null;
    	if(classloaderStack.containsKey(jar)) {
    		loader = classloaderStack.get(jar);
    	}else {
    		loader = loader(jar, parentClassLoader);
    	}
    	if(!pure) {
    		classloaderStack.put(jar, loader);
    	}
    	return loader;
    }
    public static void enablePure() {
    	pure = true;
    	cleanup();
    }
    public static void disablePure() {
    	pure = false;
    	cleanup();
    }
    
    public static List<Class<?>> pluginClass(File file, String packagename) throws IOException, ClassNotFoundException{
    	List<Class<?>> plugins = new ArrayList<Class<?>>();
    	try(JarFile jar = new JarFile(file)){
    		try(PluginClassLoader loader = new PluginClassLoader(file.toURI().toURL(), PluginClassLoader.loader, true)){
        		Enumeration<JarEntry> jars = jar.entries();
            	while(jars.hasMoreElements()) {
            		JarEntry entry = jars.nextElement();
            		if(entry.isDirectory() || !entry.getName().endsWith(".class")) { continue; }
            		String className = entry.getName().replace("/", ".");
            		if(className.indexOf("$") != -1){ continue; }
            		className = className.substring(0, className.length() - 6);
            		if (!className.startsWith(packagename)) {
						continue;
					}
            		try {
            			Class<?> clazz = loader.loadClass(className);
            			if(clazz.isSynthetic() || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) { continue; }
                		if(PluginInterface.class.isAssignableFrom(clazz)) {
                			plugins.add(clazz);
                		}
					} catch (Exception e) { }
            	}
        	}
    	}
		return plugins;
    }
    
    public static List<Class<?>> systemPluginClass() throws IOException, ClassNotFoundException{
    	List<Class<?>> plugins = new ArrayList<Class<?>>();
    	ClassPath classPath = ClassPath.from(loader.getParent());
    	ImmutableSet<ClassInfo> classInfos = classPath.getTopLevelClassesRecursive("com.newegg.flow.core.plugin");
    	for (ClassInfo classInfo : classInfos) {
    		Class<?> clazz = classInfo.load();
    		if(clazz.isSynthetic() || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) { continue; }
    		if(PluginInterface.class.isAssignableFrom(clazz)) {
    			plugins.add(clazz);
    		}
		}
		return plugins;
    }

    //======================================================
    private boolean gc = false;
	String id = UUID.randomUUID().toString();
	private boolean remote = false;
    
    private PluginClassLoader() {
        super(new URL[0], loader);
    }
	private PluginClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}
    private PluginClassLoader(URL jar, ClassLoader parent, boolean remoteJar) throws MalformedURLException {
        super(new URL[]{ jar }, parent);
        this.remote  = remoteJar;
    }

    private boolean isChildClassLoader(ClassLoader loader) {
    	if(loader == null){ return false; }
    	if(loader == this){ return true; }
    	if(loader.getParent() != null){
    		return isChildClassLoader(loader.getParent());
    	}
    	return false;
	}

	@SuppressWarnings("deprecation")
	private void closeDaemon() {
		Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
		for (Thread t : maps.keySet()) {
			if(t.isInterrupted() || !t.isDaemon()){ continue; }
			if (isChildClassLoader(t.getContextClassLoader())) {
				// ignore process reaper thread
				if(IS_NOT_WINDOWS && PROCESS_REAPER.equals(t.getName())) {
					t.setContextClassLoader(loader);
					continue;
				}
				// interrupt daemon thread
				try {
					t.interrupt();
					logger.info("close thread: " + t.getName());
				} catch (Exception e) {
					logger.error("interrupt thread error: " + t.getName(), e);
				}
				// stop thread if it still alive
				try {
					if (t.isAlive()) { t.stop(); }
				} catch (Exception e) {
					logger.error("stop thread error: ", e);
				}
			}
		}
	}

    private void removeShutdownHooks() {
		try {
			String className = "java.lang.ApplicationShutdownHooks";
			Class<?> clazz = Class.forName(className);
			Field field = clazz.getDeclaredField("hooks");
			field.setAccessible(true);
			List<Thread> removedHooks = new ArrayList<Thread>();
			synchronized(clazz) {
				@SuppressWarnings("unchecked")
				IdentityHashMap<Thread, Thread> hooks = (IdentityHashMap<Thread, Thread>) field.get(clazz);
				IdentityHashMap<Thread, Thread> preservedHooks = new IdentityHashMap<>(hooks.size());
				for(Thread t : hooks.keySet()) {
					if(!Thread.State.NEW.equals(t.getState())) {
						preservedHooks.put(t, t);
					}
					if (isChildClassLoader(t.getContextClassLoader())) {
						removedHooks.add(t);
					} else {
						preservedHooks.put(t, t);
					}
				}
				if(removedHooks.size() > 0) {
					field.set(clazz, preservedHooks);
				}
			}
			// execute thread may take long time, so removed from synchronized block
			if(removedHooks.size() > 0) {
				for(Thread t : removedHooks) {
					try {
						t.start();
					} catch (Exception e) {
						logger.error("<start> execute shutdown hook error[" + t.getName() + "]", e);
					}
				}
				for(Thread t : removedHooks) {
					try {
						if(t.isAlive() && !t.isInterrupted()) {
							t.join();
						}
					} catch (Exception e) {
						logger.error("<join> execute shutdown hook error[" + t.getName() + "]", e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("remove shutdown hook error", e);
		}
	}
	
	@Override
    public String toString() {
        return PluginClassLoader.class.getName() + ":" + id;
    }
    
	public boolean isRemote() {
		return remote;
	}
	
    @Override
    public void close() throws IOException {
    	if(!pure) { return; }
    	removeShutdownHooks();
    	closeDaemon();
    	super.close();
    	this.gc = true;
    }
    
    public static void main(String[] args) throws ClassNotFoundException, IOException {
    	List<Class<?>> classes = pluginClass(new File("e:\\platform-extend-hdfs-0.0.1.jar"), "com.newegg.flow.plugin.hdfs");
    	for (Class<?> class1 : classes) {
			System.out.println(class1);
		}
	}
}
