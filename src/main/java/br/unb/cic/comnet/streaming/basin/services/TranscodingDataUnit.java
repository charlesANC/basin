package br.unb.cic.comnet.streaming.basin.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TranscodingDataUnit {
	private URL m3u8Url;
	private List<URLForTranscoding> urls;
	private URLForTranscoding lastTranscoded;
	private Long lastModified;
	
	public Integer getNumOfLinks() {
		return urls.size();
	}
	
	public Long getLastModified() {
		return lastModified;
	}
	
	public URLForTranscoding getNextToTranscode() {
		if (urls.isEmpty()) {
			return null;
		}
		
		if (lastTranscoded == null) {
			lastTranscoded = urls.get(urls.size() - 1);
		} else {
			int lastIndex = urls.indexOf(lastTranscoded);
			if (lastIndex < urls.size()) {
				lastTranscoded = urls.get(urls.indexOf(lastTranscoded) + 1);
			} else {
				lastTranscoded = null;
			}
		}
		return lastTranscoded;
	}		
	
	public TranscodingDataUnit(URL m3u8Url) {
		this.m3u8Url = m3u8Url;
		this.urls = new ArrayList<URLForTranscoding>();
		this.lastModified = -1L;
	}
	
	// FIXME: figure out how to know if a m3u8 url was updated.
	// maybe it is not necessary due nextToTranscode behavior. Need to check out.
	public boolean updateLinks() throws IOException {
		HttpURLConnection m3u8Connection = (HttpURLConnection) m3u8Url.openConnection();
		Long newLastModified = m3u8Connection.getLastModified();
		//if (newLastModified > lastModified) {
			updateM3u8links(m3u8Connection);
			lastModified = newLastModified;
			return true;
		//}
		//return false;
	}
	
	private void updateM3u8links(HttpURLConnection m3u8Connection) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(m3u8Connection.getInputStream()));) {
			String line = null;
			while((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					URLForTranscoding newUrl = new URLForTranscoding(new URL(line)); 
					if (!urls.contains(newUrl)) urls.add(newUrl);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}	
}
