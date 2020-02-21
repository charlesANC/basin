package br.unb.cic.comnet.streaming.basin.services;

import java.io.Serializable;
import java.net.URL;

public class URLForTranscoding implements Serializable, Comparable<URLForTranscoding> {
	private static final long serialVersionUID = 1L;
	
	private URL url;
	private Boolean wasTranscoded;
	
	public Boolean getWasTranscoded() {
		return wasTranscoded;
	}
	public void setWasTranscoded(Boolean wasTranscoded) {
		this.wasTranscoded = wasTranscoded;
	}

	public URL getUrl() {
		return url;
	}

	public URLForTranscoding(URL url) {
		this.url = url;
		this.wasTranscoded = false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URLForTranscoding other = (URLForTranscoding) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(URLForTranscoding o) {
		return this.url.getFile().compareTo(o.getUrl().getFile());
	}
}
