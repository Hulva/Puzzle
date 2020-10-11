package hulva.luva.wxx.platform.puzzle.backend.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
@Entity
@Table(name = "t_plugin_field")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PluginFieldEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;
	String value;
	Boolean required;
	String label;
	String format;
	String placeholder;
	Integer sort;
	Boolean readOnly;
	String source;
	String data;
	String name;

	String defaultData; // 当 job 中对当前值进行了修改，template 中设置的值将被当作 默认值
	Integer flag; // flag @Link{ActionFlag}
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plugin_id", nullable = false)
	PluginEntity plugin;
}
