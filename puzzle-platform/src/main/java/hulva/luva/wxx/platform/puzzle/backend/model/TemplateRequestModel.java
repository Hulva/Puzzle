package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.List;
import java.util.Map;

import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateEntity;
import lombok.Data;

@Data
public class TemplateRequestModel {
    private String id;
    private String name;
    /**
     * @see TemplateEntity.TemplateType
     */
    private String templateType;
    private Integer rootStructId;
    private Map<String, TemplatePluginTreeRequest> pluginTree;
    private List<ImportedConfig> importedConfigs;
}
