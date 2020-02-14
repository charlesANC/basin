package br.unb.cic.comnet.streaming.basin.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.unb.cic.comnet.streaming.basin.services.FileVideoService;

@Configuration
public class BasinBeansConfiguration {
	
	@Value("${videoDirectory}")
	private String videosDirectory;
	
	@Bean
	public FileVideoService getFileVideoService() {
		return new FileVideoService(videosDirectory);
	}
}
