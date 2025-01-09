package ai.aitia.demo.light_sensor_provider_with_publishing.controller;

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

import ai.aitia.demo.smart_city_common.dto.LightSensorRequestDTO;
import ai.aitia.demo.smart_city_common.dto.LightSensorResponseDTO;
import ai.aitia.demo.light_sensor_provider_with_publishing.LightSensorProviderWithPublishingConstants;
import ai.aitia.demo.light_sensor_provider_with_publishing.database.DTOConverter;
import ai.aitia.demo.light_sensor_provider_with_publishing.database.InMemoryLightSensorDB;
import ai.aitia.demo.light_sensor_provider_with_publishing.entity.LightSensor;
import eu.arrowhead.application.skeleton.publisher.event.EventTypeConstants;
import eu.arrowhead.application.skeleton.publisher.event.PresetEventType;
import eu.arrowhead.application.skeleton.publisher.service.PublisherService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
@RequestMapping(LightSensorProviderWithPublishingConstants.LIGHT_SENSOR_URI)
public class LightSensorServiceWithPublishingController {

    private static int counter = 0;

    @Autowired
    private InMemoryLightSensorDB lightSensorDB;

    @Autowired
    private PublisherService publisherService;

    @Value(LightSensorProviderWithPublishingConstants.$SERVICE_LIMIT_WD)
    private int serviceLimit;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LightSensorResponseDTO> getLightSensors( @RequestParam(name = LightSensorProviderWithPublishingConstants.REQUEST_PARAM_VALUE, required = false) final String value) {
        ++counter;

        publisherService.publish(PresetEventType.REQUEST_RECEIVED, Map.of(EventTypeConstants.EVENT_TYPE_REQUEST_RECEIVED_METADATA_REQUEST_TYPE, HttpMethod.GET.name()), LightSensorProviderWithPublishingConstants.LIGHT_SENSOR_URI);

        final List<LightSensorResponseDTO> response = new ArrayList<>();
        for (final LightSensor lightSensor : lightSensorDB.getAll()) {
            boolean toAdd = true;
            
            if (value != null && !value.isBlank() && !lightSensor.getValue().equalsIgnoreCase(value)) {
                toAdd = false;
            }
            if (toAdd) {
                response.add(DTOConverter.convertLightSensorToLightSensorResponseDTO(lightSensor));
            }
        }

        if (counter > serviceLimit) {
            System.exit(0);
        }

        return response;
    }

    @GetMapping(path = LightSensorProviderWithPublishingConstants.BY_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LightSensorResponseDTO getLightSensorById(@PathVariable(value = LightSensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
        return DTOConverter.convertLightSensorToLightSensorResponseDTO(lightSensorDB.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LightSensorResponseDTO createLightSensor(@RequestBody final LightSensorRequestDTO dto) {
        
        if (dto.getValue() == null || dto.getValue().isBlank()) {
            throw new BadPayloadException("value is null or blank");
        }
        final LightSensor lightSensor = lightSensorDB.create( dto.getValue());

        return DTOConverter.convertLightSensorToLightSensorResponseDTO(lightSensor);
    }

    @PutMapping(path = LightSensorProviderWithPublishingConstants.BY_ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LightSensorResponseDTO updateLightSensor(@PathVariable(name = LightSensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id, @RequestBody final LightSensorRequestDTO dto) {
        
        if (dto.getValue() == null || dto.getValue().isBlank()) {
            throw new BadPayloadException("value is null or blank");
        }
        final LightSensor lightSensor = lightSensorDB.updateById(id, dto.getValue());

        return DTOConverter.convertLightSensorToLightSensorResponseDTO(lightSensor);
    }

    @DeleteMapping(path = LightSensorProviderWithPublishingConstants.BY_ID_PATH)
    public void removeLightSensorById(@PathVariable(value = LightSensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
        lightSensorDB.removeById(id);
    }
}