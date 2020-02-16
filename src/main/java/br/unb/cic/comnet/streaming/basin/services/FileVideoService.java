package br.unb.cic.comnet.streaming.basin.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

public class FileVideoService {
	private static final long MAX_DATA_BLOCK = 1024 * 1024;
	
	private String videosDirectory;
	
	public FileVideoService(String videosDirectory) {
		this.videosDirectory = videosDirectory;
	}
	
	public List<String> listFiles(final String extension) throws MalformedURLException, IOException {
		File directory = new File(getVideoURL(".").getURI());
		return Arrays.asList(directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("." + extension.toLowerCase());
			}
		}));
	}
	
	public UrlResource getVideoURL(String fileName) throws MalformedURLException, IOException {
		return new UrlResource(new ClassPathResource(videosDirectory + "/" + fileName).getURI());
	}

	public MediaType getResourceMediaType(UrlResource resource) {
		return MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
	}
	
	public ResourceRegion getResourceRegion(UrlResource resource, HttpRange range) throws IOException {
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
}
