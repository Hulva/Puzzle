package hulva.luva.wxx.platform.puzzle.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hulva Luva.H
 * @date 2019年5月25日
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportedConfig {
    private Integer id;
    private Integer configId;
    private String group;
    private String name;
    private String value;
    private Integer from; // 0 template 1 job 标识这条记录来自

    private Integer flag; // flag @Link{ActionFlag}
}
