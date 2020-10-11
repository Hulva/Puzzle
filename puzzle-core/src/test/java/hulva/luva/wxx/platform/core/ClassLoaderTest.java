package hulva.luva.wxx.platform.core;

import java.io.IOException;

import hulva.luva.wxx.platform.core.util.PluginClassLoaderExecute;

public class ClassLoaderTest {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Thread.sleep(15000);
		
		PluginClassLoaderExecute.execute(loader->{
			Class<?> pluginClazz = loader.loadClass("com.newegg.flow.core.PluginConfig");
			System.out.println(pluginClazz.getClassLoader().toString());
		});
		PluginClassLoaderExecute.execute(loader->{
			Class<?> pluginClazz = loader.loadClass("com.newegg.flow.core.plugin.DataPlugin");
			System.out.println(pluginClazz.getClassLoader().toString());
		});
		PluginClassLoaderExecute.execute(loader->{
			Class<?> pluginClazz = loader.loadClass("com.newegg.flow.core.plugin.EachFlowDataPlugin");
			System.out.println(pluginClazz.getClassLoader().toString());
		});
		PluginClassLoaderExecute.execute(loader->{
			Class<?> pluginClazz = loader.loadClass("com.newegg.flow.core.plugin.FlowDataPlugin");
			System.out.println(pluginClazz.getClassLoader().toString());
		});
		PluginClassLoaderExecute.execute(loader->{
			Class<?> pluginClazz = loader.loadClass("com.newegg.flow.core.plugin.ThreadPoolPlugin");
			System.out.println(pluginClazz.getClassLoader().toString());
		});
		PluginClassLoaderExecute.execute("F:\\workspaces\\java\\FlowPlatform\\platform-extend-demo\\target\\platform-extend-demo-0.0.1.jar", loader->{
			Class<?> pluginClazz = loader.loadClass("com.newegg.flow.plugin.DaemonTest");
			System.out.println(pluginClazz.getClassLoader().toString());
			Object test = pluginClazz.newInstance();
			System.out.println(test);
			
			Thread t2 = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Class<?> pluginClazz = loader.loadClass("com.newegg.flow.plugin.DaemonTest");
						System.out.println(pluginClazz.getClassLoader().toString());
						Object test = pluginClazz.newInstance();
						System.out.println("Thread2:" + test);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
			
			t2.start();
			
			PluginClassLoaderExecute.execute("F:\\workspaces\\java\\FlowPlatform\\platform-extend-demo\\target\\platform-extend-demo-0.0.1.jar", loader1->{
				Class<?> class1 = loader1.loadClass("com.newegg.flow.plugin.DaemonTest");
				System.out.println(class1.getClassLoader().toString());
				Object test1 = class1.newInstance();
				System.out.println(test1);
			});
			
			t2.join();
		});
		
		Thread daemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("parent running");
				}
			}
		});
		daemon.start();
	}

}
