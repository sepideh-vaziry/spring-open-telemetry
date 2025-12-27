package com.example.springopentelemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

  @GetMapping("/greet/{name}")
  public String greet(@PathVariable String name) {
    LOGGER.info("Greeting user: {}", name);

    sleep(50L); // Simulate a small delay

    return "Hello, " + name + "!";
  }

  @GetMapping("/slow")
  public String slow() {
    LOGGER.info("Starting slow operation");

    sleep(500L);

    LOGGER.info("Slow operation completed");

    return "Done!";
  }

  @GetMapping("/very-slow")
  public String verySlow() {
    LOGGER.info("Starting very slow operation");

    sleep(2000L);

    LOGGER.info("Very slow operation completed");

    return "Done!";
  }

  private void sleep(Long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
}
