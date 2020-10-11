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
 * @author Hulva Luva.H
 * @date 2019年6月11日
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_flow_struct_data")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlowStructDataEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_id")
	FlowEntity flow;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_struct_id")
	TemplateStructEntity templateStruct;
	
	String name;
	String value;
}
