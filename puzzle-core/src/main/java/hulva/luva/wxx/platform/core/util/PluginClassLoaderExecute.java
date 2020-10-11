package hulva.luva.wxx.platform.core.util;

import java.io.IOException;

public class PluginClassLoaderExecute {

	public static void execute(PluginExecute execute) throws InterruptedException, IOException {
		PluginClassLoader loader = PluginClassLoader.get();
		try {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try { execute.execute(loader); } catch (Exception e) { throw new RuntimeException(e); }
				}
			});
			t.setContextClassLoader(loader);
			t.start();
			t.join();
		} finally {
			loader.close();
		}
	}
	
	public static void execute(String jar, PluginExecute execute) throws InterruptedException, IOException {
		PluginClassLoader loader = PluginClassLoader.get(jar, PluginClassLoader.get());
		try {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try { execute.execute(loader); } catch (Exception e) { throw new RuntimeException(e); }
				}
			});
			t.setContextClassLoader(loader);
			t.start();
			t.join();
		} finally {
			loader.close();
		}
	}
	
	@FunctionalInterface
	public static interface PluginExecute{
		void execute(ClassLoader loader) throws Exception;
	}
}
