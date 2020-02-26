package br.unb.cic.comnet.streaming.basin.flexM3u8IO;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class PlayList {
	public static final int NUM_MAX_SEGMENTS = 14;	
	
	private Integer version;
	private Float targetDuration;
	private Integer mediaSequence;
	private List<ImmutablePair<String, String>> extraInfo;
	private List<MediaSegment> segments;
	
	private int maxNumSegments;
	
	PlayList() {
		this(NUM_MAX_SEGMENTS);
	}
	
	PlayList(int maxNumSegments) {
		this.maxNumSegments = maxNumSegments;
		this.segments = new ArrayList<MediaSegment>();
		this.extraInfo = new ArrayList<ImmutablePair<String, String>>();
	}
	
	public Integer getVersion() {
		return version;
	}
	void setVersion(Integer version) {
		this.version = version;
	}
	
	public Float getTargetDuration() {
		return targetDuration;
	}
	void setTargetDuration(Float targetDuration) {
		this.targetDuration = targetDuration;
	}
	
	public Integer getMediaSequence() {
		return mediaSequence;
	}
	public void incrementMediaSequence() {
		mediaSequence++;
	}
	void setMediaSequence(Integer mediaSequence) {
		this.mediaSequence = mediaSequence;
	}
	
	public List<ImmutablePair<String, String>> getExtraInfo() {
		return new ArrayList<ImmutablePair<String, String>>(extraInfo);
	}
	void addExtraInfo(String key, String value) {
		extraInfo.add(new ImmutablePair<String, String>(key, value));
	}

	public List<MediaSegment> getSegments() {
		return new ArrayList<MediaSegment>(segments);
	}
	public void addSegment(String url, float duration, List<ImmutablePair<String, String>> extraInfo) {
		MediaSegment segment = new MediaSegment();
		segment.setUrl(url);
		segment.setDuration(duration);
		segment.setInfo(extraInfo);
		addSegment(segment);
	}
	void addSegment(MediaSegment segment) {
		if (segments.size() >= maxNumSegments) {
			segments.remove(0);
		}
		segments.add(segment);
	}
	
	
	public PlayList cloneWithoutSegments() {
		PlayList playList = new PlayList();
		playList.mediaSequence = this.getMediaSequence();
		playList.targetDuration = this.getTargetDuration();
		playList.version = this.getVersion();
		playList.extraInfo = this.getExtraInfo();
		return playList;
	}
}
