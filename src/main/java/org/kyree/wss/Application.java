package org.kyree.wss;

import org.kyree.wss.services.WebSiteSearcherService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		WebSiteSearcherService service =  context.getBean(WebSiteSearcherService.class);
		String term = "Hello";
		if(args  != null && args.length != 0) {
			term  =  args[0];
		}
		service.execute("urls.txt", term);
	}
}
