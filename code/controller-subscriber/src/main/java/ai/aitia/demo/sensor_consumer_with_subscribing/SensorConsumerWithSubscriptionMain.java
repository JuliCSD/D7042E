package ai.aitia.demo.sensor_consumer_with_subscribing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.application.skeleton.subscriber.ConfigEventProperites;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@EnableConfigurationProperties(ConfigEventProperites.class)
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, SensorConsumerConstants.BASE_PACKAGE, LampProviderConstants.BASE_PACKAGE})
public class SensorConsumerWithSubscriptionMain {
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(SensorConsumerWithSubscriptionMain.class, args);
    }
   
}
