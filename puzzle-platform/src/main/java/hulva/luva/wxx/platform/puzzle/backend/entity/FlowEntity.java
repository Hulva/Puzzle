package hulva.luva.wxx.platform.puzzle.backend.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
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
 * @date 2019年3月16日
 * @description
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_flow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlowEntity {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	String id;
	String name;
	String executeNodes;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
	TemplateEntity template;
	
	String crontab;
	Boolean enable;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "t_flow_config", joinColumns = {
			@JoinColumn(name = "flow_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "config_id", referencedColumnName = "id") })
	List<ConfigEntity> configs;
}
