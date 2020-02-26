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
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class FileVideoService {
	private static final long MAX_DATA_BLOCK = 1024 * 1024;
	
	private String videosDirectory;
	
	public FileVideoService(String videosDirectory) {
		this.videosDirectory = videosDirectory;
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
	
	public Resource createFileForWrinting(String fileName) throws FileNotFoundException, IOException {
		Resource newFile = getInputResource(fileName);
		newFile.getFile().createNewFile();
		return newFile;
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
		File fileOutput = getInputResource("out_" + fileName).getFile();		
		if (!fileOutput.exists() ||	fileOutput.lastModified() < fileInput.lastModified()) {
			fileOutput.createNewFile();
			convertM3u8ToFullPath(fileInput, fileOutput);
		}
		return new FileSystemResource(fileOutput);
	}
	
	private Resource getInputResource(String fileName) {
		return new FileSystemResource(new File(videosDirectory + "/" + fileName));
	}
	
	private void convertM3u8ToFullPath(File input, File output) throws FileNotFoundException, IOException {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(input));
				BufferedWriter writer = new BufferedWriter(new FileWriter(output, Charset.forName("UTF-8")));
		) {
			String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
			url = url + "/videos/full/";
			String line = null;
			while((line = reader.readLine()) != null) {
				if (!line.startsWith("#") && !line.startsWith(url)){
					writer.write(url + line + "\r\n");
				} else {
					writer.write(line + "\r\n");					
				}
			}
		}
	}
}
