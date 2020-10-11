package hulva.luva.wxx.platform.puzzle.backend.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import hulva.luva.wxx.platform.puzzle.backend.entity.*;

public class ExecuteJobModel {
    private Map<Integer, List<TemplateStructEntity>> templateChildStructs;
    private Map<String, PluginEntity> plugins;
    private Map<String, List<PluginFieldEntity>> pluginFields;
    private Map<String, String> templateStructDatas;
    private Map<String, String> context;
    private String type;

    public ExecuteJobModel(List<TemplateStructEntity> templateStructs, List<TemplateStructDataEntity> templateStructDatasDataEntities,
                           List<FlowStructDataEntity> pluginStructDatasFromJob, List<PluginEntity> plugins, List<PluginFieldEntity> pluginFields,
                           List<ImportedConfig> importedConfigs, String type) {
        this.type = type;
        initTemplateStructs(templateStructs);
        initTemplateStruceDatas(templateStructDatasDataEntities, pluginStructDatasFromJob);
        initPlugins(plugins);
        initPluginFields(pluginFields);
        initContext(importedConfigs);
    }

    private void initContext(List<ImportedConfig> importedConfigs) {
        if (this.context == null) {
            this.context = new HashMap<>();
        }
        importedConfigs.forEach(config -> this.context.put(config.getName(), config.getValue()));
    }

    private void initTemplateStructs(List<TemplateStructEntity> templateStructs) {
        templateChildStructs = templateStructs.stream() // -1 代表 root
                .collect(Collectors.groupingBy(m -> m.getParent() == null ? -1 : m.getParent().getId()));
    }

    private void initTemplateStruceDatas(List<TemplateStructDataEntity> list, List<FlowStructDataEntity> pluginStructDatasFromJob) {
        templateStructDatas = list.stream().collect(
                Collectors.toMap(v -> v.getTemplateStruct().getId() + ":" + v.getName().toUpperCase(), TemplateStructDataEntity::getValue));
        if (pluginStructDatasFromJob != null && pluginStructDatasFromJob.size() > 0) {
            // 如果存在job中修改的数据，将覆盖template中的值
            pluginStructDatasFromJob.forEach(jobStructData -> templateStructDatas
                    .put(jobStructData.getTemplateStruct().getId() + ":" + jobStructData.getName().toUpperCase(), jobStructData.getValue()));
        }
    }

    private void initPlugins(List<PluginEntity> list) {
        plugins = list.stream().collect(Collectors.toMap(x -> x.getName() + ":" + x.getVersion(), Function.identity(), (nameVersion1, nameVersion2) -> nameVersion1));
    }

    private void initPluginFields(List<PluginFieldEntity> list) {
        pluginFields = list.stream().collect(Collectors.groupingBy(x -> x.getPlugin().getId().toString()));
    }

    public PluginEntity getPlugin(String pluginNameAndVersion) {
        return plugins.get(pluginNameAndVersion);
    }

    public List<PluginFieldEntity> getPluginFields(String pluginNameAndVersion) {
        return pluginFields.get(pluginNameAndVersion);
    }

    public List<TemplateStructEntity> getChildStucts(Integer structId) {
        return templateChildStructs.get(structId);
    }

    public String getFieldData(int structId, String name) {
        return templateStructDatas.get(structId + ":" + name.toUpperCase());
    }

    public Map<String, String> getContext() {
        return context;
    }

    public String getType() {
        return type;
    }
}
