package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 
 * @author Hulva Luva.H
 * @since 2019-11-29
 *
 */
@Data
public class JobRequestModel {
	private String id;
	private String name;
	private String executeNodes;
	private String templateId;
	private String templateName;
	private String templateType;
	private String crontab;
	private Boolean enable;
	private Integer rootStructId;
	private Map<String, TemplatePluginTreeRequest> pluginTree;
	private List<ImportedConfig> importedConfigs;
}
