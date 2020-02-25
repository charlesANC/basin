package br.unb.cic.comnet.streaming.basin.flexM3u8IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * This is a non-compliance implementation of a m3u8 parser
 * It assumes catching information is more important than the file being correct
 * @author CharlesAntonio
 */
public class FlexiblePlayListReader {

	public PlayList read(InputStream input) throws IOException {
		return createPlayList(readFile(input));
	}
	
	private PlayList createPlayList(List<ImmutablePair<String, String>> lines) {
		PlayList playList = new PlayList();
		MediaSegment currentSegment = null;
		
		for(ImmutablePair<String, String> line : lines) {
			if (line.getKey().equals(M3u8Tags.TAG_VERSION)) {
				playList.setVersion(Integer.valueOf(line.getValue()));
			}
			else if (line.getKey().equals(M3u8Tags.TAG_TARGET_DURATION)) {
				playList.setTargetDuration(Float.valueOf(line.getValue()));
			}
			else if (line.getKey().equals(M3u8Tags.TAG_MEDIA_SEQUENCE)) {
				playList.setMediaSequence(Integer.valueOf(line.getValue()));
			}
			else if (line.getKey().equals(M3u8Tags.TAG_PROGRAM_DATE) || 
						line.getKey().equals(M3u8Tags.TAG_SEGMENT_INFORMATION)) {
				if (currentSegment == null) {
					currentSegment = new MediaSegment();
				}
				if (line.getKey().equals(M3u8Tags.TAG_SEGMENT_INFORMATION)) {
					String duration = line.getValue().split(",")[0];
					currentSegment.setDuration(Float.valueOf(duration));
				} else {
					currentSegment.addInfo(line.getKey(), line.getValue());					
				}
			}
			else if (line.getKey().equals(M3u8Tags.URL)) {
				if (currentSegment == null) {
					currentSegment = new MediaSegment();
				}
				currentSegment.setUrl(line.getValue());
				playList.addSegment(currentSegment);
				currentSegment = null;
			}
			else {
				playList.addExtraInfo(line.getKey(), line.getValue());
			}
		}
		return playList;
	}
	
	private List<ImmutablePair<String, String>> readFile(InputStream input) throws IOException {
		List<ImmutablePair<String, String>> lines = new ArrayList<ImmutablePair<String, String>>();
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(M3u8Tags.TAG_START)) {
					String[] parts = line.split(":", 2);
					if (parts.length == 2) {
						lines.add(new ImmutablePair<String, String>(parts[0], parts[1]));						
					}
				} else {
					lines.add(new ImmutablePair<String, String>(M3u8Tags.URL, line));
				}
			}
			return lines;
		} catch (IOException e) {
			throw e;
		}
	}
}
