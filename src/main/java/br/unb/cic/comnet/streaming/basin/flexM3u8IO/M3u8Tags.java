package br.unb.cic.comnet.streaming.basin.flexM3u8IO;

public class M3u8Tags {
	
	public static final String TAG_START = "#";
	
	public static final String URL = "URL";	
	public static final String HEADER = "#EXTM3U";	

	public static final String TAG_VERSION = "#EXT-X-VERSION";
	public static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
	public static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
	
	public static final String TAG_PROGRAM_DATE = "#EXT-X-PROGRAM-DATE-TIME";
	public static final String TAG_SEGMENT_INFORMATION = "#EXTINF";	
	
	public static final String TAG_PRE_FETCH = "#EXT-X-TWITCH-PREFETCH";
	
	public static final String TAG_END_OF_LIST = "#EXT-X-ENDLIST";
}
