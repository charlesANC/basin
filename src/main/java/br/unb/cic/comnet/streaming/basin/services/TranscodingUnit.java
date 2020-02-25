package br.unb.cic.comnet.streaming.basin.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import br.unb.cic.comnet.streaming.basin.flexM3u8IO.FlexiblePlayListReader;
import br.unb.cic.comnet.streaming.basin.flexM3u8IO.FlexiblePlayListWriter;
import br.unb.cic.comnet.streaming.basin.flexM3u8IO.MediaSegment;
import br.unb.cic.comnet.streaming.basin.flexM3u8IO.PlayList;

public class TranscodingUnit implements Runnable {
	private Log log = LogFactory.getLog(TranscodingUnit.class);
	
	private FileVideoService fileService;
	private FFmpegService ffmpegService;
	private URL m3u8Source;
	private boolean hasToStop = false;	
	
	private List<String> already;
	private String partName;	
	private FlexiblePlayListReader reader;
	private FlexiblePlayListWriter writer;

	private int index = 0;
	
	public TranscodingUnit(
			FileVideoService fileService,
			FFmpegService ffmpegService,
			URL m3u8Source) 
	{
		this.fileService = fileService;
		this.ffmpegService = ffmpegService;
		this.m3u8Source = m3u8Source;
	}
	
	public void stop() {
		hasToStop = true;
	}

	@Override
	public void run() {
		try {
			log.info("Starting... ");
			
			Resource output = fileService.createFileForWrinting(createOutputName());
			log.info("Will write to the file " + output.getFilename());
			
			already = new ArrayList<String>();			
			reader = new FlexiblePlayListReader();
			writer = new FlexiblePlayListWriter();			
			
			log.info("Reading the input file " + m3u8Source.getFile());
			PlayList input = readInputPlayList();
			
			PlayList ouputPlayList = input.cloneWithoutSegments();
			
			log.info("Will start the loop...");
			while(!hasToStop) {
				MediaSegment nextSegment = getNextSegment(input.getSegments());
				if (nextSegment != null) {
					log.info("Next file to be transcoded is " + nextSegment.getUrl());
					String newSegmentName = "part_" + partName + (index++) + ".ts";
					ffmpegService.reduceQuality(new URL(nextSegment.getUrl()), newSegmentName);
					ouputPlayList.addSegment(newSegmentName, nextSegment.getDuration(), nextSegment.getInfo());
					if (index >= 3) {
						log.info("Writing the output file...");
						writeOut(ouputPlayList, output);
						log.info("Going to rest a little...");
					}
				}
				log.info("Updating source...");
				input = readInputPlayList();				
			}
		} catch (IOException e) {
			log.error("Something wrong has happen >>> {}", e);
		}
	}
	
	private String createOutputName() {
		partName = RandomUtils.nextLong() + "";
		return "ouput_" + partName + ".m3u8";
	}

	private PlayList readInputPlayList() throws IOException {
		try (InputStream stream = m3u8Source.openStream()) {
			return reader.read(stream);
		} catch (IOException e) {
			throw e;
		}
	}
	
	private void writeOut(PlayList playList, Resource output) throws IOException {
		try (OutputStream outputStrean = new FileOutputStream(output.getFile())) {
			writer.write(playList, outputStrean);			
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
