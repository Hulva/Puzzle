package hulva.luva.wxx.platform.core;

import java.util.Set;

public class ThreadTest {
	
	public static void main(String[] args) {
		Set<Thread> threads = Thread.getAllStackTraces().keySet();
		threads.forEach(t->{
			System.out.println(t.getName() + ":" + t.getContextClassLoader());
		});
	}
}
