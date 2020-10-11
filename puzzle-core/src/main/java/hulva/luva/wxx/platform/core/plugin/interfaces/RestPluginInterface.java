package hulva.luva.wxx.platform.core.plugin.interfaces;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;

import hulva.luva.wxx.platform.core.exception.PluginException;

public interface RestPluginInterface extends PluginInterface{

	Map<String, String> onRequest(HttpServletRequest request, HttpServletResponse response, JSONObject jsonParam) throws PluginException;
	
}
