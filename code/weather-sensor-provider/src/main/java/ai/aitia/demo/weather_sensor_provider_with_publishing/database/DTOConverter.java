package ai.aitia.demo.weather_sensor_provider_with_publishing.database;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import ai.aitia.demo.smart_city_common.dto.WeatherSensorResponseDTO;
import ai.aitia.demo.weather_sensor_provider_with_publishing.entity.WeatherSensor;

public class DTOConverter {

	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static WeatherSensorResponseDTO convertWeatherSensorToWeatherSensorResponseDTO(final WeatherSensor weatherSensor) {
		Assert.notNull(weatherSensor, "weatherSensor is null");
		return new WeatherSensorResponseDTO(weatherSensor.getId(), weatherSensor.getTemperature(), weatherSensor.getHumidity(), weatherSensor.getPressure(), weatherSensor.getWind());
	}
	
	//-------------------------------------------------------------------------------------------------
	public static List<WeatherSensorResponseDTO> convertWeatherSensorListToWeatherSensorResponseDTOList(final List<WeatherSensor> weatherSensors) {
		Assert.notNull(weatherSensors, "weatherSensor list is null");
		final List<WeatherSensorResponseDTO> weatherSensorResponse = new ArrayList<>(weatherSensors.size());
		for (final WeatherSensor weatherSensor : weatherSensors) {
			weatherSensorResponse.add(convertWeatherSensorToWeatherSensorResponseDTO(weatherSensor));
		}
		return weatherSensorResponse;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public DTOConverter() {
		throw new UnsupportedOperationException(); 
	}
}
