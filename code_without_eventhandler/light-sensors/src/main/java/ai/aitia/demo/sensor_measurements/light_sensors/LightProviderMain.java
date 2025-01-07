package ai.aitia.demo.sensor_measurements.light_sensors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import ai.aitia.demo.sensor.measurements.common.EFCommonConstants;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EFCommonConstants.BASE_PACKAGE})
public class LightProviderMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(LightProviderMain.class, args);
	}	
}
