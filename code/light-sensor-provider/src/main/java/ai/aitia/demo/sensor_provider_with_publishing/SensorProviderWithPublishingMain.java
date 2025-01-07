package ai.aitia.demo.sensor_provider_with_publishing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
// import org.springframework.scheduling.annotation.EnableScheduling;

import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
// @EnableScheduling
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, SensorProviderWithPublishingConstants.BASE_PACKAGE})
public class SensorProviderWithPublishingMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(SensorProviderWithPublishingMain.class, args);
	}	
}
