package ai.aitia.demo.light_sensor_provider_with_publishing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, LightSensorProviderWithPublishingConstants.BASE_PACKAGE})
public class LightSensorProviderWithPublishingMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(LightSensorProviderWithPublishingMain.class, args);
	}	
}
