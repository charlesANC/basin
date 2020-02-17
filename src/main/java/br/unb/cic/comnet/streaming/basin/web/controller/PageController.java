package br.unb.cic.comnet.streaming.basin.web.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.unb.cic.comnet.streaming.basin.services.FFmpegService;
import br.unb.cic.comnet.streaming.basin.services.FileVideoService;

@Controller
public class PageController {
	private Logger log = LoggerFactory.getLogger(PageController.class);
	
	@Autowired
	private FileVideoService videoService;
	
	@Autowired
	private FFmpegService ffmpegService;
	
	@GetMapping("/")
	public String home(Model model) {
		try {
			model.addAttribute("fileNames", videoService.listFiles("mp4", "m3u8"));
			return "index";			
		} catch (IOException e) {
			log.error("Something wrong has happen >>> {}", e);
			return "error";
		}
	}
	
	@GetMapping("/play/{fileName}")
	public String play(@PathVariable("fileName") String fileName, Model model) {
		try {
			UrlResource url = videoService.getVideoURL(fileName);
			model.addAttribute("mediaType", videoService.getResourceMediaType(url));
			model.addAttribute("fileURL", "/videos/"+fileName+"/full");
			return "play";			
		} catch (IOException e) {
			log.error("Something wrong has happen >>> {}", e);
			return "error";
		}
	}
	
	@GetMapping("/transcode/{fileName}")	
	public String transcode(@PathVariable("fileName") String fileName, Model model) {
		try {
			ffmpegService.transcode(videoService.getVideoURL(fileName));
			model.addAttribute("message", "Transcoding has started... wait some seconds and press F5.");
			model.addAttribute("fileNames", videoService.listFiles("mp4", "m3u8"));
		} catch (IOException e) {
			log.error("Something wrong has happen >>> {}", e);
			model.addAttribute("message", "Error: " + e.getMessage());
		}
		return "index";
	}

}
