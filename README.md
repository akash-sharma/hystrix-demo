# hystrix-demo

Hystrix as a Circuit breaker in Java

Why to use circuit breaker:
(1) Avoids overloading the unhealthy downstream service so that it can recover
(2) It stops cascading failures across services in a distributed environment.
(3) Helps to create a system that can survive gracefully when key services are either unavailable or have high latency
(4) It provides fallback options. This helps to add proper error message or error handlings.

Developed by Netflix engineering team.

All fallback logic put into a command object.

HystrixCommand : for blocking I/O

HystrixObservableCommand : for non blocking I/O


===========================================


--> pom.xml changes

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.2.RELEASE</version>
        <relativePath />
    </parent>
    <dependencies>

        <!--Hystrix dependencies -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.1.2.RELEASE</version>
        </dependency>
        <!--Other spring boot dependencies -->
    </dependencies>


--> Hystrix artifact for Spring boot versions :

Spring boot version 1.x.x --> spring-cloud-starter-hystrix

Spring boot version 2.x.x --> spring-cloud-starter-netflix-hystrix 


--> Add annotation in your Main Spring boot configuration file :

@EnableCircuitBreaker


===========================================


=> Understanding Hystrix Properties

--> execution.isolation.strategy

There are two types of isolation strategies :
THREAD — it executes on a separate thread and concurrent requests are limited by
        the number of threads in hystrix thread-pool
	
SEMAPHORE — it executes on the calling thread and concurrent requests are limited by the semaphore count

Default value is THREAD


(1) For blocking I/O, use a thread-isolated HystrixCommand.
(2) For nonblocking I/O, use a semaphore-isolated HystrixObservableCommand.
(3) The only time you should use SEMAPHORE isolation for HystrixCommand to avoid the overhead of separate threads.
(4) The advantage of the thread pool approach is that requests that are passed to application component can be timed out, something that is not possible when using semaphores.


--> execution.isolation.thread.timeoutInMilliseconds

Time in milliseconds after which the hystrix will timeout.
This property will only works when isolation strategy is THREAD.
Default value is 1000.


Hystrix thread pool properties :
coreSize              (default = 10)

maximumSize    (default = 10)

maxQueueSize   (default = -1)


--> execution.isolation.semaphore.maxConcurrentRequests

Maximum number of requests allowed in HystrixCommand when using SEMAPHORE.
If this maximum concurrent limit is hit then subsequent requests will be rejected.
Default value is 10.


--> fallback.isolation.semaphore.maxConcurrentRequests

Maximum number of fallback execution allowed in HystrixCommand when using SEMAPHORE.
If the maximum concurrent limit is hit then subsequent requests will be rejected and
   an exception thrown since no fallback could be retrieved.
Default value is 10.


--> How to calculate number of THREADs in Thread pool or count of SEMAPHORE

Theoretical formula for calculating the size is:

Requests per second at peak when healthy × 99th percentile latency in seconds + some breathing room

For Example :
Requests per second per instance at peak time = 60
99th % Latency = 200 ms = 0.2 seconds
Number of threads = 60 x 0.2 + 5 (some extra space) = 17


--> How Hystrix trips circuit

Within a timespan of duration metrics.rollingStats.timeInMilliseconds ,
the percentage of actions resulting in a handled exception exceeds errorThresholdPercentage ,
provided also that the number of actions through the circuit in the timespan is at least requestVolumeThreshold.


--> circuitBreaker.requestVolumeThreshold

Minimum number of requests in a rolling window that will trip the circuit.
Default value is 20.

--> circuitBreaker.sleepWindowInMilliseconds

After tripping the circuit, the amount of time to reject requests before allowing attempts again to determine if the circuit should again be closed.
Default value is 5000.


--> circuitBreaker.errorThresholdPercentage

Error percentage at or above which the circuit should trip open and start short-circuiting requests to fallback logic.
Default value is 50.


--> metrics.rollingStats.timeInMilliseconds

Duration of the statistical rolling window, in milliseconds.
Default value is 10000.

--> circuitBreaker.enabled

Whether a circuit breaker will be used to track health and to short-circuit requests if it trips.
Default value is true


Note : Hystrix fallback logic will only be executed any unhandled exception of Annotated method.


===========================================

Hystrix Dashboard :

--> pom.xml changes

        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
			<version>2.1.2.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>


--> Add annotation in your Main Spring boot configuration file :

@EnableHystrixDashboard


--> To enable the Hystrix metrics stream, add to application properties file :

management.endpoints.web.exposure.include=hystrix.stream


===========================================

    // Reload Hystrix properties at runtime
	public void loadProperty(String propertyName, String propertyValue) {

		ConcurrentCompositeConfiguration config =
			(ConcurrentCompositeConfiguration) ConfigurationManager.getConfigInstance();
		config.setOverrideProperty(propertyName, propertyValue);
		Hystrix.reset();
		HystrixPlugins.reset();
	}


===========================================