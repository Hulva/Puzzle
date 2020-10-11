package hulva.luva.wxx.platform.core;

import java.util.List;

import com.alibaba.fastjson.JSON;

import hulva.luva.wxx.platform.core.plugin.ShellPlugin;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginFieldMetadata;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;

/**
 * @author Hulva Luva.H
 * @date 2020-10-12 00:00
 * @since 0.0.1
 *
 */
public class PluginTests {

	public static void main(String[] args) {
		List<PluginFieldMetadata> pluginFieldMetadatas = BasePluginUtil.getPluginFieldMetadatas(ShellPlugin.class);
		System.out.println(JSON.toJSONString(pluginFieldMetadatas));
	}
}
