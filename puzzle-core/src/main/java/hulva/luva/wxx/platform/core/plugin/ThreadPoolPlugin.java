package hulva.luva.wxx.platform.core.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.annotation.Param;
import hulva.luva.wxx.platform.core.annotation.PluginMetaData;
import hulva.luva.wxx.platform.core.commons.FieldFormat;
import hulva.luva.wxx.platform.core.exception.PluginException;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * 开启线程池执行任务
 */
@PluginMetaData(name = "ThreadPoolPlugin", type = PluginType.OTHER, version = 0)
public class ThreadPoolPlugin extends FlowPlugin {

	@Param(value = "TIMEOUT", defaultValue = "86400", label = "Timeout", format = FieldFormat.number, sort = 20)
	long timeout;

	@Param(value = "TYPE", defaultValue = "ALL", label = "All or Any", format = FieldFormat.select, sort = 21, source = "[\"ALL\",\"Any\"]")
	String TYPE;

	private boolean ALL = true;

	public ThreadPoolPlugin(Context context, PluginConfig config) throws PluginException {
		super(context, config);
	}

	@Override
	public void init() throws PluginException {
		if (TYPE.equals("ANY")) {
			ALL = false;
		}
		super.init();
	}

	@Override
	public Map<String, String> start(Map<String, String> request) throws PluginException {
		List<Callable<Boolean>> threads = new ArrayList<>();
		for (PluginConfig c : plugins) {
			try {
				threads.add(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						BasePluginUtil.execute(getContext(), c, request);
						return true;
					}
				});
			} catch (Exception e) {
				throw new PluginException("无法加载指定插件[" + c.getKey() + "]", e);
			}
		}
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threads.size());
		try {
			if (ALL) {
				List<Future<Boolean>> futures = fixedThreadPool.invokeAll(threads);
				for (Future<Boolean> f : futures) {
					f.get(timeout, TimeUnit.SECONDS);
				}
			} else {
				fixedThreadPool.invokeAny(threads);
			}
		} catch (Exception e) {
			throw new PluginException("线程池出错了: ", e);
		} finally {
			fixedThreadPool.shutdown();
		}
		return request;
	}
}
