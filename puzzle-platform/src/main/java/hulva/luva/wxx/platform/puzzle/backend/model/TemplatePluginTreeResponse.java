package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.List;

import hulva.luva.wxx.platform.puzzle.backend.entity.PluginEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginFieldEntity;
import lombok.Data;

/**
 * @author Hulva Luva.H
 * @date 2019年5月16日
 * @description
 */
@Data
public class TemplatePluginTreeResponse {
	private PluginEntity plugin;
	private List<PluginFieldEntity> pluginFields;
	private List<String> childStructIds;
	private Integer structId;
	private String name;
}
