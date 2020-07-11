package com.akash.context;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;

public class HystrixContextLoader {

  // Reload Hystrix properties at runtime
  public void loadProperty(String propertyName, String propertyValue) {

    ConcurrentCompositeConfiguration config =
        (ConcurrentCompositeConfiguration) ConfigurationManager.getConfigInstance();
    config.setOverrideProperty(propertyName, propertyValue);
    Hystrix.reset();
    HystrixPlugins.reset();
  }
}
