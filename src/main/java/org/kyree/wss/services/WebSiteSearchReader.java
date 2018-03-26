package org.kyree.wss.services;

import java.io.BufferedReader;
import java.io.IOException;

import org.kyree.wss.services.domain.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebSiteSearchReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSiteSearchReader.class);
	
	private Integer count = 0;
	private BufferedReader reader;
	
	
	public synchronized SearchRequest readNextEntry() throws IOException {
		String line = reader.readLine();
		if(line == null) {
			LOGGER.debug("no information read");
			return null;
		}
		if(count == 0) {
			line = reader.readLine(); //dismiss header
			if(line == null) {
				LOGGER.debug("no information read");
				return null;
			}
		}
		
		
		String[] items = line.split(",");
		SearchRequest request = new SearchRequest();
		request.setUrl(items[1]);
		count++;
		return request;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	

	
	
}
