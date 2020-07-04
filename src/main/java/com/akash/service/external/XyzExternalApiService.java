package com.akash.service.external;

import com.akash.constant.Route;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class XyzExternalApiService {

  private static final Logger LOGGER = LogManager.getLogger(XyzExternalApiService.class);

  @Autowired private RestTemplate restTemplate;

  @Value("${xyz.api.baseurl}")
  private String baseUrl;

  @HystrixCommand(fallbackMethod = "getDefaultXyzApiResponse", commandKey = "xyzCommandKey")
  public String xyzApi() {

    try {
      String response = restTemplate.getForObject(baseUrl + Route.MOCK_API, String.class);
      LOGGER.info("xyz api response : {}", response);
      return response;
    } catch (RestClientException e) {
      return getErrorResponse(e);
    }
  }

  // fallback method of xyzApi hystrix
  private String getDefaultXyzApiResponse() {

    LOGGER.info("getDefaultXyzApiResponse() executed");
    // push fallback metrics
    return "This is fallback Response";
  }

  // handling 4xx response as success
  private String getErrorResponse(RestClientException e) {

    if (e instanceof HttpClientErrorException) {

      HttpClientErrorException ex = (HttpClientErrorException) e;
      HttpStatus.Series httpStatusSeries = ex.getStatusCode().series();
      if (httpStatusSeries == HttpStatus.Series.SERVER_ERROR) {
        throw ex;
      }
      String errorResponse = ex.getResponseBodyAsString();
      LOGGER.info("error response body : {}", errorResponse);
      return errorResponse;
    } else {
      throw e;
    }
  }
}
