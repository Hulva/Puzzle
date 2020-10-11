package hulva.luva.wxx.platform.core.plugin.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Hulva Luva.H
 * @since 2019-11-29
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginFieldMetadata {
	Integer id;
	String name;
	String value;
	Boolean required;
	String label;
	String format;
	String placeholder;
	Integer sort;
	Boolean readOnly;
	String source;
	String data;

	String defaultData; // 当 job 中对当前值进行了修改，template 中设置的值将被当作 默认值
}
