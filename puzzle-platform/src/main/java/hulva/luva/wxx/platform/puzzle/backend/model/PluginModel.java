package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.List;

import hulva.luva.wxx.platform.puzzle.backend.entity.PluginEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginFieldEntity;
import lombok.Data;

/**
 * @author Hulva Luva.H
 * @date 2019年5月10日
 * @description
 */
@Data
public class PluginModel {
    PluginEntity plugin; // root plugin
    List<PluginFieldEntity> pluginFields;

    List<String> childs;
    private Integer structId;
    private String structName;
}
