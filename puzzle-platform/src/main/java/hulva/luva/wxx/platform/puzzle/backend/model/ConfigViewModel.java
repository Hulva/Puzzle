package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.List;

import hulva.luva.wxx.platform.puzzle.backend.entity.ConfigEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Frank.X.Lv
 * @date 2019年4月3日
 * @Description
 */
@Data
@AllArgsConstructor
public class ConfigViewModel {
	private String group;
	private List<ConfigEntity> configList;
}
