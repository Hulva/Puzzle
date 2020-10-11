package hulva.luva.wxx.platform.puzzle;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hulva.luva.wxx.platform.core.RestfulPlatform;
import hulva.luva.wxx.platform.core.ServicePlatform;

@SpringBootApplication
public class PuzzlePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuzzlePlatformApplication.class, args);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ServicePlatform.services.keySet().forEach(t -> {
					try {
						ServicePlatform.stop(t);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				RestfulPlatform.servicesByID.keySet().forEach(t -> {
					try {
						ServicePlatform.stop(t);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		});
	}

}
