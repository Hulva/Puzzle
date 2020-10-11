package hulva.luva.wxx.platform.puzzle.backend.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;

import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.puzzle.backend.constants.Constants;
import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.FlowStructDataEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginFieldEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateStructDataEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateStructEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.ExecuteJobModel;
import hulva.luva.wxx.platform.puzzle.backend.repository.FlowRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.FlowStructDataRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.PluginFieldRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.PluginRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructDataRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructRepository;

/**
 * @author fl76
 */
@Service
public class JobService {

	@Autowired
    FlowRepository flowRepository;
    @Autowired
    TemplateStructRepository templateStructRepository;
    @Autowired
    TemplateStructDataRepository templateStructDataRepository;
    @Autowired
    FlowStructDataRepository flowStructDataRepository;
    @Autowired
    PluginRepository pluginRepository;
    @Resource
    PluginFieldRepository pluginFieldRepository;

    public FlowEntity findById(String id) {
        return flowRepository.findById(id).get();
    }

    public FlowEntity findByName(String name) {
        return flowRepository.findOne(Example.of(FlowEntity.builder().name(name).build())).get();
    }


    public List<FlowEntity> findAll() {
        return flowRepository.findAll(Example.of(FlowEntity.builder().template(TemplateEntity.builder().type("JOB").build()).build()));
    }


    public ExecuteJobModel initJob(final FlowEntity job) throws Exception {
        Assert.notNull(job, "can not find job:" + job);
        Assert.notNull(job.getTemplate(), "Job数据未配置Template：" + job);
        TemplateEntity template = job.getTemplate();
        Assert.notNull(template, "Job数据未配置Template：" + job);
        List<TemplateStructEntity> templateStructs = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder().template(TemplateEntity.builder().id(template.getId()).build()).build()));
        Assert.notNull(templateStructs, "无法找到template对应的structs数据：" + job.getTemplate());
        List<TemplateStructDataEntity> templateStructDatasDataEntities = templateStructDataRepository.findAll(Example.of(TemplateStructDataEntity.builder().template(TemplateEntity.builder().id(template.getId()).build()).build()));
        List<FlowStructDataEntity> pluginStructDatasFromJob = flowStructDataRepository.findAll(Example.of(FlowStructDataEntity.builder().flow(FlowEntity.builder().id(job.getId()).build()).build()));
        Assert.notNull(templateStructDatasDataEntities, "无法找到template对应的structsdata数据：" + job.getTemplate());
        List<PluginEntity> plugins = new ArrayList<PluginEntity>();
        List<PluginFieldEntity> pluginFields = new ArrayList<PluginFieldEntity>();
        for (TemplateStructEntity entity : templateStructs) {
        	PluginEntity pluginEntity = pluginRepository.findOne(Example.of(PluginEntity.builder()
        			.name(entity.getPluginName())
        			.version(entity.getPluginVersion())
        			.build())).get();
        	if (!"SYSTEM".equals(pluginEntity.getJarName())) {
                File jar = Paths.get(Constants.PLUGIN_JAR_PATH_PRFIX, pluginEntity.getJarName()).toFile();
                if(!jar.exists()) {
                	throw new RuntimeException("Jar file not exists!");
                }
        	}
        	plugins.add(pluginEntity);
        	List<PluginFieldEntity> pluginFieldEntity = pluginFieldRepository.findAll(Example.of(PluginFieldEntity.builder()
        			.plugin(pluginEntity)
        			.build()));
        	pluginFields.addAll(pluginFieldEntity);
        }
        Assert.notEmpty(plugins, "无法找到对应的插件数据：" + job.getTemplate());
//        List<ImportedConfig> importedConfigs = templateConfigRepository.findAll(Example.of(TemplateConfigEntity.builder().templateId(job.getTemplate().getId()).build()))
//        		.stream().map(templateConfig -> ImportedConfig.builder().configId(templateConfig.getConfigId()).build()).collect(Collectors.toList());
//        List<FlowConfigEntity> importedConfigsByJob = flowConfigRepository.findAll(Example.of(FlowConfigEntity.builder().id(job.getId()).build()));
//        importedConfigs.addAll(importedConfigsByJob);
        // TODO
        return new ExecuteJobModel(templateStructs, templateStructDatasDataEntities, pluginStructDatasFromJob, plugins, pluginFields, null/*importedConfigs*/, template.getType());
    }

    public static PluginConfig buildPluginConfig(ExecuteJobModel jobModel, TemplateStructEntity struct, String key) {
        PluginEntity plugin = jobModel.getPlugin(struct.getPluginName() + ":" + struct.getPluginVersion());
        List<PluginFieldEntity> pluginFieldList = jobModel.getPluginFields(struct.getPluginName() + ":" + struct.getPluginVersion());

        PluginConfig pluginConfig = new PluginConfig();
        pluginConfig.setKey(key != null ? key + "." + struct.getName() : struct.getName());
        pluginConfig.setName(struct.getName());
        pluginConfig.setVersion(plugin.getVersion() + "");
        if (!"SYSTEM".equals(plugin.getJarName())) {
            pluginConfig.setPath(Paths.get(Constants.PLUGIN_JAR_PATH_PRFIX, plugin.getJarName()).toAbsolutePath().toString());
        }
        pluginConfig.setClassPath(plugin.getName());

        TreeMap<String, String> configs = new TreeMap<String, String>();
        ArrayList<Object> plugins = new ArrayList<Object>();
        if (pluginFieldList != null) {
            pluginFieldList.forEach((f) -> {
                if (!f.getFormat().equalsIgnoreCase("plugins")) {
                    String value = jobModel.getFieldData(struct.getId(), f.getValue());
                    if (value == null) {
                        configs.put(f.getValue(), "");
                    } else {
                        configs.put(f.getValue(), value);
                    }
                } else {
                    List<TemplateStructEntity> childStructs = jobModel.getChildStucts(struct.getId());
                    if (childStructs != null) {
                        childStructs.sort(new Comparator<TemplateStructEntity>() {
                            @Override
                            public int compare(TemplateStructEntity o1, TemplateStructEntity o2) {
                                if (o1.getOrder() > o2.getOrder()) {
                                    return 1;
                                } else {
                                    return o1.getOrder() == o2.getOrder() ? 0 : -1;
                                }
                            }
                        });
                        childStructs.forEach((childStruct) -> {
                            PluginConfig fieldPluginConfig = buildPluginConfig(jobModel, childStruct, key != null ? key + "." + struct.getName() : struct.getName());
                            plugins.add(fieldPluginConfig);
                        });
                    }
                }
            });
        }
        configs.put("plugins", JSON.toJSONString(plugins));
        pluginConfig.setConfig(configs);
        return pluginConfig;
    }
}
