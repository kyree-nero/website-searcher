package org.kyree.wss.services.domain;

public class SearchRequest {
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "SearchRequest [url=" + url + "]";
	}
	
	
}
