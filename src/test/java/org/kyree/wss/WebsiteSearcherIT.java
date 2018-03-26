package org.kyree.wss;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kyree.wss.services.WebSiteSearchReader;
import org.kyree.wss.services.WebSiteSearcherResultWriter;
import org.kyree.wss.services.WebSiteSearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
//@TestPropertySource("/application-test.properties")
public class WebsiteSearcherIT {
	@Autowired WebSiteSearcherService wssService;
	@Autowired WebSiteSearchReader reader;
	@Autowired WebSiteSearcherResultWriter writer;
	
	
	@Before public void before() {
		reader.setCount(0);
		writer.setCount(0);
		writer.setHasTerm(0);
		writer.setNotReadable(0);
		writer.setSslHandshakeIssue(0);
		writer.setTimedOut(0);
		writer.setUnexpectedResponseCode(0);
		writer.setUnknownHost(0);
	}
	
	@Ignore 
	@Test public void test() {
		wssService.execute("urls.txt", "the");
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
//		Assert.assertEquals(1, writer.getHasTerm().intValue());
//		Assert.assertEquals(1, writer.getNotReadable().intValue());
//		Assert.assertEquals(1, writer.getSslHandshakeIssue().intValue());
//		Assert.assertEquals(1, writer.getTimedOut().intValue());
//		Assert.assertEquals(1, writer.getUnexpectedReturnCode().intValue());
//		Assert.assertEquals(1, writer.getUnknownHost().intValue());
		
	}
	
	
	
	@Test public void testShortList() {
		wssService.execute("test.urls.txt", "the");
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
		
	}
	
	@Test public void testUnknownHost() {
		wssService.execute("test.unknownhost.urls.txt", "the");
		
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
		
		Assert.assertEquals(1, writer.getCount().intValue());
		Assert.assertEquals(1, writer.getUnknownHost().intValue());
		
	}
	
	@Test public void testTimedOut() {
		wssService.execute("test.timedout.urls.txt", "the");
		
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
		
		Assert.assertEquals(1, writer.getCount().intValue());
		Assert.assertEquals(1, writer.getTimedOut().intValue());
		
	}
	
	@Test public void testHasTerm() {
		wssService.execute("test.hasterm.urls.txt", "About");
		
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
		
		Assert.assertEquals(1, writer.getCount().intValue());
		Assert.assertEquals(1, writer.getHasTerm().intValue());
		
	}
	
	@Test public void testDoesNotHaveTerm() {
		wssService.execute("test.hasterm.urls.txt", "NOTTHERE");
		
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
		
		Assert.assertEquals(1, writer.getCount().intValue());
		Assert.assertEquals(0, writer.getHasTerm().intValue());
		
	}
	
	@Test public void testUnexpectedResponseCode() {
		wssService.execute("test.unexpectedresponsecode.url.txt", "the");
		
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
		
		Assert.assertEquals(1, writer.getCount().intValue());
		Assert.assertEquals(1, writer.getUnexpectedResponseCode().intValue());
		
	}
	
	@Test public void testSSLProblem() {
		wssService.execute("test.sslproblem.url.txt", "the");
		
		Assert.assertTrue(reader.getCount().intValue() > 0);
		Assert.assertEquals(reader.getCount().intValue(), writer.getCount().intValue());
		
		Assert.assertEquals(1, writer.getCount().intValue());
		Assert.assertEquals(1, writer.getSslHandshakeIssue().intValue());
		
	}
	
	
	
}
