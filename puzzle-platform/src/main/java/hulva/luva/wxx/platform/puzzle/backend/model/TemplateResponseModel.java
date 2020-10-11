package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.List;

import hulva.luva.wxx.platform.puzzle.backend.entity.ConfigEntity;
import lombok.Data;

@Data
public class TemplateResponseModel {
    private String id;
    private String name;
    private String type;
    private TemplatePluginTreeResponse templateStructAndData;
    private List<ConfigEntity> importedConfigs;
}
