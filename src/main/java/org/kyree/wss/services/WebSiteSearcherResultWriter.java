package org.kyree.wss.services;

import org.kyree.wss.services.domain.SearchResponse;
import org.springframework.stereotype.Component;

@Component
public class WebSiteSearcherResultWriter { 
	
	Integer count = 0;
	Integer notReadable = 0;
	Integer timedOut = 0;
	Integer hasTerm = 0;
	Integer unknownHost = 0;
	Integer unexpectedResponseCode = 0;
	Integer sslHandshakeIssue = 0;
	
	public void write(SearchResponse response) {
		System.out.println(response);
		count++;
		if(response.getReadable() != null && !response.getReadable()) {
			notReadable++;
		}
		if(response.getTimedOut() != null && response.getTimedOut()) {
			timedOut++;
		}
		if(response.getHasTerm() != null && response.getHasTerm()) {
			hasTerm++;
		}
		if(response.getUnknownHost() != null && response.getUnknownHost()) {
			unknownHost++;
		}
		if(response.getUnexpectedStatusCode() != null && response.getUnexpectedStatusCode()) {
			unexpectedResponseCode++;
		}
		if(response.getSslHandshakeIssue() != null && response.getSslHandshakeIssue()) {
			sslHandshakeIssue++;
		}
		
	}

	
	
	public Integer getSslHandshakeIssue() {
		return sslHandshakeIssue;
	}



	public void setSslHandshakeIssue(Integer sslHandshakeIssue) {
		this.sslHandshakeIssue = sslHandshakeIssue;
	}



	

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getUnexpectedResponseCode() {
		return unexpectedResponseCode;
	}



	public void setUnexpectedResponseCode(Integer unexpectedResponseCode) {
		this.unexpectedResponseCode = unexpectedResponseCode;
	}



	public Integer getNotReadable() {
		return notReadable;
	}

	public void setNotReadable(Integer notReadable) {
		this.notReadable = notReadable;
	}

	public Integer getTimedOut() {
		return timedOut;
	}

	public void setTimedOut(Integer timedOut) {
		this.timedOut = timedOut;
	}

	public Integer getHasTerm() {
		return hasTerm;
	}

	public void setHasTerm(Integer hasTerm) {
		this.hasTerm = hasTerm;
	}

	public Integer getUnknownHost() {
		return unknownHost;
	}

	public void setUnknownHost(Integer unknownHost) {
		this.unknownHost = unknownHost;
	}
	
	
}
