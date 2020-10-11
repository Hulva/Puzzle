package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.List;

import hulva.luva.wxx.platform.puzzle.backend.entity.PluginEntity;
import lombok.Data;

/**
 * @author Hulva Luva.H
 * @date 2019年5月16日
 * @description
 */
@Data
public class TemplatePluginTreeRequest {
    private String key;
    private String structId;
    private String structName;
    private String parent;
    private List<String> child;
    private PluginEntity plugin;
    private List<PluginData> pluginFields;
    private Integer flag; // flag @Link{ActionFlag}
}
