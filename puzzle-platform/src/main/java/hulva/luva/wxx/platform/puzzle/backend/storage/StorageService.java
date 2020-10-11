package hulva.luva.wxx.platform.puzzle.backend.storage;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Hulva Luva.H
 * @date 2019年3月22日
 * @description
 *
 */
public interface StorageService {
	void init();

	void store(MultipartFile file, String fileName);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	Resource loadAsResource(Path file);
	
	void deleteAll();

	Path getFile(String filename);
}
