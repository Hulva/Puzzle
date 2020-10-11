package hulva.luva.wxx.platform.core.exception;

public class PluginException extends Exception{
	private static final long serialVersionUID = 3479847894474417277L;

	public PluginException(String message){
		super(message);
	}
	
	public PluginException(String message, Throwable e) {
		super(message, e);
	}
}
