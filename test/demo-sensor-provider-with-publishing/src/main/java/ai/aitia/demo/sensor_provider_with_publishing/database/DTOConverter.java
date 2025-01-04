package ai.aitia.demo.sensor_provider_with_publishing.database;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import ai.aitia.demo.sensor_common.dto.SensorResponseDTO;
import ai.aitia.demo.sensor_provider_with_publishing.entity.Sensor;

public class DTOConverter {

	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static SensorResponseDTO convertSensorToSensorResponseDTO(final Sensor sensor) {
		Assert.notNull(sensor, "sensor is null");
		return new SensorResponseDTO(sensor.getId(), sensor.getBrand(), sensor.getColor());
	}
	
	//-------------------------------------------------------------------------------------------------
	public static List<SensorResponseDTO> convertSensorListToSensorResponseDTOList(final List<Sensor> sensors) {
		Assert.notNull(sensors, "sensor list is null");
		final List<SensorResponseDTO> sensorResponse = new ArrayList<>(sensors.size());
		for (final Sensor sensor : sensors) {
			sensorResponse.add(convertSensorToSensorResponseDTO(sensor));
		}
		return sensorResponse;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public DTOConverter() {
		throw new UnsupportedOperationException(); 
	}
}
