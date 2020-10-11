package hulva.luva.wxx.platform.core.aop;

import com.alibaba.fastjson.JSONObject;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutePoint {
	private static Logger logger = LoggerFactory.getLogger(ExecutePoint.class);

	private static List<ExecutePointListener> listeners = new ArrayList<>();
	private static MailListener mailListener;
	private static CustomMailContentHelper customMailContentHelper;
	
	public static void setMailListener(MailListener mailListener) {
		ExecutePoint.mailListener = mailListener;
	}

	public static void setCustomMailContentHelper(CustomMailContentHelper customMailContentHelper) {
		ExecutePoint.customMailContentHelper = customMailContentHelper;
	}
	
	public static void addListener(ExecutePointListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(ExecutePointListener listener) {
		listeners.remove(listener);
	}

	static void log(String id, String data) {
		for (ExecutePointListener executePointListener : listeners) {
			try {
				executePointListener.log(id, data);
			} catch (Exception e) {
				logger.error("save log error", e);
			}
		}
	}

	public static void onInit(String key, Context context, PluginConfig config, Map<String, String> request) {
		String logData = generateLogData("onInit", context, config, null, null, request, null);
		listeners.forEach(listener -> listener.log(context.getId(), logData));
		logger.debug("onInit>> " + generateLog(context, config));
	}

	public static void onExecuteResult(String key, Context context, PluginConfig config, Map<String, String> result) {
		String logData = generateLogData("onExecuteResult", context, config, null, null, null, result);
		listeners.forEach(listener -> listener.log(context.getId(), logData));
		logger.debug("onExecuteResult: " + generateLog(context, config) + " >> " + result);
	}

	public static void onExecuteLog(String key, Context context, PluginConfig config, String message) {
		String logData = generateLogData("onExecuteLog", context, config, null, message, null, null);
		listeners.forEach(listener -> listener.log(context.getId(), logData));
		logger.debug("onExecuteLog>> " + generateLog(context, config) + " >> " + message);
	}
	
	public static void sendMail(Context context, PluginConfig config, String message) {
		mailListener.send(context, config, message, null);
	}
	
	/**
	 *	for some old plugin compatible 
	 */
	@Deprecated
	public static void sendMail(String key, Context context, PluginConfig config, String message) {
		mailListener.send(context, config, message, null);
	}
	
	public static void sendMail(MailContentAppendder appendder) {
		customMailContentHelper.send(appendder);
	}

	public static void onExecuteException(String key, Context context, PluginConfig config, Exception e) {
		onExecuteException(context, config, "", e);
	}
	
	public static void onExecuteException(Context context, PluginConfig config, String message, Exception e) {
		JSONObject logData = generateLogDataJSON("onExecuteException", context, config, e, message, null, null);
		listeners.forEach(listener -> listener.log(context.getId(), logData.toJSONString()));
		mailListener.send(context, config, message, e);
		logger.error("onExecuteException>> ", e);
	}

	public static void onWarning(String key, Context context, PluginConfig config, String message, Exception e) {
		String logData = generateLogData("onWarning", context, config, e, message, null, null);
		listeners.forEach(listener -> listener.log(context.getId(), logData));
		logger.warn("onWarning>> " + message, e);
	}

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private static String generateLogData(String stage, Context context, PluginConfig config, Exception e, String message, Map<String, String> request, Map<String, String> result) {
		return JSONObject.toJSONString(generateLogDataJSON(stage, context, config, e, message, request, result), true);
	}
	
	private static JSONObject generateLogDataJSON(String stage, Context context, PluginConfig config, Exception e, String message, Map<String, String> request, Map<String, String> result) {
		JSONObject logData = new JSONObject();
		logData.put("key", config.getKey());
		logData.put("tag", stage);
		logData.put("job", context.getId());
		logData.put("name", context.getName());
		logData.put("class", config.getClassPath());
		logData.put("version", context.getVersion());
		logData.put("keychain", context.getKeychain());
		logData.put("timestamp", formatter.format(ZonedDateTime.now()));
		logData.put("config", config.getConfig());
		
		Map<String, String> ctx = new HashMap<String, String>();
		ctx.putAll(context.ctx());
		logData.put("context", ctx);
		logData.put("plugin.version", config.getVersion());
		
		if (e != null) { logData.put("exception", e.getMessage()); }
		if (message != null) { logData.put("message", message); }
		if (request != null) {
			Set<String> objectKeySet = new HashSet<String>();
			request.forEach((k,v)->{
				if(k.endsWith("_DATA")) {
					objectKeySet.add(k.substring(0, k.lastIndexOf("_DATA")));
				}
			});
			request.forEach((k, v)->{
				boolean flag = true;
				for (String objKey : objectKeySet) {
					if(k.startsWith(objKey + ".")) { flag = false; }
				}
				if(flag) { logData.put("request." + k, v); }
			});
		}
		if(result != null) {
			Set<String> objectKeySet = new HashSet<String>();
			result.forEach((k,v)->{
				if(k.endsWith("_DATA")) {
					objectKeySet.add(k.substring(0, k.lastIndexOf("_DATA")));
				}
			});
			result.forEach((k, v)->{
				boolean flag = true;
				for (String objKey : objectKeySet) {
					if(k.startsWith(objKey + ".")) { flag = false; }
				}
				if(flag) { logData.put("result." + k, v); }
			});
		}
		return logData;
	}

	private static String generateLog(Context context, PluginConfig config) {
		JSONObject result = generateJSONLog(context, config);
		return JSONObject.toJSONString(result, true);
	}
	
	private static JSONObject generateJSONLog(Context context, PluginConfig config) {
		JSONObject result = new JSONObject();
		result.put("id", context.getId());
		result.put("key", config.getKey());
		result.put("name", context.getName());
		result.put("version", context.getVersion());
		result.put("class", config.getClassPath());
		return result;
	}

	public static void main(String[] args) {
		String key = "asdasdsdfsadf_DATA";
		System.out.println(key.substring(0, key.lastIndexOf("_DATA")));
	}
}
