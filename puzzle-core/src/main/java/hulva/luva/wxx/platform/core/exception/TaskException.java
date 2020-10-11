package hulva.luva.wxx.platform.core.exception;

public class TaskException extends RuntimeException{
	private static final long serialVersionUID = -4084764336013499555L;

	public TaskException(){}
	public TaskException(Throwable e) { super(e); }
	public TaskException(String msg) { super(msg); }
	public TaskException(String msg, Throwable e) { super(msg, e); }
}