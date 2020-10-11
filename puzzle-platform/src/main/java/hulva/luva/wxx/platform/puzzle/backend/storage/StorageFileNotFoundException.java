package hulva.luva.wxx.platform.puzzle.backend.storage;

public class StorageFileNotFoundException extends StorageException {
	private static final long serialVersionUID = -4391100382445133292L;

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
