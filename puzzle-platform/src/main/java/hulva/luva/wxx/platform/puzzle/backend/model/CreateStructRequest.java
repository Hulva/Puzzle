package hulva.luva.wxx.platform.puzzle.backend.model;

import lombok.Data;

/**
 * @author Hulva Luva.H
 * @date 2019年5月31日
 * @description
 */
@Data
public class CreateStructRequest {
    private String templateId;
    private String templateName;
    private String templateType;
    private Integer parent;
    private String structName;
    private String pluginName;
    private Integer pluginVersion;
}
