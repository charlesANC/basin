package br.unb.cic.comnet.streaming.basin.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import br.unb.cic.comnet.streaming.basin.flexM3u8IO.MediaSegment;
import br.unb.cic.comnet.streaming.basin.flexM3u8IO.PlayList;

public class TranscodingUnit extends PlayListBuider {
	private Log log = LogFactory.getLog(TranscodingUnit.class);
	
	private URL m3u8Source;
	PlayList inputPlayList;
	PlayList outputPlayList;
	Resource output;
	
	private List<String> already;
	private String partName;	
	private int index = 0;
	
	public TranscodingUnit(
			FileVideoService fileService,
			FFmpegService ffmpegService,
			URL m3u8Source) 
	{
		super(fileService, ffmpegService);
		this.m3u8Source = m3u8Source;
	}

	@Override
	public void initilize() {
		try {
			output = getFileService().createFileForWriting(createOutputName());
			log.info("Will write to the file " + output.getFilename());		
			already = new ArrayList<String>();
			
			log.info("Reading the input file " + m3u8Source.getFile());
			this.inputPlayList = readInputPlayList();
			this.outputPlayList = inputPlayList.cloneWithoutSegments();			
		} catch (IOException e) {
			log.error("Something wrong has happen >> {}", e);
		}
	}
	
	@Override
	public void process() {
		try {
			Instant start = Instant.now();
			
			MediaSegment nextSegment = getNextSegment(inputPlayList.getSegments());
			if (nextSegment != null) {
				log.info("Next file to be transcoded is " + nextSegment.getUrl());
				
				String newSegmentName = "part_" + partName + (index++) + ".ts";
				
				getFfmpegService().reduceQuality(new URL(nextSegment.getUrl()), newSegmentName);
				
				outputPlayList.addSegment(newSegmentName, nextSegment.getDuration(), nextSegment.getInfo());
				if (index >= 3) {
					log.info("Writing the output file...");
					writeOut(outputPlayList, output);
					log.info("Going to rest a little...");
				}
			}
			
			log.info("Updating source...");
			
			inputPlayList = readInputPlayList();
			
			Duration elapsed = Duration.between(start, Instant.now());
			log.info("Elapsed time to transcode is " + Integer.valueOf(elapsed.getNano() / 1000000) + " miliseconds. ");			
			
		} catch(IOException e) {
			log.error("Something wrong has happen >> {}", e);			
			stop();
		}
	}	
	
	private String createOutputName() {
		partName = RandomUtils.nextLong() + "";
		return "ouput_" + partName + ".m3u8";
	}

	private PlayList readInputPlayList() throws IOException {
		try (InputStream stream = m3u8Source.openStream()) {
			return getReader().read(stream);
		} catch (IOException e) {
			throw e;
		}
	}
	
	private void writeOut(PlayList playList, Resource output) throws IOException {
		try (OutputStream outputStrean = new FileOutputStream(output.getFile())) {
			getWriter().write(playList, outputStrean);			
			playList.incrementMediaSequence();			
		} catch(IOException e) {
			throw e;
		}
	} 
	
	private MediaSegment getNextSegment(List<MediaSegment> segments) {
		for(MediaSegment segment : segments) {
			if (!already.contains(segment.getUrl())) {
				already.add(segment.getUrl());
				return segment;
			}
		}
		return null;
	}
}
