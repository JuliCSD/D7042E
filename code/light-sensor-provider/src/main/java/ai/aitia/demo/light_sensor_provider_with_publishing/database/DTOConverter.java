package ai.aitia.demo.light_sensor_provider_with_publishing.database;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import ai.aitia.demo.smart_city_common.dto.LightSensorResponseDTO;
import ai.aitia.demo.light_sensor_provider_with_publishing.entity.LightSensor;

public class DTOConverter {

	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static LightSensorResponseDTO convertLightSensorToLightSensorResponseDTO(final LightSensor lightSensor) {
		Assert.notNull(lightSensor, "lightSensor is null");
		return new LightSensorResponseDTO(lightSensor.getId(), lightSensor.getName(), lightSensor.getValue());
	}
	
	//-------------------------------------------------------------------------------------------------
	public static List<LightSensorResponseDTO> convertLightSensorListToLightSensorResponseDTOList(final List<LightSensor> lightSensors) {
		Assert.notNull(lightSensors, "lightSensor list is null");
		final List<LightSensorResponseDTO> lightSensorResponse = new ArrayList<>(lightSensors.size());
		for (final LightSensor lightSensor : lightSensors) {
			lightSensorResponse.add(convertLightSensorToLightSensorResponseDTO(lightSensor));
		}
		return lightSensorResponse;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public DTOConverter() {
		throw new UnsupportedOperationException(); 
	}
}
