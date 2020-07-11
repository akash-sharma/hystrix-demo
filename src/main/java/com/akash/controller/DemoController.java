package com.akash.controller;

import com.akash.constant.Constant;
import com.akash.constant.Route;
import com.akash.service.external.OfferExternalApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

  private static final Logger LOGGER = LogManager.getLogger(DemoController.class);

  @Autowired private OfferExternalApiService offerExternalApiService;

  @GetMapping(path = Route.DEMO_API, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> demoApi() {

    String offerApiResponse = offerExternalApiService.offerApi();
    String demoResponse = Constant.SUCCESS + "-" + offerApiResponse;

    return new ResponseEntity(demoResponse, HttpStatus.OK);
  }
}
