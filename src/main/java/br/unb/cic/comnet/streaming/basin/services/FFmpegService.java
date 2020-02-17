package br.unb.cic.comnet.streaming.basin.services;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.UrlResource;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class FFmpegService {
	
	private FFmpegExecutor executor;
	
	public FFmpegService(FFmpegExecutor executor) {
		this.executor = executor;
	}
	
	public void transcode(UrlResource resource) throws IOException {
		executor.createJob(createTwoFilesBuilder(new File(resource.getURI()).getAbsolutePath())).run();		
	}
	
	private FFmpegBuilder createSingleFileBuilder(String fileName) {
		FFmpegBuilder builder = new FFmpegBuilder()
				  .setInput("\"" + fileName + "\"")
				  .addOutput("thumbnail.png")
				    .setFrames(1)
				    .setVideoFilter("select='gte(n\\,10)',scale=200:-1")
				    .done();
		return builder;
	}
	
	private FFmpegBuilder createTwoFilesBuilder(String fileName) {
		FFmpegBuilder builder = new FFmpegBuilder()
				  .setVerbosity(FFmpegBuilder.Verbosity.INFO)
				  .setInput("\"" + fileName + "\"")
				  .addOutput("\"" + fileName + "_.m3u8\"")
				  	.addExtraArgs("-crf", "21")
				  	.setAudioCodec("aac")
				  	.setAudioBitRate(128000)
				  	.setAudioChannels(2)
				  	.setPreset("superfast")
				  	.setFormat("hls")
				  	.addExtraArgs("-hls_time", "6")
				  	.addExtraArgs("-hls_playlist_type", "event ")
				  .done();
		return builder;
	}
}
