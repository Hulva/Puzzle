package hulva.luva.wxx.platform.puzzle.backend.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hulva.luva.wxx.platform.puzzle.backend.entity.PluginEntity;
import hulva.luva.wxx.platform.puzzle.backend.entity.TemplateStructEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.FileModel;
import hulva.luva.wxx.platform.puzzle.backend.repository.PluginRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructDataRepository;
import hulva.luva.wxx.platform.puzzle.backend.repository.TemplateStructRepository;
import hulva.luva.wxx.platform.puzzle.backend.storage.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hulva Luva.H
 * @since 2019-10-12
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController extends AbstractBaseController {
	@Autowired
	PluginRepository pluginRepository;
	@Autowired
	TemplateStructRepository templateStructRepository;
	@Autowired
	TemplateStructDataRepository templateStructDataRepository;
//    @Lazy
//	@Autowired
//	PluginFileRepository pluginFileRepository;

	private static final String[] FOLDER_LIST = { "data", "extends", "jars", "json", "scripts" };
	private static final Set<String> FOLDER_LIST_EXCLUDE = new HashSet<String>() {
		private static final long serialVersionUID = 3024901105529710931L;
		{
			add("ignite");
			add("MAP");
			add("STORAGE");
		}
	};
	private static final Map<String, String> FOLDER_CLASS_NAME = new HashMap<String, String>() {
		private static final long serialVersionUID = 3024901105529710931L;

		{
			put("data", "t-folder");
			put("extends", "t-plugin");
			put("jars", "t-packages");
			put("json", "t-resource");
			put("scripts", "t-scripts");
		}
	};
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	@GetMapping("/list")
	public Response<List<FileModel>> listFiles() {
		List<FileModel> fileDatas = new ArrayList<>();
		for (int i = 0; i < FOLDER_LIST.length; i++) {
			File file = Paths.get(FOLDER_LIST[i]).toFile();
			scanFile(fileDatas, file);
		}
		return SUCCESS(fileDatas);
	}

	@GetMapping("/list/nested")
	public Response<List<FileModel>> listFiles(@RequestParam("path") String path) {
		List<FileModel> fileDatas = new ArrayList<>();
		File file = Paths.get(path).toFile();
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			scanFile(fileDatas, files[i]);
		}
		return SUCCESS(fileDatas);
	}

	@GetMapping("/delete")
	public Response<String> delete(@RequestParam("file") String file) {
		String fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
		if (file.endsWith(".jar")) {
			List<PluginEntity> pluginEntities = pluginRepository.findAll(Example.of(PluginEntity.builder().name(fileName).build()));
			for (PluginEntity pluginEntity : pluginEntities) {
				List<TemplateStructEntity> inUse = templateStructRepository.findAll(Example.of(TemplateStructEntity.builder().pluginName(pluginEntity.getName()).pluginVersion(pluginEntity.getVersion()).build()));
				if (inUse != null && inUse.size() > 0) {
					return FAIL("无法删除，被使用中[" + inUse.stream()
						.map(tse -> String.format("%s-%s", tse.getTemplate().getName(), tse.getName()))
						.collect(Collectors.toList()) + "]");
				}
			}
		} else if (file.endsWith(".groovy")) {
			// TODO
		}
		Paths.get(file).toFile().delete();
		
		return SUCCESS("删除成功!");
	}

	private void scanFile(List<FileModel> fileDatas, File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory() && !FOLDER_LIST_EXCLUDE.contains(file.getName())) {
			FileModel fileModel = new FileModel(file.getName(), null, "directory",
					formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), TimeZone.getDefault().toZoneId())));
			if (FOLDER_CLASS_NAME.containsKey(file.getName())) {
				fileModel.setClassName(FOLDER_CLASS_NAME.get(file.getName()));
			} else {
				fileModel.setClassName("t-folder");
			}
			fileDatas.add(fileModel);
//			File[] files = file.listFiles();
//			for (int i = 0; i < files.length; i++) {
//				scanFile(fileDatas, files[i]);
//			}
		} else if (file.isFile()) {
			String fileName = file.getName();
			FileModel fileModel = new FileModel(fileName, file.length(), "file",
					formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), TimeZone.getDefault().toZoneId())));
			fileModel.setPath(file.getAbsolutePath());
			switch (fileName) {
			case "csv":
				fileModel.setClassName("t-csv");
				break;
			case "xml":
				fileModel.setClassName("t-xml");
				break;
			case "zip":
				fileModel.setClassName("t-zip");
				break;
			case "jar":
				fileModel.setClassName("t-jar");
				break;
			case "image":
				fileModel.setClassName("t-image");
				break;
			default:
				fileModel.setClassName("t-file");
				break;
			}
			fileDatas.add(fileModel);
		}
	}

	@GetMapping("/download")
	public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@RequestParam("fileName") String fileName,
			HttpServletRequest request) {
		// Load file as Resource
		org.springframework.core.io.Resource resource = loadAsResource(fileName);

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

	public Resource loadAsResource(String filename) {
		try {
			Path file = Paths.get(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}
}
