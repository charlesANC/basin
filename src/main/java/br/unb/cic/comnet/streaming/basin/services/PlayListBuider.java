package br.unb.cic.comnet.streaming.basin.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unb.cic.comnet.streaming.basin.flexM3u8IO.FlexiblePlayListReader;
import br.unb.cic.comnet.streaming.basin.flexM3u8IO.FlexiblePlayListWriter;

public abstract class PlayListBuider implements Runnable {
	private Log log = LogFactory.getLog(PlayListBuider.class);
	
	private FileVideoService fileService;
	private FFmpegService ffmpegService;
	private boolean hasToStop = false;	
	
	private FlexiblePlayListReader reader;
	private FlexiblePlayListWriter writer;	
	
	public PlayListBuider(FileVideoService fileService, FFmpegService ffmpegService) {
		this.fileService = fileService;
		this.ffmpegService = ffmpegService;
	}
	
	protected FileVideoService getFileService() {
		return fileService;
	}

	protected FFmpegService getFfmpegService() {
		return ffmpegService;
	}

	protected FlexiblePlayListReader getReader() {
		return reader;
	}

	protected FlexiblePlayListWriter getWriter() {
		return writer;
	}

	public void stop() {
		hasToStop = true;
	}	
	
	@Override
	public void run() {
		log.info("Starting... ");
		
		reader = new FlexiblePlayListReader();
		writer = new FlexiblePlayListWriter();			
		
		initilize();
		
		log.info("Will start the loop...");
		while(!hasToStop) {
			process();
			log.info("looooop...");				
		}
	}
	
	public abstract void initilize();
	
	public abstract void process();
}
