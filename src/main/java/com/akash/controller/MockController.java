package com.akash.controller;

import com.akash.constant.Route;
import com.akash.context.AppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

  private static final Logger LOGGER = LogManager.getLogger(MockController.class);

  @GetMapping(path = Route.MOCK_API, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> mockApi() {

    HttpStatus.Series httpStatus = HttpStatus.Series.resolve(AppContext.HTTP_STATUS_SERIES);

    if (httpStatus == HttpStatus.Series.SUCCESSFUL) {
      // return 200 response
      return new ResponseEntity("This is Success Mock Response", HttpStatus.OK);

    } else if (httpStatus == HttpStatus.Series.SERVER_ERROR) {

      // return 5xx response
      return new ResponseEntity("This is 5xx Mock Response", HttpStatus.INTERNAL_SERVER_ERROR);

    } else if (httpStatus == HttpStatus.Series.CLIENT_ERROR) {

      // return 4xx response
      return new ResponseEntity("This is 4xx Mock Response", HttpStatus.BAD_REQUEST);
    } else {

      // make request time out
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        LOGGER.error("thread interrupted ", e);
      }
      return new ResponseEntity("This is Timed out Mock Response", HttpStatus.OK);
    }
  }
}
