package hulva.luva.wxx.platform.puzzle.backend.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author Hulva Luva.H
 * @since 2019年3月21日
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_plugin")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PluginEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;
	String name;
	String aliasName;
	String jarName;
	/**
	 * @see PluginType
	 */
	String type; // SERVICE RESTFUL OTHER
	Integer version;
	
	@OneToMany(mappedBy = "plugin", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
	List<PluginFieldEntity> pluginFields;
}
