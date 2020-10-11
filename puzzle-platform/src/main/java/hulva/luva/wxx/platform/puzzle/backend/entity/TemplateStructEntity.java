package hulva.luva.wxx.platform.puzzle.backend.entity;

import javax.persistence.Column;
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
 * @date 2019年4月2日
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_template_struct")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplateStructEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    TemplateEntity template;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    TemplateStructEntity parent;
    
    String pluginName;
    Integer pluginVersion;
    @Column(name = "sort_order")
    Integer order;
    String belongField;
}
