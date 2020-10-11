package hulva.luva.wxx.platform.puzzle.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.activation.UnsupportedDataTypeException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hulva.luva.wxx.platform.core.plugin.RestPlugin;
import hulva.luva.wxx.platform.core.plugin.ServicePlugin;
import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata;
import hulva.luva.wxx.platform.core.util.BasePluginUtil;
import hulva.luva.wxx.platform.core.util.PluginClassLoader;
import hulva.luva.wxx.platform.puzzle.backend.constants.Constants;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.PluginFieldEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateStructEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.PluginModel;
import hulva.luva.wxx.platform.puzzle.backend.repository.PluginFieldRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.PluginRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructRepository;
import hulva.luva.wxx.platform.puzzle.backend.storage.StorageService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hulva Luva.H
 * @since 2019年3月15日
 */
@Slf4j
@RestController
@RequestMapping("/api/plugin")
public class PluginController extends AbstractBaseController {
	private final StorageService storageService;
	@Autowired
	PluginFieldRepository pluginFieldRepository;
	@Autowired
	TemplateStructRepository templateStructRepository;
	@Autowired
	PluginRepository pluginRepository;
	
//	@Lazy
//	@Autowired
//	PluginFileRepository pluginFileRepository;
	
	public PluginController(StorageService storageService) {
		this.storageService = storageService;
	}

	@Deprecated
	@GetMapping("/get/{pluginName}/{pluginVersion}")
	public Response<List<PluginFieldEntity>> getPluginInfoById(@PathVariable String pluginName, @PathVariable Integer pluginVersion) {
		List<PluginFieldEntity> fields = pluginFieldRepository.findAll(Example.of(PluginFieldEntity.builder()
				.build()));
		return SUCCESS(fields);
	}

	/**
	 * 删除 mysql 中的插件记录，当此插件对应的jar包没有其他在mysql中记录的插件时，删除jar
	 *
	 * @return 删除插件的结果
	 */
	@GetMapping("/delete/{pluginName}/{pluginVersion}")
	public Response<Void> deletePluginInfoByNameVersion(@PathVariable String pluginName, @PathVariable Integer pluginVersion) {
		List<TemplateStructEntity> inUse = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder()
				.pluginName(pluginName)
				.pluginVersion(pluginVersion)
				.build()));
		if (inUse != null && inUse.size() > 0) {
			return FAIL("无法删除，被使用中[" + inUse.stream().map(TemplateStructEntity::getTemplate).collect(Collectors.toList()) + "]");
		}
		PluginEntity plugin = pluginRepository.findOne(Example.of(PluginEntity.builder()
				.name(pluginName)
				.version(pluginVersion)
				.build())).get();
		Paths.get(Constants.PLUGIN_JAR_PATH_PRFIX, plugin.getJarName()).toFile().delete();
		pluginFieldRepository.delete(PluginFieldEntity.builder()
				.plugin(plugin)
				.build());
		pluginRepository.delete(PluginEntity.builder()
				.name(pluginName)
				.version(pluginVersion)
				.build());
		return SUCCESS();
	}

	/**
	 * 删除 mysql 中的插件记录，当此插件对应的jar包没有其他在mysql中记录的插件时，删除jar
	 *
	 * @return 删除插件的结果
	 */
	@Deprecated
	@PostMapping("/delete")
	public Response<List<String>> deletePluginInfoByNameVersions(@RequestBody List<String> nameVersions) {
		List<String> result = new ArrayList<>();
		for (String nameVersion : nameVersions) {
			String[] nameVersionArr = nameVersion.split("@");
			List<TemplateStructEntity> inUse = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder()
					.pluginName(nameVersionArr[0])
					.pluginVersion(Integer.parseInt(nameVersionArr[1]))
					.build()));
			if (inUse != null && inUse.size() > 0) {
				result.add("无法删除" + nameVersion + "，被使用中["
						+ inUse.stream().map(TemplateStructEntity::getTemplate).collect(Collectors.toList()) + "]");
				continue;
			}
			pluginFieldRepository.delete(PluginFieldEntity.builder()
					// TODO
					.build());
			pluginRepository.delete(PluginEntity.builder()
					.name(nameVersionArr[0])
					.version(Integer.parseInt(nameVersionArr[1]))
					.build());
		}
		return SUCCESS(result);
	}

	@GetMapping("/get/name/{pluginName}")
	public Response<List<PluginEntity>> getPluginInfoByName(@PathVariable String pluginName) {
		return SUCCESS(pluginRepository.findAll(Example.of(PluginEntity.builder()
				.name(pluginName)
				.build())));
	}

	@GetMapping("/all")
	public Response<List<PluginEntity>> all() {
		return SUCCESS(pluginRepository.findAll());
	}

	@GetMapping("/system/refresh")
	public Response<List<PluginModel>> allSystem() throws IOException, ClassNotFoundException {
		List<Class<?>> clazzs = PluginClassLoader.systemPluginClass();
		List<PluginModel> plugins = new ArrayList<>();
		for (Class<?> clazz : clazzs) {
			PluginEntity plugin = resolvePluginClass("SYSTEM", clazz);
			List<PluginFieldEntity> pluginFieldEntities = BasePluginUtil.getPluginFieldMetadatas(clazz)
					.parallelStream().map(pluginFieldMetadata -> {
						PluginFieldEntity pluginFieldEntity = new PluginFieldEntity();
						BeanUtils.copyProperties(pluginFieldMetadata, pluginFieldEntity);
						pluginFieldEntity.setPlugin(plugin);
						return pluginFieldEntity;
					}).collect(Collectors.toList());
			pluginRepository.save(plugin);
			pluginFieldRepository.saveAll(pluginFieldEntities);

			PluginModel pluginModel = new PluginModel();
			pluginModel.setPlugin(plugin);
			pluginModel.setPluginFields(pluginFieldEntities);
			plugins.add(pluginModel);
		}
		return SUCCESS(plugins);
	}

	@GetMapping("/list")
	public Response<Map<String, List<PluginEntity>>> list() {
		return SUCCESS(pluginRepository.findAll().stream().collect(Collectors.groupingBy(PluginEntity::getName)));
	}

	private final static List<String> SUPPORTED_FILE_TYPE = new ArrayList<>(2);
	static {
		SUPPORTED_FILE_TYPE.add("jar");
		SUPPORTED_FILE_TYPE.add("groovy");
	}
	@PostMapping("/jar/upload/{packagename}")
	public Response<Object> handleFileUpload(@PathVariable String packagename, @RequestParam("file") MultipartFile uploadFile) throws Exception {
		String originalFileName = uploadFile.getOriginalFilename();
//		String name = originalFileName.substring(0, originalFileName.lastIndexOf("."));
		String type = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
		
		if (!SUPPORTED_FILE_TYPE.contains(type.toLowerCase())) {
			throw new UnsupportedDataTypeException(type);
		}
		
		Response<Object> response = null;
		if ("jar".equals(type.toLowerCase())) {
			response = handleJarUpload(uploadFile, originalFileName, packagename);
		} else if ("groovy".equals(type.toLowerCase())) {
			response = handleGroovyUpload(uploadFile, originalFileName);
		}
		
		return response;
	}

	private Response<Object> handleJarUpload(MultipartFile uploadFile, String filename, String packagename) throws Exception {
		storageService.store(uploadFile, filename);
		Path pluginJarPath = storageService.getFile(filename);
		List<Class<?>> clazzs = PluginClassLoader.pluginClass(pluginJarPath.toFile(), packagename);
		List<PluginEntity> plugins = new ArrayList<>();
		for (Class<?> clazz : clazzs) {
			PluginEntity plugin = resolvePluginClass(filename, clazz);
			List<PluginFieldEntity> pluginFieldEntities = BasePluginUtil.getPluginFieldMetadatas(clazz)
					.parallelStream().map(pluginFieldMetadata -> {
						PluginFieldEntity pluginFieldEntity = new PluginFieldEntity();
						BeanUtils.copyProperties(pluginFieldMetadata, pluginFieldEntity);
						pluginFieldEntity.setPlugin(plugin);
						return pluginFieldEntity;
					}).collect(Collectors.toList());
			pluginRepository.save(plugin);
			pluginFieldRepository.saveAll(pluginFieldEntities);
			plugins.add(plugin);
		}
		return SUCCESS(plugins);
	}

	private Response<Object> handleGroovyUpload(MultipartFile uploadFile, String fileName2Save) throws IOException {
		try (InputStream inputStream = uploadFile.getInputStream()) {
			Files.copy(inputStream, Paths.get(Constants.PLUGIN_Script_PATH_PRFIX, fileName2Save), StandardCopyOption.REPLACE_EXISTING);
		}
		return SUCCESS(fileName2Save);
	}

	/**
	 * 根据字段解析Plugin
	 */
	PluginEntity resolvePluginClass(String filename, Class<?> clazz) {
		if (filename == null) {
			filename = "SYSTEM";
		}
		PluginEntity plugin = new PluginEntity();
		if (ServicePlugin.class.isAssignableFrom(clazz)) {
			plugin.setType(PluginMetadata.PluginType.SERVICE.name());
		} else if (RestPlugin.class.isAssignableFrom(clazz)) {
			plugin.setType(PluginMetadata.PluginType.RESTFUL.name());
		} else {
			plugin.setType(PluginMetadata.PluginType.OTHER.name());
		}
		plugin.setJarName(filename);
		plugin.setName(clazz.getName());
		plugin.setAliasName(clazz.getSimpleName());
		if (!"SYSTEM".equals(filename)) {
			PluginEntity oldPlugin = pluginRepository.findAll(Example.of(PluginEntity.builder()
					.name(clazz.getName())
					.build()), Sort.by(Order.asc("version"))).stream().findFirst().get();
			if (oldPlugin == null) {
				plugin.setVersion(0);
			} else {
				plugin.setVersion(oldPlugin.getVersion() + 1);
			}
		}
		return plugin;
	}



	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		org.springframework.core.io.Resource resource = null;
		if (fileName.endsWith(".jar")) {
			resource = storageService.loadAsResource(fileName);
		} else if(fileName.endsWith(".groovy")) {
			Path filePath = Paths.get(Constants.PLUGIN_Script_PATH_PRFIX).resolve(fileName);
			resource = storageService.loadAsResource(filePath);
		}

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			log.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
}
