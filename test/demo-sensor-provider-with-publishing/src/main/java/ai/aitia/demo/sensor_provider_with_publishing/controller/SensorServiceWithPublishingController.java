package ai.aitia.demo.sensor_provider_with_publishing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.sensor_common.dto.SensorRequestDTO;
import ai.aitia.demo.sensor_common.dto.SensorResponseDTO;
import ai.aitia.demo.sensor_provider_with_publishing.SensorProviderWithPublishingConstants;
import ai.aitia.demo.sensor_provider_with_publishing.database.DTOConverter;
import ai.aitia.demo.sensor_provider_with_publishing.database.InMemorySensorDB;
import ai.aitia.demo.sensor_provider_with_publishing.entity.Sensor;
import eu.arrowhead.application.skeleton.publisher.event.EventTypeConstants;
import eu.arrowhead.application.skeleton.publisher.event.PresetEventType;
import eu.arrowhead.application.skeleton.publisher.service.PublisherService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
@RequestMapping(SensorProviderWithPublishingConstants.SENSOR_URI)
public class SensorServiceWithPublishingController {
	
	//=================================================================================================
	// members

	
	private static int counter = 0;
	
	@Autowired
	private InMemorySensorDB sensorDB;
	
	@Autowired
	private PublisherService publisherService;
	
	@Value( SensorProviderWithPublishingConstants.$SERVICE_LIMIT_WD )
	private int serviceLimit;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public List<SensorResponseDTO> getSensors(@RequestParam(name = SensorProviderWithPublishingConstants.REQUEST_PARAM_BRAND, required = false) final String brand,
													  @RequestParam(name = SensorProviderWithPublishingConstants.REQUEST_PARAM_COLOR, required = false) final String color) {
		++counter;
		
		publisherService.publish(PresetEventType.REQUEST_RECEIVED, Map.of(EventTypeConstants.EVENT_TYPE_REQUEST_RECEIVED_METADATA_REQUEST_TYPE, HttpMethod.GET.name()), SensorProviderWithPublishingConstants.SENSOR_URI);
		
		final List<SensorResponseDTO> response = new ArrayList<>();
		for (final Sensor sensor : sensorDB.getAll()) {
			boolean toAdd = true;
			if (brand != null && !brand.isBlank() && !sensor.getBrand().equalsIgnoreCase(brand)) {
				toAdd = false;
			}
			if (color != null && !color.isBlank() && !sensor.getColor().equalsIgnoreCase(color)) {
				toAdd = false;
			}
			if (toAdd) {
				response.add(DTOConverter.convertSensorToSensorResponseDTO(sensor));
			}
		}
		
		if (counter > serviceLimit) {
			System.exit(0);
		}
		
		return response;
	}
	
	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = SensorProviderWithPublishingConstants.BY_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public SensorResponseDTO getSensorById(@PathVariable(value = SensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
		return DTOConverter.convertSensorToSensorResponseDTO(sensorDB.getById(id));
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public SensorResponseDTO createSensor(@RequestBody final SensorRequestDTO dto) {
		if (dto.getBrand() == null || dto.getBrand().isBlank()) {
			throw new BadPayloadException("brand is null or blank");
		}
		if (dto.getColor() == null || dto.getColor().isBlank()) {
			throw new BadPayloadException("color is null or blank");
		}
		final Sensor sensor = sensorDB.create(dto.getBrand(), dto.getColor());
		
		return DTOConverter.convertSensorToSensorResponseDTO(sensor);
	}
	
	//-------------------------------------------------------------------------------------------------
	@PutMapping(path = SensorProviderWithPublishingConstants.BY_ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public SensorResponseDTO updateSensor(@PathVariable(name = SensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id, @RequestBody final SensorRequestDTO dto) {
		if (dto.getBrand() == null || dto.getBrand().isBlank()) {
			throw new BadPayloadException("brand is null or blank");
		}
		if (dto.getColor() == null || dto.getColor().isBlank()) {
			throw new BadPayloadException("color is null or blank");
		}
		final Sensor sensor = sensorDB.updateById(id, dto.getBrand(), dto.getColor());
		
		return DTOConverter.convertSensorToSensorResponseDTO(sensor);
	}
	
	//-------------------------------------------------------------------------------------------------
	@DeleteMapping(path = SensorProviderWithPublishingConstants.BY_ID_PATH)
	public void removeSensorById(@PathVariable(value = SensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
		sensorDB.removeById(id);
	}
}