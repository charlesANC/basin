package br.unb.cic.comnet.streaming.basin.flexM3u8IO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

public class FlexiblePlayListWriter {
	
	public void write(PlayList playList, OutputStream output) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output))) {
			writer.write(M3u8Tags.HEADER + "\n");
			
			writeALine(M3u8Tags.TAG_VERSION, playList.getVersion(), writer);
			writeALine(M3u8Tags.TAG_TARGET_DURATION, playList.getTargetDuration(), writer);
			writeALine(M3u8Tags.TAG_MEDIA_SEQUENCE, playList.getMediaSequence(), writer);
			
			for(MediaSegment segment : playList.getSegments()) {
				writeSegment(segment, writer);
			}
			
//			writer.write(M3u8Tags.TAG_END_OF_LIST + "\r\n");
		} catch (IOException e) {
			throw e;
		}
	}
	
	private void writeSegment(MediaSegment segment, Writer writer) throws IOException {
		writeALine(M3u8Tags.TAG_SEGMENT_INFORMATION, segment.getDuration(), writer);
		writer.write(segment.getUrl() + "\r\n");
	}

	private void writeALine(String key, Integer value, Writer writer) throws IOException {
		writer.write(key + ":" + value.toString() + "\n");
	}	
	
	private void writeALine(String key, Float value, Writer writer) throws IOException {
		writer.write(String.format(Locale.US, "%s:%.2f,\n", key, value));
	}	
}