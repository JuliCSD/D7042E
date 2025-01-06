package ai.aitia.demo.sensor_measurements.light_sensors.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.aitia.demo.sensor.measurements.common.EFDataService;
import ai.aitia.demo.sensor.measurements.common.dto.SensorDetailsDTO;
import ai.aitia.demo.sensor.measurements.common.dto.SensorDetailsDTO.Builder;
import ai.aitia.demo.sensor.measurements.common.dto.SensorDetailsListDTO;

@Component
public class LightService {
	
	//=================================================================================================
	// members
	
	@Autowired
	private EFDataService dataService;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public SensorDetailsListDTO getHourlySensorDetails(final long building, final long tsStart, final long tsEnd) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(tsStart), ZoneOffset.UTC);
		final LocalDateTime startHour = start.withMinute(0).withSecond(0);
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(tsEnd), ZoneOffset.UTC);
		final LocalDateTime endHour = end.withMinute(0).withSecond(0);
		
		final List<SensorDetailsDTO> sensorDetails = new ArrayList<>();
		for (LocalDateTime timestamp = startHour; timestamp.isBefore(endHour) || timestamp.isEqual(endHour); timestamp = timestamp.plusHours(1)) {
			if (timestamp.isAfter(LocalDateTime.now())) {
				break;
			}
			final Builder sensorDetailsDTOBuilder = new SensorDetailsDTO.Builder(timestamp.toEpochSecond(ZoneOffset.UTC), building);
			sensorDetails.add(sensorDetailsDTOBuilder.setInTemp(dataService.getLightTemperature(timestamp))
								   					 .build());
		}
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		return new SensorDetailsListDTO(sensorDetails, sensorDetails.get(0).getTimestamp(), sensorDetails.get(sensorDetails.size() - 1).getTimestamp());
	}
}
