package hulva.luva.wxx.platform.puzzle.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hulva Luva.H
 * @since 2019-11-26
 *
 */
@RestController
@RequestMapping("/api/distribute/file")
public class DistributedFileController extends AbstractBaseController {

//    @Autowired
//	Ignite ignite;
//    @Lazy
//	@Autowired
//	PluginFileRepository pluginFileRepository;
//    
//	@RequestMapping("/summary")
//	public Response<?> summary() {
//		Map<String, Object> result = new HashMap<>();
//		Iterable<PluginFile> pluginFiles = pluginFileRepository.findAll();
//		List<PluginFile> summary = null;
//		if (pluginFiles != null) {
//			summary = Lists.newArrayList(pluginFiles);
//		} else {
//			summary = new ArrayList<PluginFile>(0);
//		}
//		result.put("summary", summary.stream().collect(Collectors.groupingBy(PluginFile::getName)));
//		return SUCCESS(result);
//	}
//	
//	@RequestMapping("/sync")
//	public Response<?> sync(@RequestParam String fileName) {
//		ignite.message().send("fileSync", fileName);
//		return SUCCESS("File syncing is on the way...");
//	}
//	
//	@RequestMapping("/search")
//	public Response<Map<String, Object>> search(@RequestParam String fileName) {
//		Map<String, Object> result = new HashMap<>();
//		Iterable<PluginFile> pluginFiles = pluginFileRepository.findByNameLike(fileName);
//		List<PluginFile> summary = null;
//		if (pluginFiles != null) {
//			summary = Lists.newArrayList(pluginFiles);
//		} else {
//			summary = new ArrayList<PluginFile>(0);
//		}
//		result.put("summary", summary.stream().collect(Collectors.groupingBy(PluginFile::getName)));
//		return SUCCESS(result);
//	}
}
