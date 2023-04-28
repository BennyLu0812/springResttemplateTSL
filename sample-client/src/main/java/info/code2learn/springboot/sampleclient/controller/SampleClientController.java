package info.code2learn.springboot.sampleclient.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/sampleclient")
public class SampleClientController {

	private static final Logger logger = LoggerFactory.getLogger(SampleClientController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${sampleserver.url}")
	private String sampleServerUrl;

	@GetMapping("/welcome")
	public ResponseEntity<String> welcome() {
		logger.info("ServerUrl:"+sampleServerUrl + "a/message");
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(sampleServerUrl + "a/message", String.class);
		return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
	}

	// http://localhost:9090/client/web/sampleclient/testWelcome
	@GetMapping("/testWelcome")
	public ResponseEntity<String> testwelcome() {
		logger.info("test welcome");
		return new ResponseEntity<String>("welcome", HttpStatus.OK);
	}
	
}
