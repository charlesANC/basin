package br.unb.cic.comnet.streaming.basin.flexM3u8IO;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class PlayList {
	
	private Integer version;
	private Float targetDuration;
	private Integer mediaSequence;
	private List<ImmutablePair<String, String>> extraInfo;
	private List<MediaSegment> segments;
	
	PlayList() {
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
	void addSegment(MediaSegment segment) {
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
