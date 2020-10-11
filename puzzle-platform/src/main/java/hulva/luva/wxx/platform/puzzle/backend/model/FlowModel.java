package hulva.luva.wxx.platform.puzzle.backend.model;

import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Foster.X.Li
 * @author Hulvaaa Luva.H
 * @date 2019年5月22日
 * @changed 2019-07-17 by Hulva Luva.H
 * @description
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class FlowModel extends FlowEntity {
	private String templateName;
	private String templateType;
	private int status;
	private Boolean running;
	private Boolean lastStatus;
}
