package hulva.luva.wxx.platform.puzzle.backend.storage;

public class StorageInvalidFileException extends StorageException {
	private static final long serialVersionUID = -4391100382445133292L;

	public StorageInvalidFileException(String message) {
		super("Invalid file: " + message);
	}

	public StorageInvalidFileException(String message, Throwable cause) {
		super("Invalid file: " + message, cause);
	}
}
