package ai.aitia.demo.weather_sensor_provider_with_publishing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, WeatherSensorProviderWithPublishingConstants.BASE_PACKAGE})
public class WeatherSensorProviderWithPublishingMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(WeatherSensorProviderWithPublishingMain.class, args);
	}	
}
