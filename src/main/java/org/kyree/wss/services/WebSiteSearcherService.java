package org.kyree.wss.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.LongStream;

import javax.xml.ws.WebServiceException;

import org.kyree.wss.services.domain.SearchRequest;
import org.kyree.wss.services.domain.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSiteSearcherService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSiteSearcherService.class);
	
	public static final Long MAXTHREADS = 20L;
	public static final Integer MAXTIMEOUTSPERSITE = 3;
	
	@Autowired WebSiteSearchReader reader;
	@Autowired WebSiteSearcherResultWriter writer;
	
	
	public void execute(String fileName, String term) {
		
		
			Map<WebSiteSearcherWorker, FutureTask<Boolean>> pool = new HashMap<WebSiteSearcherWorker, FutureTask<Boolean>>();
			
			LOGGER.info("start");
			
			//String fileName = "urls.txt";//"classpath:urls.txt";
	
			final String lowercaseTerm = term.toLowerCase();
			try {
				term = term.toLowerCase();
//				URL root = getClass().getProtectionDomain().getCodeSource().getLocation();
//				String path = (new File(root.toURI())).getParentFile().getPath();
//				File file = new File(path + File.separator + fileName);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)));
//				BufferedReader bReader = new BufferedReader(new FileReader(fileName));
				
				reader.setReader(bReader);
				
				
				
				LongStream.range(1,MAXTHREADS).forEach(i -> createNewTask(pool, reader, writer, lowercaseTerm, i));
				
				pool.entrySet().stream().forEach(f -> new Thread(f.getValue()).start());
			
				
				
				
				
				Map<WebSiteSearcherWorker, List<String>> timedOutRecord = new HashMap<WebSiteSearcherWorker, List<String>>();
				
				while(pool.size() != 0) {
					LOGGER.info("remaining  workers : " + pool.size());
					
					
					List<WebSiteSearcherWorker> doneWorkers = new ArrayList<WebSiteSearcherWorker>();
					List<WebSiteSearcherWorker> cancelWorkers = new ArrayList<WebSiteSearcherWorker>();
					
					
					pool.entrySet().stream().forEach(f -> {
						boolean timedOut = false;
						WebSiteSearcherWorker worker = f.getKey();
						String currentURL = worker.getCurrentSearchRequest() ==  null ? null :worker.getCurrentSearchRequest().getUrl();
						SearchRequest currentSearchRequest = worker.getCurrentSearchRequest();
						try {
							LOGGER.debug("waiting for worker " + f.getKey().getId());
							try {
								
								f.getValue().get(20, TimeUnit.SECONDS);
								
								
								if(timedOutRecord.containsKey(worker)){
									resetTimeout(timedOutRecord, worker);
								}
							} catch (TimeoutException e) {
								worker.setPauseReads(true);
								
								
								LOGGER.debug("worker "+worker.getId()+"  timed out");
								timedOut = true;
								
								if(!timedOutRecord.containsKey(worker)){
									List<String> urls = new ArrayList<String>();
									urls.add(currentURL);
									timedOutRecord.put(worker,  urls);
									LOGGER.warn("added time out record for worker "+worker.getId()+"  with url: " + currentSearchRequest.getUrl());
								}else {
									List<String> currentURLOccurrences = timedOutRecord.get(worker);
									if(worker.getFinishedRequest()){
										synchronized(worker) {
											worker.notify();
										}
										resetTimeout(timedOutRecord, worker);
									}else if(currentURLOccurrences.contains(currentURL)){
										if(currentURLOccurrences.size() >= MAXTIMEOUTSPERSITE) {
											cancelWorkers.add(worker);
											LOGGER.error("queued cancel for worker " + worker.getId());
											worker.setUrlAtTimeOfCancel(currentURL);
											timedOutRecord.remove(worker);
											LOGGER.debug("reset time out record for worker "+worker.getId());
											
										}else {
											currentURLOccurrences.add(currentURL);
											LOGGER.warn("increased time out record for worker "+worker.getId()+"  with url: " + currentURL + " to " + timedOutRecord.get(f.getKey()).size());
										}
									}else {
										List<String> urls = new ArrayList<String>();
										urls.add(currentURL);
										timedOutRecord.put(worker,  urls);
										LOGGER.warn("replaced time out record for worker "+worker.getId()+"  with url: " + currentURL);
									}
								}
							}
						} catch (InterruptedException | ExecutionException e) {
							throw new WebServiceException(e);
						}finally {
							if(timedOut == false) {
								LOGGER.debug("done  with worker " + f.getKey().getId());
								doneWorkers.add(f.getKey());
							}
							LOGGER.debug(doneWorkers.size() + "  workers queued to be dropped");
						}
					});	
					
					
					doneWorkers.stream().forEach(d -> pool.remove(d));
					doneWorkers.clear();
					
					cancelWorkers.stream().forEach(c -> {
						pool.get(c).cancel(true); 
						SearchResponse response = new SearchResponse();
						response.setRequest(c.getCurrentSearchRequest());
						response.setThreadId(c.getId());
						response.setTimedOut(true);
						writer.write(response);
						pool.remove(c);
						LOGGER.info("cancelled worker " + c.getId());
						new Thread(createNewTask(pool, reader, writer, lowercaseTerm, c.getId())).start();
						LOGGER.info("replaced worker " + c.getId());
						
						});
					cancelWorkers.clear();
					
				}
				LOGGER.debug("remaining  workers : " + pool.size());
				LOGGER.info(
						"processed :"+writer.getCount() + "/"+reader.getCount()+" "
					  + "has-term:  " + writer.getHasTerm() + "  "
					  + "not readable: " + writer.getNotReadable() + "  "
					  + "timed out: " + writer.getTimedOut() + "  "
					  + "unknown host: " + writer.getUnknownHost() );
			} catch (Throwable e) {
				throw new WebServiceException(e);
			}finally {
				LOGGER.info("done");
				
			}

		
	}
	
	
	private FutureTask<Boolean> createNewTask(Map<WebSiteSearcherWorker, FutureTask<Boolean>> pool, WebSiteSearchReader reader, WebSiteSearcherResultWriter writer, String term, Long i) {
		WebSiteSearcherWorker worker = new WebSiteSearcherWorker();
		worker.setReader(reader);
		worker.setWriter(writer);
		worker.setTerm(term);
		worker.setId(i);
		
		FutureTask<Boolean> futureTask = new FutureTask<>(worker);
		pool.put(worker, futureTask);
		return futureTask;
	}
	
	 
	private void resetTimeout(Map<WebSiteSearcherWorker, List<String>> timedOutRecord , WebSiteSearcherWorker worker) {
		timedOutRecord.remove(worker);
		worker.setPauseReads(false);
		LOGGER.debug("reset time out record for worker "+worker.getId());
	}
}
