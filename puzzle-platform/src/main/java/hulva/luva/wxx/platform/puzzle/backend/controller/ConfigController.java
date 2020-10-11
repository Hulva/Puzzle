package hulva.luva.wxx.platform.puzzle.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hulva.luva.wxx.platform.puzzle.backend.entity.ConfigEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.ConfigViewModel;
import hulva.luva.wxx.platform.puzzle.backend.repository.ConfigRepository;

/**
 * @author Frank.X.Lv
 * @date 2019年4月2日
 * @Description
 */
@RequestMapping("/api/config")
@RestController
public class ConfigController extends AbstractBaseController {

    @Resource
    private ConfigRepository configRepository;

    @GetMapping("/list")
    public Object getConfigList() {
        List<ConfigEntity> configlist = configRepository.findAll();
        List<ConfigViewModel> groupList = this.buildConfigViewModelList(configlist);
        return SUCCESS(groupList);
    }

    @GetMapping("/list/group")
    public Response<List<String>> getConfigGroupList() {
        List<String> configGroups = configRepository.findAll().parallelStream().map(ConfigEntity::getGroup).distinct().collect(Collectors.toList());
        return SUCCESS(configGroups);
    }

    @GetMapping("/list/group/{group}")
    public Object getConfigByGroup(@PathVariable("group") String group) {
        List<ConfigEntity> configlist = configRepository.findAll(Example.of(ConfigEntity.builder().group(group).build()));
        return SUCCESS(configlist);
    }

    @GetMapping("/list/group/{group}/{name}")
    public Response<ConfigEntity> getImportedConfigValue(@PathVariable("group") String group, @PathVariable("name") String name) {
        ConfigEntity config = configRepository.findOne(Example.of(ConfigEntity.builder().group(group).name(name).build())).get();
        return SUCCESS(config);
    }

    @PostMapping("/insert")
    public void insertConfig(@RequestBody ConfigEntity config) {
        configRepository.save(config);
    }

    @PostMapping("/update")
    public void updateConfig(@RequestBody ConfigEntity config) {
        configRepository.save(config);
    }

    @GetMapping("/delete/{id}")
    public void deleteConfig(@PathVariable("id") int id) {
        configRepository.deleteById(id);
    }

    @GetMapping("/delete/group/{group}")
    public void deleteGroupConfig(@PathVariable("group") String group) {
        configRepository.delete(ConfigEntity.builder().group(group).build());
    }

    private List<ConfigViewModel> buildConfigViewModelList(List<ConfigEntity> configlist) {
        List<ConfigViewModel> groupList = new ArrayList<>();

        for (ConfigEntity configEntity : configlist) {
            boolean existsGroup = false;
            String configGroup = configEntity.getGroup();
            for (ConfigViewModel configViewModel : groupList) {
                if (configGroup.equalsIgnoreCase(configViewModel.getGroup())) {
                    configViewModel.getConfigList().add(configEntity);
                    existsGroup = true;
                    break;
                }
            }

            if (!existsGroup) {
                List<ConfigEntity> configs = new ArrayList<>();
                configs.add(configEntity);
                ConfigViewModel cv = new ConfigViewModel(configGroup, configs);
                groupList.add(cv);
            }
        }

        return groupList;
    }
}
