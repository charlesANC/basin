package br.unb.cic.comnet.streaming.basin.rest.controller;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import br.unb.cic.comnet.streaming.basin.services.FileVideoService;

@RestController
public class VideoRestController {
	private final Log log = LogFactory.getLog(VideoRestController.class);
	
	@Autowired
	private FileVideoService videoService;
	
	@GetMapping("/videos/{fileName}/full")
	public ResponseEntity<UrlResource> getFullVideo(@PathVariable("fileName") String fileName) {
		try {
			log.info("Serving file " + fileName);
			UrlResource resource = videoService.getVideoURL(fileName);
			return ResponseEntity
					.status(HttpStatus.OK)
					.contentType(videoService.getResourceMediaType(resource))
					.body(resource);			
		} catch (IOException e) {
			log.error("There is a problem! >>> {}", e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@GetMapping("/videos/{fileName}/partials")
	public ResponseEntity<ResourceRegion> getPartialVideo(
			@PathVariable("fileName") String fileName, 
			@RequestHeader HttpHeaders headers) 
	{
		return getVideoPartially(fileName, headers);
	}
	
	@GetMapping("/videos/{directory}/{fileName}/partials")
	public ResponseEntity<ResourceRegion> getPartialVideo(
			@PathVariable("directory") String directory,			
			@PathVariable("fileName") String fileName, 
			@RequestHeader HttpHeaders headers) 
	{
		return getVideoPartially(directory + "/" + fileName, headers);
	}	

	private ResponseEntity<ResourceRegion> getVideoPartially(String fileName, HttpHeaders headers) {
		try {
			log.info("Serving partialy file " + fileName);			
			UrlResource resource = videoService.getVideoURL(fileName);
			HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
			return ResponseEntity
						.status(HttpStatus.PARTIAL_CONTENT)
						.contentType(videoService.getResourceMediaType(resource))
						.body(videoService.getResourceRegion(resource, range));
		} catch(IOException e) {
			log.error("There is a problem! >>> {}", e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);			
		}
	}
}
