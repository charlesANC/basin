package br.unb.cic.comnet.streaming.basin.services;

import java.io.IOException;
import java.net.URL;

import org.springframework.core.io.Resource;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class FFmpegService {
	private static final int QUALITY_REDUCE_FACTOR = 24;
	
	private FFmpegExecutor executor;
	
	public FFmpegService(FFmpegExecutor executor) {
		this.executor = executor;
	}
	
	public void transcode(Resource resource) throws IOException {
		executor.createJob(createTwoFilesBuilder(resource.getFile().getAbsolutePath())).run();		
	}
	
	public void reduceQuality(URL url, String outputName) throws IOException {
		executor.createJob(reduceBitRate(QUALITY_REDUCE_FACTOR, url, outputName)).run();
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
	
	private FFmpegBuilder reduceBitRate(int factor, URL input, String output) {
		FFmpegBuilder builder = new FFmpegBuilder()
				.setVerbosity(FFmpegBuilder.Verbosity.QUIET)
				  .setInput(input.toString())
				  .addOutput(output)
				  	.setVideoCodec("libx264")				  
				  	.addExtraArgs("-crf", String.valueOf(factor))
				  	.setAudioCodec("aac")
				  	.setPreset("veryfast")
				  .done();
		return builder;
	}
}
