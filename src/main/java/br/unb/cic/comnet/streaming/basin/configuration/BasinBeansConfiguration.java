package br.unb.cic.comnet.streaming.basin.configuration;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import br.unb.cic.comnet.streaming.basin.services.FFmpegService;
import br.unb.cic.comnet.streaming.basin.services.FileVideoService;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.RunProcessFunction;

@Configuration
public class BasinBeansConfiguration {
	
	@Value("${videoDirectory}")
	private String videosDirectory;
	
	@Value("${ffmpegPath}")	
	private String ffmpegPath;
	
	@Value("${ffprobPath}")	
	private String ffprobPath;
	
	@Bean
	public FileVideoService getFileVideoService(ResourceLoader resourceLoader) {
		return new FileVideoService(videosDirectory, resourceLoader);
	}
	
	@Bean
	public FFmpegService getFFmpegService(FFmpeg ffmpeg, FFprobe ffprobe) throws IOException {
		return new FFmpegService(new FFmpegExecutor(ffmpeg, ffprobe));
	}
	
	@Bean
	public FFprobe getFFProbe(RunProcessFunction runProcessFunction) {
		return new FFprobe(ffprobPath, runProcessFunction);
	}
	
	@Bean
	public FFmpeg getFFmpeg(RunProcessFunction runProcessFunction) throws IOException {
		return new FFmpeg(ffmpegPath, runProcessFunction);
	}
	
	@Bean
	public RunProcessFunction getRunProcessFunction() throws IOException {
		RunProcessFunction runProcessFunction = new RunProcessFunction();
		runProcessFunction.setWorkingDirectory(new File(new ClassPathResource(videosDirectory).getURI()));
		return runProcessFunction;
	}
}
