package hulva.luva.wxx.platform.puzzle.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.FlowStructDataEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateStructEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.JobRequestModel;
import hulva.luva.wxx.platform.puzzle.backend.model.TemplatePluginTreeRequest;
import hulva.luva.wxx.platform.puzzle.backend.repository.FlowRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.FlowStructDataRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructRepository;
import hulva.luva.wxx.platform.puzzle.backend.service.JobRegister;
import hulva.luva.wxx.platform.puzzle.execute.job.ExecuteJob;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hulva Luva.H
 * @date 2019年3月15日
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/api/flow")
public class FlowController extends AbstractBaseController {
	@Autowired
	private ExecuteJob execute;
	@Autowired
	JobRegister register;
	@Autowired
	FlowRepository flowRepository;
	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	FlowStructDataRepository flowStructDataRepository;
	@Autowired
	TemplateStructRepository templateStructRepository;
	
	@GetMapping("/list")
	public Response<List<FlowEntity>> listJobs() {
		List<FlowEntity> flowModels = flowRepository.findAll();
		return SUCCESS(flowModels);
	}

	@GetMapping("/get/{id}")
	public Response<Optional<FlowEntity>> getFlow(@PathVariable String id) {
		return SUCCESS(flowRepository.findById(id));
	}

	@GetMapping("/delete/{id}")
	public Response<Void> delete(@PathVariable String id) throws Exception {
		flowRepository.deleteById(id);
		return SUCCESS(null, "Flow [" + id + "] has been deleted!");
	}

	@GetMapping("/enable/{id}")
	public Response<Void> enable(@PathVariable String id) throws Exception {
		FlowEntity flow = flowRepository.findById(id).get();
		flow.setEnable(!flow.getEnable());
		flowRepository.save(flow);
		return SUCCESS(null, "[" + flow.getName() + "] has been " + (flow.getEnable() ? "enabled" : "disabled") + "!");
	}

	@PostMapping("/put")
	@Transactional
	public Response<FlowEntity> put(@RequestBody JobRequestModel requsetBody) throws Exception {
		FlowEntity exsitsNameFlow = flowRepository.findOne(Example.of(FlowEntity.builder().name(requsetBody.getName()).build())).get();
		FlowEntity flow = new FlowEntity();
		if (!"".equals(requsetBody.getId()) && requsetBody.getId() != null) {
			if (exsitsNameFlow != null && !exsitsNameFlow.getId().equals(requsetBody.getId())) {
				return FAIL("Name already exist!");
			}
			flow.setId(requsetBody.getId());
			flow.setName(requsetBody.getName());
			flow.setExecuteNodes(requsetBody.getExecuteNodes());
			flow.setTemplate(requsetBody.getTemplateId().equals(exsitsNameFlow.getName()) ? exsitsNameFlow.getTemplate()
					: templateRepository.findById(requsetBody.getTemplateId()).get());
			flow.setCrontab(requsetBody.getCrontab());
			flow.setEnable(requsetBody.getEnable());
			flowRepository.save(flow);
		} else {
			if (exsitsNameFlow != null) {
				return FAIL("Name already exist!");
			}
			flow.setName(requsetBody.getName());
			flow.setTemplate(templateRepository.findById(requsetBody.getTemplateId()).get());
			flow.setCrontab(requsetBody.getCrontab());
			flow.setEnable(requsetBody.getEnable());
			flowRepository.save(flow);
		}

		TemplateEntity template = new TemplateEntity();
		template.setName(requsetBody.getTemplateName());
		template.setType(requsetBody.getTemplateType());
		template.setId(requsetBody.getTemplateId());

		// 2. TemplateStruct
		Map<String, TemplatePluginTreeRequest> pluginTrees = requsetBody.getPluginTree();
		this.saveTemapteStructAndData(pluginTrees, requsetBody.getRootStructId() + "", flow.getId(), null);

//		List<FlowConfigEntity> configEntities = new ArrayList<>();
//		requsetBody.getImportedConfigs().forEach(importedConfig -> {
//			if (importedConfig.getFrom() == 1) {
//				if (importedConfig.getId() != null && importedConfig.getFlag() == 0) { // flag @Link{ActionFlag}
//					flowConfigRepository.deleteById(importedConfig.getId());
//				} else if (importedConfig.getId() == null) {
//					FlowConfigEntity config = new FlowConfigEntity();
//					config.setFlowId(flow.getId());
//					config.setConfigId(importedConfig.getConfigId());
//					configEntities.add(config);
//				}
//			}
//		});
//		flowConfigRepository.saveAll(configEntities);
		return SUCCESS(flow);
	}

	private void saveTemapteStructAndData(Map<String, TemplatePluginTreeRequest> pluginTrees, String treeNodeKey,
			String flowId, Integer parentId) {
		TemplatePluginTreeRequest pluginTreeNode = pluginTrees.get(treeNodeKey);
		// flag @Link{ActionFlag}
		if (pluginTreeNode.getFlag() == -1 || pluginTreeNode.getFlag() == null) {
			return;
		}
		List<FlowStructDataEntity> flowStructDataEntities = new ArrayList<>();
		String[] structIdField = pluginTreeNode.getStructId().split("#");
		List<FlowStructDataEntity> existTemplateStructDataEntities = flowStructDataRepository
				.findAll(Example.of(FlowStructDataEntity.builder()
						.flow(FlowEntity.builder().id(flowId).build())
						.templateStruct(TemplateStructEntity.builder().id(Integer.parseInt(structIdField[0])).build())
						.build()));
		Map<String, Integer> fieldValueMap = new HashMap<>();
		for (FlowStructDataEntity flowStructDataEntity : existTemplateStructDataEntities) {
			fieldValueMap.put(flowStructDataEntity.getName(), flowStructDataEntity.getId());
		}
		pluginTreeNode.getPluginFields().forEach(fieldData -> {
			if (fieldData.getFlag() != null) { // flag @Link{ActionFlag}
				if (fieldData.getFlag() == 1) { // add or edit
					FlowStructDataEntity flowStructDataEntity = new FlowStructDataEntity();
					flowStructDataEntity.setName(fieldData.getField());
					flowStructDataEntity.setFlow(flowRepository.findById(flowId).get());
					flowStructDataEntity.setTemplateStruct(templateStructRepository.findById(Integer.parseInt(structIdField[0])).get());
					flowStructDataEntity.setValue(fieldData.getValue());
					flowStructDataEntity.setId(fieldValueMap.get(fieldData.getField()));
					flowStructDataEntities.add(flowStructDataEntity);
				} else if (fieldData.getFlag() == 0 && fieldValueMap.get(fieldData.getField()) != null) { // delete
					flowStructDataRepository.deleteById(fieldValueMap.get(fieldData.getField()));
				}
			}
		});
		flowStructDataRepository.saveAll(flowStructDataEntities);

		List<String> childTreeNodeKeys = pluginTreeNode.getChild();
		for (String childTreeNodeKey : childTreeNodeKeys) {
			this.saveTemapteStructAndData(pluginTrees, childTreeNodeKey, flowId, Integer.parseInt(structIdField[0]));
		}
	}

	/**
	 * 运行 Flow
	 */
	@GetMapping("/run/{flowId}")
	public Response<Boolean> runOrStart(@PathVariable String flowId, @RequestParam(required = false) String host)
			throws Exception {
//		if (theLatestFlowTrace.isPresent() && "RUNNING".endsWith(theLatestFlowTrace.get().getStatus())) {
//			return SUCCESS(null, "Job is running...");
//		}
		FlowEntity flow = flowRepository.findById(flowId).get();

		if (host == null) {
			switch (flow.getTemplate().getType()) {
			case "JOB":
				Thread t = new Thread(() -> {
//					FlowTrace currenTrace = new FlowTrace(IPHelp.getIP(), flowId, "RUNNING", System.currentTimeMillis(), "Mannually execute");
					try {
						execute.execute(flow);
					} catch (Exception e) {
						log.error("run once error", e);
					} finally {
						try {
							Thread.sleep(5000);
							log.info("Manually Execute flowjob done: " + flow);
						} catch (InterruptedException ignore) {
							ignore.printStackTrace();
						}
					}
				});
				t.setDaemon(false);
				t.start();
				return SUCCESS(null, "success");
			default:
				throw new IllegalStateException("Unsupported value: " + flow.getTemplate().getType());
			}
		} else {
			return SUCCESS(true, "Job will runninng in " + host);
		}
	}
}
