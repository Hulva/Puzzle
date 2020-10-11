package hulva.luva.wxx.platform.puzzle.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hulva Luva.H
 * @since 2019-10-12
 *
 */
@Data
@NoArgsConstructor
public class FileModel {
	private String name;
	private Long fileSize;
	private String type;
	private String modifyTime;
	private String className; // frontend style
	private String path;

	public FileModel(String name, Long fileSize, String type, String modifyTime) {
		super();
		this.name = name;
		this.fileSize = fileSize;
		this.type = type;
		this.modifyTime = modifyTime;
		this.className = "file".equals(type) ? "t-file" : "t-folder";
	}
}
