package br.unb.cic.comnet.streaming.basin.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class FileVideoService {
	private static final long MAX_DATA_BLOCK = 1024 * 1024;
	
	private String videosDirectory;
	private ResourceLoader resourceLoader;
	
	private Map<String, File> outputs; 
	
	public FileVideoService(String videosDirectory, ResourceLoader resourceLoader) {
		this.videosDirectory = videosDirectory;
		this.resourceLoader = resourceLoader;
		this.outputs = new HashMap<String, File>();
	}
	
	public List<String> listFiles(final String... extensions) throws MalformedURLException, IOException {
		File directory = new File(getVideoURL(".").getURI());
		return Arrays.asList(directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				for(String extension: extensions) {
					if (name.toLowerCase().endsWith("." + extension.toLowerCase())) {
						return true;
					}
				}
				return false;
			}
		}));
	}
	
	public Resource getVideoURL(String fileName) throws MalformedURLException, IOException {
		return dealWithManifestFiles(fileName);
	}

	public MediaType getResourceMediaType(Resource resource) {
		return MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
	}
	
	public ResourceRegion getResourceRegion(Resource resource, HttpRange range) throws IOException {
		if (range == null) {
			return new ResourceRegion(resource, 0, Math.min(MAX_DATA_BLOCK, resource.contentLength()));
		} else {
			long length = resource.contentLength();
			return new ResourceRegion(
					resource, 
					range.getRangeStart(length), 
					Math.min(MAX_DATA_BLOCK, range.getRangeEnd(length) - range.getRangeStart(length) + 1)
			);
		}
	}
	
	private Resource dealWithManifestFiles(String fileName) throws FileNotFoundException, IOException {
		Resource input = getInputResource(fileName);
		if (!input.exists()) {
			return null;
		}
		
		if (!fileName.endsWith("m3u8")) {
			return input;			
		}
		
		File fileInput = input.getFile();
		if (!outputs.containsKey(fileName) || 
				outputs.get(fileName).lastModified() < fileInput.lastModified()) {
			File fileOutput = File.createTempFile(fileName.replace(".", ""), ".m3u8");
			convertM3u8ToFullPath(fileInput, fileOutput);
			outputs.put(fileName, fileOutput);
		}
		return new FileSystemResource(outputs.get(fileName));
	}
	
	private Resource getInputResource(String fileName) {
		return resourceLoader.getResource("classpath:" + videosDirectory + "/" + fileName);
	}
	
	private void convertM3u8ToFullPath(File input, File output) throws FileNotFoundException, IOException {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(input));
				BufferedWriter writer = new BufferedWriter(new FileWriter(output, Charset.forName("UTF-8")));
		) {
			String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
			String line = null;
			while((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					writer.write(line + "\r\n");
				} else {
					writer.write(url + "/videos/" + line + "/partials\r\n");
				}
			}
		}
	}
}
