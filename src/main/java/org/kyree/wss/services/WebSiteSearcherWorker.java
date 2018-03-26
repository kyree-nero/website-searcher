package org.kyree.wss.services;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.kyree.wss.services.domain.SearchRequest;
import org.kyree.wss.services.domain.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSiteSearcherWorker implements Callable<Boolean>{
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSiteSearcherWorker.class);
	 
	private String term;
	private WebSiteSearchReader reader;
	private WebSiteSearcherResultWriter writer;
	private Long id;
	private Boolean pauseReads = false;
	private String urlAtTimeOfCancel = null;
	private SearchRequest currentSearchRequest;
	private Boolean finishedRequest = false;
	
	
	public SearchRequest getCurrentSearchRequest() {
		return currentSearchRequest;
	}

	public void setCurrentSearchRequest(SearchRequest currentSearchRequest) {
		this.currentSearchRequest = currentSearchRequest;
	}

	public String getUrlAtTimeOfCancel() {
		return urlAtTimeOfCancel;
	}

	public void setUrlAtTimeOfCancel(String urlAtTimeOfCancel) {
		this.urlAtTimeOfCancel = urlAtTimeOfCancel;
	}

	public WebSiteSearchReader getReader() {
		return reader;
	}

	public void setReader(WebSiteSearchReader reader) {
		this.reader = reader;
	}

	
	public WebSiteSearcherResultWriter getWriter() {
		return writer;
	}

	public void setWriter(WebSiteSearcherResultWriter writer) {
		this.writer = writer;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	
	public Boolean getFinishedRequest() {
		return finishedRequest;
	}

	public void setFinishedRequest(Boolean finishedRequest) {
		this.finishedRequest = finishedRequest;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	
	public Boolean getPauseReads() {
		return pauseReads;
	}

	public void setPauseReads(Boolean pauseReads) {
		this.pauseReads = pauseReads;
	}

	@Override
	public Boolean call() throws Exception {
		LOGGER.info("worker id " + id + " start ");
		try {
			
			SearchRequest request = null;
			while(( request = reader.readNextEntry()) != null) {
				
				LOGGER.debug("worker id " + id + "   page " + request.getUrl() + "   read");
				LOGGER.debug(request.toString());
				currentSearchRequest = request;
				finishedRequest = false;
				try {
				SearchResponse response = new SearchResponse();
				
				response.setRequest(request);
				response.setThreadId(id);
				
				// do work
				try {
					response.setHasTerm(hasTerm(request.getUrl(), "http://", term));
					response.setReadable(true);
				}catch(UnknownHostException u) { 
					response.setUnknownHost(true);
				}catch(ConnectException u) { 
					response.setReadable(false);
				}catch(HttpStatusException h) {
					response.setUnexpectedStatusCode(true);
				}catch(SSLHandshakeException s) {
					response.setSslHandshakeIssue(true);
				}
				
				
				
				writer.write(response);
				
				LOGGER.debug("worker id "+ id + "   page " + request.getUrl() + "   end");
				}finally {
					
						this.currentSearchRequest = null;
						finishedRequest = true;
				}
				if(pauseReads) {
					synchronized(this) {
						wait();
					}
				}
			}
			LOGGER.debug("worker id "+ id + " done ");
			return true;
		}catch(Throwable t) {
			LOGGER.error("error in worker.");
			t.printStackTrace();
			return false;
		}finally {
			LOGGER.info("worker id " + id + " end ");
		}
	}
	
	private Boolean hasTerm(String url, String prefixToTry, String term) throws IOException {
		url = url.replaceAll("\"", "");
		if(!url.startsWith(prefixToTry) &&  !url.startsWith(prefixToTry)) {
			url = prefixToTry + url;
		}
		Document doc = Jsoup.connect(url).timeout(0).get();
		String text = doc.body().text(); 
		return text.toLowerCase().contains(term);
		
	}
	
	
}
