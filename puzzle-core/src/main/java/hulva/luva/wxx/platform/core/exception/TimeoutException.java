package hulva.luva.wxx.platform.core.exception;

public class TimeoutException extends RuntimeException{
	private static final long serialVersionUID = -6770407852881478846L;
	
	public TimeoutException(){}
	
	public TimeoutException(Exception e) {
		super(e);
	}
}
