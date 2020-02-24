package br.unb.cic.comnet.streaming.basin.flexM3u8IO;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class MediaSegment {
	private String url;
	private List<ImmutablePair<String, String>> info;
	
	MediaSegment() {
		this.info = new ArrayList<ImmutablePair<String, String>>();
	}
	
	public String getUrl() {
		return url;
	}
	void setUrl(String url) {
		this.url = url;
	}
	
	public List<ImmutablePair<String, String>> getInfo() {
		return info;
	}
	void addInfo(String key, String value) {
		this.info.add(new ImmutablePair<String, String>(key, value));
	}
}
