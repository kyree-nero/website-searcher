package org.kyree.wss.services.domain;

public class SearchResponse {
	private SearchRequest request;
	private Boolean hasTerm;
	private Long threadId;
	private Boolean readable;
	private Boolean timedOut;
	private Boolean unknownHost;
	private Boolean unexpectedStatusCode;
	private Boolean sslHandshakeIssue;
	
	
	
	public Boolean getSslHandshakeIssue() {
		return sslHandshakeIssue;
	}
	public void setSslHandshakeIssue(Boolean sslHandshakeIssue) {
		this.sslHandshakeIssue = sslHandshakeIssue;
	}
	public Boolean getUnexpectedStatusCode() {
		return unexpectedStatusCode;
	}
	public void setUnexpectedStatusCode(Boolean unexpectedStatusCode) {
		this.unexpectedStatusCode = unexpectedStatusCode;
	}
	public Boolean getUnknownHost() {
		return unknownHost;
	}
	public void setUnknownHost(Boolean unknownHost) {
		this.unknownHost = unknownHost;
	}
	public Boolean getTimedOut() {
		return timedOut;
	}
	public void setTimedOut(Boolean timedOut) {
		this.timedOut = timedOut;
	}
	public SearchRequest getRequest() {
		return request;
	}
	public void setRequest(SearchRequest request) {
		this.request = request;
	}
	public Boolean getHasTerm() {
		return hasTerm;
	}
	public void setHasTerm(Boolean hasTerm) {
		this.hasTerm = hasTerm;
	}
	public Long getThreadId() {
		return threadId;
	}
	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}
	
	public Boolean getReadable() {
		return readable;
	}
	public void setReadable(Boolean readable) {
		this.readable = readable;
	}
	@Override
	public String toString() {
		return "SearchResponse [request=" + request + ", hasTerm=" + hasTerm + ", threadId=" + threadId + ", readable="
				+ readable + ", timedOut=" + timedOut + ", unknownHost=" + unknownHost + "]";
	}
	
	
	
}
