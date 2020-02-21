package br.unb.cic.comnet.streaming.basin.services;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.core.io.Resource;

import ch.qos.logback.classic.Logger;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class FFmpegService {
	private static final int QUALITY_REDUCE_FACTOR = 24;
	
	private Log log = LogFactory.getLog(FFmpeg.class);
	
	private FFmpegExecutor executor;
	
	public FFmpegService(FFmpegExecutor executor) {
		this.executor = executor;
	}
	
	public void transcode(Resource resource) throws IOException {
		executor.createJob(createTwoFilesBuilder(resource.getFile().getAbsolutePath())).run();		
	}
	
	public void reduceQuality(URL url, int index) throws IOException {
		executor.createJob(reduceBitRate(QUALITY_REDUCE_FACTOR, url, index)).run();
	}
	
	/*
	private FFmpegBuilder createSingleFileBuilder(String fileName) {
		FFmpegBuilder builder = new FFmpegBuilder()
				  .setInput("\"" + fileName + "\"")
				  .addOutput("thumbnail.png")
				    .setFrames(1)
				    .setVideoFilter("select='gte(n\\,10)',scale=200:-1")
				    .done();
		return builder;
	}
	*/
	
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
	
	private FFmpegBuilder reduceBitRate(int factor, URL url, int index) {
		FFmpegBuilder builder = new FFmpegBuilder()
				.setVerbosity(FFmpegBuilder.Verbosity.INFO)
				  .setInput(url.toString())
				  .addOutput("mine_" + index + ".ts")
				  	.setVideoCodec("libx264")				  
				  	.addExtraArgs("-crf", String.valueOf(factor))
				  	.setAudioCodec("aac")
				  	.setPreset("veryfast")
				  .done();
		return builder;
	}
}
