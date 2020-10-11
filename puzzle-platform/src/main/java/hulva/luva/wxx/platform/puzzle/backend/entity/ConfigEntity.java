package hulva.luva.wxx.platform.puzzle.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author Hulva Luva.H
 * @date 2019年4月2日
 * @description
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_config")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;
	@Column(name = "config_group")
	String group;
	@Column(name = "config_name")
	String name;
	@Column(name = "config_value")
	String value;
}
