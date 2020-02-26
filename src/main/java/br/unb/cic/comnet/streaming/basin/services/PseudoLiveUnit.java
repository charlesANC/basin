package br.unb.cic.comnet.streaming.basin.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import br.unb.cic.comnet.streaming.basin.flexM3u8IO.PlayList;

public class PseudoLiveUnit extends PlayListBuider {
	private final Log log = LogFactory.getLog(PseudoLiveUnit.class);
	
	private String partsPrefix;
	private int index = 0;
	
	private PlayList original;
	private PlayList pseudoLive;
	private Resource output;

	public PseudoLiveUnit(
			FileVideoService fileService, 
			FFmpegService ffmpegService, 
			String partsPrefix
	) {
		super(fileService, ffmpegService);
		this.partsPrefix = partsPrefix;
	}

	@Override
	public void initilize() {
		try {
			loadPlayLists();
			output = getFileService().createFileForWrinting("pl_" + partsPrefix + ".m3u8");
		} catch (IOException e) {
			log.error("Something wrong has happened >> {}", e);
			stop();
		}
	}

	@Override
	public void process() {
		try {
			log.info("Writing a new version of the file...");
			writeOut(pseudoLive, output);
			copySegment(original, pseudoLive, index++);
			if (index > original.getSegments().size()) {
				stop();
			}
			Thread.sleep(5000);
		} catch (IOException | InterruptedException e) {
			log.error("Something wrong has happened >> {}", e);
			stop();
		}
	}
	
	public void loadPlayLists() throws IOException {
		original = getReader()
				.read(getFileService().getVideoURL(partsPrefix + "_.m3u8")
						.getInputStream());
		
		pseudoLive = original.cloneWithoutSegments();
		
		for(int i = 0; i < PlayList.NUM_MAX_SEGMENTS; i++) {
			copySegment(original, pseudoLive, i);
		}
		index = PlayList.NUM_MAX_SEGMENTS;
	} 
	
	private void copySegment(PlayList origin, PlayList destiny, int index) {
		if (index >= origin.getSegments().size()) return;
		
		destiny
			.addSegment(
				origin.getSegments().get(index).getUrl(), 
				origin.getSegments().get(index).getDuration(), 
				origin.getSegments().get(index).getInfo()
			);		
	}
	
	private void writeOut(PlayList playList, Resource output) throws IOException {
		try (OutputStream outputStrean = new FileOutputStream(output.getFile())) {
			getWriter().write(playList, outputStrean);			
			playList.incrementMediaSequence();			
		} catch(IOException e) {
			throw e;
		}
	}	
}
