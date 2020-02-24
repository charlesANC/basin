package br.unb.cic.comnet.streaming.basin.flexM3u8IO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class FlexiblePlayListWriter {
	
	public void write(PlayList playList, OutputStream output) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output))) {
			
			writer.write(M3u8Tags.HEADER + "\r\n");
			
			writeALine(M3u8Tags.TAG_VERSION, playList.getVersion(), writer);
			writeALine(M3u8Tags.TAG_TARGET_DURATION, playList.getTargetDuration(), writer);
			writeALine(M3u8Tags.TAG_MEDIA_SEQUENCE, playList.getMediaSequence(), writer);
			
			writeExtraInfo(playList.getExtraInfo(), writer);
			for(MediaSegment segment : playList.getSegments()) {
				writeExtraInfo(segment.getInfo(), writer);
				writer.write(segment.getUrl());
			}
		} catch (IOException e) {
			throw e;
		}
	}

	private void writeExtraInfo(List<ImmutablePair<String, String>> infos, Writer writer) throws IOException {
		for(ImmutablePair<String, String> info : infos) {
			writeALine(info.getKey(), info.getValue(), writer);
		}
	}
	
	private void writeALine(String key, String value, Writer writer) throws IOException {
		writer.write(key + ":" + value + "\r\n");
	}
	
	private void writeALine(String key, Integer value, Writer writer) throws IOException {
		writer.write(key + ":" + value.toString() + "\r\n");
	}	
	
	private void writeALine(String key, Float value, Writer writer) throws IOException {
		writer.write(key + ":" + value.toString() + "\r\n");
	}	
}