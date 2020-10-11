package hulva.luva.wxx.platform.core.plugin.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hulva Luva.H
 * @since 2019年3月21日
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginMetadata {
	Integer id;
	String name;
	String aliasName;
	String jarName;
	String className;
	/**
	 * @see PluginType
	 */
	PluginType type; // SERVICE RESTFUL OTHER
	Integer version;

	public enum PluginType {
		SERVICE, RESTFUL, OTHER
	}
}
