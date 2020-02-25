package br.unb.cic.comnet.streaming.basin.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.unb.cic.comnet.streaming.basin.services.FFmpegService;
import br.unb.cic.comnet.streaming.basin.services.FileVideoService;
import br.unb.cic.comnet.streaming.basin.services.TranscodingUnit;
import br.unb.cic.comnet.streaming.basin.web.model.HLSFile;

@Controller
public class PageController {
	private Logger log = LoggerFactory.getLogger(PageController.class);
	
	@Autowired
	private FileVideoService videoService;
	
	@Autowired
	private FFmpegService ffmpegService;
	
	private List<TranscodingUnit> jobs = new ArrayList<TranscodingUnit>();
	
	@GetMapping("/")
	public String home(Model model) {
		try {
			model.addAttribute("hlsFile", new HLSFile());
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
			Resource source = videoService.getVideoURL(fileName);
			model.addAttribute("mediaType", videoService.getResourceMediaType(source));
			model.addAttribute("fileURL", "/basin/videos/full/"+fileName);
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
			model.addAttribute("hlsFile", new HLSFile());			
			model.addAttribute("fileNames", videoService.listFiles("mp4", "m3u8"));
		} catch (IOException e) {
			log.error("Something wrong has happen >>> {}", e);
			model.addAttribute("message", "Error: " + e.getMessage());
		}
		return "index";
	}
/*	
	@PostMapping("/hlstrans")
	public String transcodeHlsFiles(@ModelAttribute("hlsFile") HLSFile hlsFile, Model model) {
		model.addAttribute("hlsFile", new HLSFile());		
		log.info("URL: " + hlsFile.getUrl());
		log.info("CRF: " + hlsFile.getCrf());
		
		try {
			int index = 0;
			URL url = new URL(hlsFile.getUrl());
			TranscodingDataUnit dataUnit = new TranscodingDataUnit(url);			
			URLForTranscoding next = null;
			do {
				dataUnit.updateLinks();
				next = dataUnit.getNextToTranscode();
				if (next == null) break;
				ffmpegService.reduceQuality(dataUnit.getNextToTranscode().getUrl(), index++);				
			} while (true);
		} catch (IOException e) {
			log.error("Something wrong has happen >>> {}", e);
			model.addAttribute("message", "Error: " + e.getMessage());
		}
		
		return "index";
	}
*/
	
	@PostMapping("/hlstrans")
	public String transcodeHlsFiles(@ModelAttribute("hlsFile") HLSFile hlsFile, Model model) {
		try {
			TranscodingUnit unitJob = new TranscodingUnit(videoService, ffmpegService, new URL(hlsFile.getUrl()));
			jobs.add(unitJob);
			new Thread(unitJob).start();
		} catch (IOException e) {
			log.error("Something wrong has happen >>> {}", e);
			model.addAttribute("message", "Error: " + e.getMessage());
		}
		
		return "index";
	}	
}
