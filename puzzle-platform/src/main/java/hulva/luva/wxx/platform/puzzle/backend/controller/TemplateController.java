package hulva.luva.wxx.platform.puzzle.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hulva.luva.wxx.platform.puzzle.backend.entity.ConfigEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.FlowStructDataEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginFieldEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateStructDataEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateStructEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.CreateStructRequest;
import hulva.luva.wxx.platform.puzzle.backend.model.PluginModel;
import hulva.luva.wxx.platform.puzzle.backend.model.TemplatePluginTreeRequest;
import hulva.luva.wxx.platform.puzzle.backend.model.TemplatePluginTreeResponse;
import hulva.luva.wxx.platform.puzzle.backend.model.TemplateRequestModel;
import hulva.luva.wxx.platform.puzzle.backend.model.TemplateResponseModel;
import hulva.luva.wxx.platform.puzzle.backend.repository.ConfigRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.FlowRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.FlowStructDataRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.PluginFieldRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.PluginRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructDataRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructRepository;

/**
 * @author Hulva Luva.H
 * @since 2019年4月4日
 * @description
 */
@RestController
@RequestMapping("/api/template")
public class TemplateController extends AbstractBaseController {
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    PluginRepository pluginRepository;
    @Autowired
    PluginFieldRepository pluginFieldRepository;
    @Autowired
    FlowStructDataRepository flowStructDataRepository;
    @Autowired
    TemplateStructRepository templateStructRepository;
    @Autowired
    TemplateStructDataRepository templateStructDataRepository;
    @Autowired
    ConfigRepository configRepository;
    @Autowired
    FlowRepository flowRepository;

    @GetMapping("/list")
    public Response<List<TemplateEntity>> list() {
        return SUCCESS(templateRepository.findAll());
    }

    @GetMapping("/get/{id}")
    public Response<TemplateResponseModel> getById(@PathVariable String id, @RequestParam(required = false) Boolean isJob,
                                                   @RequestParam(required = false) String jobId) {
        TemplateResponseModel result = new TemplateResponseModel();

        TemplateEntity templateEntity = templateRepository.findById(id).get();
        result.setId(id);
        result.setType(templateEntity.getType());
        result.setName(templateEntity.getName());

        TemplatePluginTreeResponse pluginTree = new TemplatePluginTreeResponse();

        TemplateStructEntity structEntity = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder().template(templateEntity).build()))
        		.stream().filter(templateStructData -> templateStructData.getParent() == null).findFirst().get();
        PluginEntity pluginEntity = pluginRepository.findOne(Example.of(PluginEntity.builder()
        		.name(structEntity.getPluginName())
        		.version(structEntity.getPluginVersion())
        		.build())).get();
        List<PluginFieldEntity> pluginFieldEntities = pluginFieldRepository.findAll(Example.of(PluginFieldEntity.builder()
        		.plugin(pluginEntity)
        		.build()));
        List<TemplateStructDataEntity> pluginStructDatas =
                templateStructDataRepository.findAll(Example.of(TemplateStructDataEntity.builder()
                		.template(TemplateEntity.builder().id(id).build())
                		.templateStruct(structEntity)
                		.build()));
        Map<String, String> fieldDatas = new HashMap<>();
        for (TemplateStructDataEntity templateStructDataEntity : pluginStructDatas) {
            fieldDatas.put(templateStructDataEntity.getName(), templateStructDataEntity.getValue());
        }
        Map<String, String> fieldDatasFromFlow = new HashMap<>();
        if (isJob != null && isJob && jobId != null) {
            List<FlowStructDataEntity> pluginStructDatasFromJob =
                    flowStructDataRepository.findAll(Example.of(FlowStructDataEntity.builder()
                		.flow(FlowEntity.builder().id(jobId).build())
                		.templateStruct(structEntity)
                		.build()));
            for (FlowStructDataEntity flowStructDataEntity : pluginStructDatasFromJob) {
                fieldDatasFromFlow.put(flowStructDataEntity.getName(), flowStructDataEntity.getValue());
            }
        }
        pluginFieldEntities.forEach(pluginFieldEntity -> {
            if (isJob != null && isJob) {
                pluginFieldEntity.setDefaultData(fieldDatas.get(pluginFieldEntity.getValue()));
            } else {
                pluginFieldEntity.setData(fieldDatas.get(pluginFieldEntity.getValue()));
            }
            if (fieldDatasFromFlow.containsKey(pluginFieldEntity.getValue())) {
                pluginFieldEntity.setData(fieldDatasFromFlow.get(pluginFieldEntity.getValue()));
            }
        });
        pluginTree.setPluginFields(pluginFieldEntities);

        pluginTree.setPlugin(pluginEntity);

        // get childs
        List<TemplateStructEntity> childStructs = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder()
        		.template(templateEntity)
        		.parent(structEntity)
        		.build()));
        List<String> childStructIds = new ArrayList<>();
        for (TemplateStructEntity templateStructEntity : childStructs) {
            childStructIds.add(templateStructEntity.getId() + (templateStructEntity.getBelongField() != null ? "#" + templateStructEntity.getBelongField() : ""));
        }
        pluginTree.setChildStructIds(childStructIds);

        List<ConfigEntity> importedConfigs = templateRepository.findById(id).get().getConfigs();
        if (isJob != null && isJob && jobId != null) {
            FlowEntity importedConfigsByJob = flowRepository.findById(jobId).get();
            importedConfigs.addAll(importedConfigsByJob.getConfigs());
        }
        result.setImportedConfigs(importedConfigs);

        pluginTree.setName(structEntity.getName());
        pluginTree.setStructId(structEntity.getId());

        result.setTemplateStructAndData(pluginTree);

        return SUCCESS(result);
    }

    @GetMapping("/getPluginData/{template}/{struct}")
    public Response<PluginModel> getPluginData(@PathVariable String template, @PathVariable Integer struct,
                                               @RequestParam(required = false) Boolean isJob, @RequestParam(required = false) String jobId) {
        TemplateStructEntity structEntity = templateStructRepository.findById(struct).get();

        PluginModel pluginModel = new PluginModel();
        PluginEntity pluginEntity = pluginRepository.findOne(Example.of(PluginEntity.builder()
        		.name(structEntity.getPluginName())
        		.version(structEntity.getPluginVersion())
        		.build())).get();
        pluginModel.setPlugin(pluginEntity);

        List<TemplateStructDataEntity> pluginStructDatas =
                templateStructDataRepository.findAll(Example.of(TemplateStructDataEntity.builder()
                		.template(TemplateEntity.builder().id(template).build())
                		.templateStruct(structEntity)
                		.build()));
        Map<String, String> fieldDatas = new HashMap<>();
        for (TemplateStructDataEntity templateStructDataEntity : pluginStructDatas) {
            fieldDatas.put(templateStructDataEntity.getName(), templateStructDataEntity.getValue());
        }
        Map<String, String> fieldDatasFromFlow = new HashMap<>();
        if (isJob != null && isJob && jobId != null) {
            List<FlowStructDataEntity> pluginStructDatasFromFlow =
                    flowStructDataRepository.findAll(Example.of(FlowStructDataEntity.builder()
                    		.flow(FlowEntity.builder().id(jobId).build())
                    		.templateStruct(structEntity)
                    		.build()));
            for (FlowStructDataEntity flowStructDataEntity : pluginStructDatasFromFlow) {
                fieldDatasFromFlow.put(flowStructDataEntity.getName(), flowStructDataEntity.getValue());
            }
        }
        List<PluginFieldEntity> pluginFieldEntities = pluginFieldRepository.findAll(Example.of(PluginFieldEntity.builder()
        		.plugin(pluginEntity)
        		.build()));
        pluginFieldEntities.forEach(pluginFieldEntity -> {
            if (isJob != null && isJob) {
                pluginFieldEntity.setDefaultData(fieldDatas.get(pluginFieldEntity.getValue()));
            } else {
                pluginFieldEntity.setData(fieldDatas.get(pluginFieldEntity.getValue()));
            }
            if (fieldDatasFromFlow.containsKey(pluginFieldEntity.getValue())) {
                pluginFieldEntity.setData(fieldDatasFromFlow.get(pluginFieldEntity.getValue()));
            }
        });
        pluginModel.setPluginFields(pluginFieldEntities);

        List<TemplateStructEntity> childStructEntities = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder()
        		.template(TemplateEntity.builder().id(template).build())
        		.parent(TemplateStructEntity.builder().id(struct).build())
        		.build()));
        List<String> childStructIds = new ArrayList<>();
        for (TemplateStructEntity childStructEntity : childStructEntities) {
            childStructIds.add(childStructEntity.getId() + (childStructEntity.getBelongField() != null ? "#" + childStructEntity.getBelongField() : ""));
        }
        pluginModel.setChilds(childStructIds);

        pluginModel.setStructId(structEntity.getId());
        pluginModel.setStructName(structEntity.getName());

        return SUCCESS(pluginModel);
    }

    @PostMapping("/put")
    public Response<TemplateEntity> put(@RequestBody TemplateRequestModel requsetBody) {
        // name already exist
        // 1. 更新 template
        TemplateEntity template = new TemplateEntity();
        template.setName(requsetBody.getName());
        template.setType(requsetBody.getTemplateType());
        if (!"".equals(requsetBody.getId()) && requsetBody.getId() != null) {
        	Example<TemplateEntity> example = Example.of(TemplateEntity.builder().name(requsetBody.getName()).build());
            TemplateEntity exisTemplateEntity = templateRepository.findOne(example).get();
            if (exisTemplateEntity != null && !exisTemplateEntity.getId().equals(requsetBody.getId())) {
                return FAIL("Name already exist!");
            }
            template.setId(requsetBody.getId());
            templateRepository.save(template);
        } else {
            TemplateEntity exisTemplateEntity = templateRepository.findOne(Example.of(TemplateEntity.builder().name(requsetBody.getName()).build())).get();
            if (exisTemplateEntity != null) {
                return FAIL("Name already exist!");
            }
            template.setId(UUID.randomUUID().toString());
            templateRepository.save(template);
        }

        // 2. TemplateStruct
        Map<String, TemplatePluginTreeRequest> pluginTrees = requsetBody.getPluginTree();
        // node been deleted. treeNode.getFlag see @Link{ActionFlag}
        List<TemplatePluginTreeRequest> tptr =
                pluginTrees.values().stream().filter(treeNode -> treeNode.getFlag() == 0).collect(Collectors.toList());
        for (TemplatePluginTreeRequest templatePluginTreeRequest : tptr) {
            String[] structIdField = templatePluginTreeRequest.getStructId().split("#");
            TemplateStructEntity structEntity = templateStructRepository.findById(Integer.parseInt(structIdField[0])).get();
            this.executeDeleteTreeNode(template, structEntity);
        }

        this.saveTemapteStructAndData(pluginTrees, requsetBody.getRootStructId() + "", 0, template.getId(), null);

//        List<TemplateConfigEntity> configEntities = new ArrayList<>();
//        requsetBody.getImportedConfigs().forEach(importedConfig -> {
//            if (importedConfig.getFlag() == 0) { // flag @Link{ActionFlag}
//                configRepository.deleteById(importedConfig.getId());
//            } else if (importedConfig.getId() == null) {
//                TemplateConfigEntity config = new TemplateConfigEntity();
//                config.setTemplateId(template.getId());
//                config.setConfigId(importedConfig.getConfigId());
//                configEntities.add(config);
//            }
//        });
//        templateConfigRepository.saveAll(configEntities);
        return SUCCESS(template);
    }

    private void executeDeleteTreeNode(TemplateEntity template, TemplateStructEntity structEntity) {
        if (structEntity != null) {
            List<TemplateStructEntity> childs = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder()
            		.template(template)
            		.parent(structEntity)
            		.build()));
            		
            if (childs != null) {
                for (TemplateStructEntity templateStructEntity : childs) {
                    this.executeDeleteTreeNode(template, templateStructEntity);
                }
            }
            templateStructDataRepository.delete(TemplateStructDataEntity.builder().templateStruct(structEntity).build());
            configRepository.delete(ConfigEntity.builder().group(structEntity.getId() + "").build());
            templateStructRepository.deleteById(structEntity.getId());
        }

    }

    @GetMapping("/delete/{templateId}")
    public Object delete(@PathVariable String templateId) {
    	Example<FlowEntity> example = Example.of(FlowEntity.builder().template(TemplateEntity.builder().id(templateId).build()).build());
        List<FlowEntity> jobUseTheTemplate = flowRepository.findAll(example);
        if (jobUseTheTemplate.size() > 0) {
            return FAIL(jobUseTheTemplate.stream().map(FlowEntity::getName).collect(Collectors.toList()));
        }
        Example<TemplateStructEntity> example2 = Example.of(TemplateStructEntity.builder().template(TemplateEntity.builder().id(templateId).build()).build());
        List<TemplateStructEntity> templateStructEntities = templateStructRepository.findAll(example2);
        for (TemplateStructEntity templateStructEntity : templateStructEntities) {
            configRepository.delete(ConfigEntity.builder().group(templateStructEntity.getId() + "").build());
        }
        templateStructDataRepository.delete(TemplateStructDataEntity.builder().template(TemplateEntity.builder().id(templateId).build()).build());
        templateStructRepository.delete(TemplateStructEntity.builder().template(TemplateEntity.builder().id(templateId).build()).build());
        templateRepository.deleteById(templateId);
        return SUCCESS(templateRepository.findAll());
    }

    @PostMapping("struct/create")
    public Response<Map<String, Object>> createStruct(@RequestBody CreateStructRequest createStruct) {
        if (createStruct.getTemplateId() == null) {
            TemplateEntity template = new TemplateEntity();
            template.setName(createStruct.getTemplateName());
            template.setType(createStruct.getTemplateType());
            template.setId(UUID.randomUUID().toString());
            templateRepository.save(template);
            createStruct.setTemplateId(template.getId());
        }
        TemplateStructEntity treeNodePluginStruct = new TemplateStructEntity();
        treeNodePluginStruct.setName(createStruct.getStructName());
        treeNodePluginStruct.setPluginName(createStruct.getPluginName());
        treeNodePluginStruct.setPluginVersion(createStruct.getPluginVersion());
        treeNodePluginStruct.setTemplate(templateRepository.findById(createStruct.getTemplateId()).get());
        treeNodePluginStruct.setParent(templateStructRepository.findById(createStruct.getParent()).get());

        templateStructRepository.save(treeNodePluginStruct);
        List<PluginFieldEntity> fields = pluginFieldRepository.findAll(Example.of(PluginFieldEntity.builder()
// TODO       		.plugin(plugin)
        		.build()));
        		
        Map<String, Object> response = new HashMap<>();
        response.put("struct", treeNodePluginStruct);
        response.put("pluginFields", fields);
        return SUCCESS(response);
    }

    @GetMapping("checkNameExist/{templateName}")
    public Response<Void> checkTemplateNameExist(@PathVariable String templateName) {
        TemplateEntity exisTemplateEntity = templateRepository.findOne(Example.of(TemplateEntity.builder().name(templateName).build())).get();
        if (exisTemplateEntity != null) {
            return FAIL("Name already exist!");
        }
        return SUCCESS();
    }


    private void saveTemapteStructAndData(Map<String, TemplatePluginTreeRequest> pluginTrees, String structId, Integer order,
                                          String templateID, Integer parentId) {
        TemplatePluginTreeRequest pluginTreeNode = pluginTrees.get(structId);

        if (pluginTreeNode == null || pluginTreeNode.getFlag() == -1 || pluginTreeNode.getFlag() == null) { // flag @Link{ActionFlag}
            return;
        }

        TemplateStructEntity treeNodePluginStruct = new TemplateStructEntity();
        treeNodePluginStruct.setName(pluginTreeNode.getStructName());
        treeNodePluginStruct.setPluginName(pluginTreeNode.getPlugin().getName());
        treeNodePluginStruct.setPluginVersion(pluginTreeNode.getPlugin().getVersion());
        treeNodePluginStruct.setTemplate(templateRepository.findById(templateID).get());
        treeNodePluginStruct.setParent(templateStructRepository.findById(parentId).get());
        treeNodePluginStruct.setId(Integer.parseInt(pluginTreeNode.getStructId().split("#")[0]));
        treeNodePluginStruct.setOrder(order);

        String[] structIdField = structId.split("#");
        if (structIdField.length > 1) {
            treeNodePluginStruct.setBelongField(structIdField[1]);
        }

        templateStructRepository.save(treeNodePluginStruct);

        List<TemplateStructDataEntity> templateStructDataEntities = new ArrayList<>();

        List<TemplateStructDataEntity> existTemplateStructDataEntities =
                templateStructDataRepository.findAll(Example.of(TemplateStructDataEntity.builder()
                		.template(TemplateEntity.builder().id(templateID).build())
                		.templateStruct(treeNodePluginStruct)
                		.build()));
                
        Map<String, Integer> fieldValueMap = new HashMap<>();
        for (TemplateStructDataEntity templateStructDataEntity : existTemplateStructDataEntities) {
            fieldValueMap.put(templateStructDataEntity.getName(), templateStructDataEntity.getId());
        }
        pluginTreeNode.getPluginFields().forEach(fieldData -> {
            if (fieldData.getFlag() != null && fieldData.getFlag() == 1) { // flag @Link{ActionFlag}
                TemplateStructDataEntity templateStructDataEntity = new TemplateStructDataEntity();
                templateStructDataEntity.setName(fieldData.getField());
                templateStructDataEntity.setTemplate(templateRepository.findById(templateID).get());
                templateStructDataEntity.setTemplateStruct(templateStructRepository.findById(treeNodePluginStruct.getId()).get());
                templateStructDataEntity.setValue(fieldData.getValue());
                templateStructDataEntity.setId(fieldValueMap.get(fieldData.getField()));
                templateStructDataEntities.add(templateStructDataEntity);
            }
        });
        templateStructDataRepository.saveAll(templateStructDataEntities);

        List<String> childTreeNodeKeys = pluginTreeNode.getChild();
        for (int i = 0; i < childTreeNodeKeys.size(); i++) {
            this.saveTemapteStructAndData(pluginTrees, childTreeNodeKeys.get(i), i, templateID, treeNodePluginStruct.getId());
        }
    }
}
