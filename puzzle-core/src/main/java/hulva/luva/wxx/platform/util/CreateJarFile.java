package hulva.luva.wxx.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class CreateJarFile {

	/** 
	 * 动态生成Jar包 
	 */  
	public static File createJar(Class<?> clazz) throws Exception {  
	    CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
	    URL root = codeSource.getLocation();
	    File jar = new File(root.toURI());
	    if(root.getPath().endsWith(".jar") && jar.isFile()){
	    	return jar;
	    }
	    System.out.println(root.getPath());
	    JarOutputStream out = null;  
	    jar = File.createTempFile("temp-", ".jar", new File(System.getProperty("java.io.tmpdir")));  
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        //public void run() { jar.delete(); }
	    });
	    try {  
	        File path = new File(root.toURI());  
	        Manifest manifest = new Manifest();  
	        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");  
	        manifest.getMainAttributes().putValue("Created-By", "fritz");  
	        out = new JarOutputStream(new FileOutputStream(jar), manifest);  
	        writeBaseFile(out, path, "");  
	    } finally {  
	        out.flush();  
	        out.close();  
	    }  
	    return jar;  
	}
	/** 
	 * 递归添加.class文件 
	 */  
	private static void writeBaseFile(JarOutputStream out, File file, String base) throws IOException {
	    if (file.isDirectory()) {  
	        File[] fl = file.listFiles();  
	        if (base.length() > 0) { base = base + "/"; }
	        for (int i = 0; i < fl.length; i++) {
	            writeBaseFile(out, fl[i], base + fl[i].getName());  
	        }  
	    } else {  
	        out.putNextEntry(new JarEntry(base));  
	        FileInputStream in = null;  
	        try {  
	            in = new FileInputStream(file);  
	            byte[] buffer = new byte[1024];  
	            int n = in.read(buffer);  
	            while (n != -1) {  
	                out.write(buffer, 0, n);  
	                n = in.read(buffer);  
	            }  
	        } finally {  
	            in.close();  
	        }    
	    }  
	}
	
}
