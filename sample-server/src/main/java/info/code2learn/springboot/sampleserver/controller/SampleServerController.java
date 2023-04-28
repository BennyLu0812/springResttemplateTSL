package info.code2learn.springboot.sampleserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/a")
public class SampleServerController {

    private static final Logger logger = LoggerFactory.getLogger(SampleServerController.class);


    @GetMapping("/message")
    public ResponseEntity<String> message() {
        logger.info("Message from https server!");
        return new ResponseEntity<>("Message from https server.", HttpStatus.OK);
    }

    @GetMapping("/b")
    public ResponseEntity<String> helloServer() {
        logger.info("hello server!");

        return new ResponseEntity<String>("hello server!", HttpStatus.OK);
    }

}
