package hulva.luva.wxx.platform.puzzle.backend.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

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
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_template")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplateEntity {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	String id;
	String name;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "t_template_config", joinColumns = {
			@JoinColumn(name = "template_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "config_id", referencedColumnName = "id") })
	List<ConfigEntity> configs;
	
	/**
	 * @see TemplateType
	 */
	String type;

	public enum TemplateType {
		SERVICE, RESTFUL, JOB
	}
}
