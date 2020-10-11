package hulva.luva.wxx.platform.puzzle.backend.model;

import lombok.Data;

/**
 * @author Hulva Luva.H
 * @date 2019年5月14日
 * @description
 */
@Data
public class PluginData {
    private String field;
    private String value;

    private Integer flag; // flag @Link{ActionFlag}
}
