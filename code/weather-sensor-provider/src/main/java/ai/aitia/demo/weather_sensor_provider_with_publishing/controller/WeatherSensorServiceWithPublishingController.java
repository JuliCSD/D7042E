package ai.aitia.demo.weather_sensor_provider_with_publishing.controller;

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

import ai.aitia.demo.smart_city_common.dto.WeatherSensorRequestDTO;
import ai.aitia.demo.smart_city_common.dto.WeatherSensorResponseDTO;
import ai.aitia.demo.weather_sensor_provider_with_publishing.WeatherSensorProviderWithPublishingConstants;
import ai.aitia.demo.weather_sensor_provider_with_publishing.database.DTOConverter;
import ai.aitia.demo.weather_sensor_provider_with_publishing.database.InMemoryWeatherSensorDB;
import ai.aitia.demo.weather_sensor_provider_with_publishing.entity.WeatherSensor;
import eu.arrowhead.application.skeleton.publisher.event.EventTypeConstants;
import eu.arrowhead.application.skeleton.publisher.event.PresetEventType;
import eu.arrowhead.application.skeleton.publisher.service.PublisherService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
@RequestMapping(WeatherSensorProviderWithPublishingConstants.WEATHER_SENSOR_URI)
public class WeatherSensorServiceWithPublishingController {

    private static int counter = 0;

    @Autowired
    private InMemoryWeatherSensorDB weatherSensorDB;

    @Autowired
    private PublisherService publisherService;

    @Value(WeatherSensorProviderWithPublishingConstants.$SERVICE_LIMIT_WD)
    private int serviceLimit;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<WeatherSensorResponseDTO> getWeatherSensors(@RequestParam(name = WeatherSensorProviderWithPublishingConstants.REQUEST_PARAM_TEMPERATURE, required = false) final String temperature,
                                              @RequestParam(name = WeatherSensorProviderWithPublishingConstants.REQUEST_PARAM_HUMIDITY, required = false) final String humidity,
                                              @RequestParam(name = WeatherSensorProviderWithPublishingConstants.REQUEST_PARAM_PRESSURE, required = false) final String pressure,
                                              @RequestParam(name = WeatherSensorProviderWithPublishingConstants.REQUEST_PARAM_WIND, required = false) final String wind) {
        ++counter;

        publisherService.publish(PresetEventType.REQUEST_RECEIVED, Map.of(EventTypeConstants.EVENT_TYPE_REQUEST_RECEIVED_METADATA_REQUEST_TYPE, HttpMethod.GET.name()), WeatherSensorProviderWithPublishingConstants.WEATHER_SENSOR_URI);

        final List<WeatherSensorResponseDTO> response = new ArrayList<>();
        for (final WeatherSensor weatherSensor : weatherSensorDB.getAll()) {
            boolean toAdd = true;
            if (temperature != null && !temperature.isBlank() && !weatherSensor.getTemperature().equalsIgnoreCase(temperature)) {
                toAdd = false;
            }
            if (humidity != null && !humidity.isBlank() && !weatherSensor.getHumidity().equalsIgnoreCase(humidity)) {
                toAdd = false;
            }
            if (pressure != null && !pressure.isBlank() && !weatherSensor.getPressure().equalsIgnoreCase(pressure)) {
                toAdd = false;
            }
            if (wind != null && !wind.isBlank() && !weatherSensor.getWind().equalsIgnoreCase(wind)) {
                toAdd = false;
            }
            if (toAdd) {
                response.add(DTOConverter.convertWeatherSensorToWeatherSensorResponseDTO(weatherSensor));
            }
        }

        if (counter > serviceLimit) {
            System.exit(0);
        }

        return response;
    }

    @GetMapping(path = WeatherSensorProviderWithPublishingConstants.BY_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WeatherSensorResponseDTO getWeatherSensorById(@PathVariable(value = WeatherSensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
        return DTOConverter.convertWeatherSensorToWeatherSensorResponseDTO(weatherSensorDB.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WeatherSensorResponseDTO createWeatherSensor(@RequestBody final WeatherSensorRequestDTO dto) {
        if (dto.getTemperature() == null || dto.getTemperature().isBlank()) {
            throw new BadPayloadException("temperature is null or blank");
        }
        if (dto.getHumidity() == null || dto.getHumidity().isBlank()) {
            throw new BadPayloadException("humidity is null or blank");
        }
        if (dto.getPressure() == null || dto.getPressure().isBlank()) {
            throw new BadPayloadException("pressure is null or blank");
        }
        if (dto.getWind() == null || dto.getWind().isBlank()) {
            throw new BadPayloadException("wind is null or blank");
        }
        final WeatherSensor weatherSensor = weatherSensorDB.create(dto.getTemperature(), dto.getHumidity(), dto.getPressure(), dto.getWind());

        return DTOConverter.convertWeatherSensorToWeatherSensorResponseDTO(weatherSensor);
    }

    @PutMapping(path = WeatherSensorProviderWithPublishingConstants.BY_ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WeatherSensorResponseDTO updateWeatherSensor(@PathVariable(name = WeatherSensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id, @RequestBody final WeatherSensorRequestDTO dto) {
        if (dto.getTemperature() == null || dto.getTemperature().isBlank()) {
            throw new BadPayloadException("temperature is null or blank");
        }
        if (dto.getHumidity() == null || dto.getHumidity().isBlank()) {
            throw new BadPayloadException("humidity is null or blank");
        }
        if (dto.getPressure() == null || dto.getPressure().isBlank()) {
            throw new BadPayloadException("pressure is null or blank");
        }
        if (dto.getWind() == null || dto.getWind().isBlank()) {
            throw new BadPayloadException("wind is null or blank");
        }
        final WeatherSensor weatherSensor = weatherSensorDB.updateById(id, dto.getTemperature(), dto.getHumidity(), dto.getPressure(), dto.getWind());

        return DTOConverter.convertWeatherSensorToWeatherSensorResponseDTO(weatherSensor);
    }

    @DeleteMapping(path = WeatherSensorProviderWithPublishingConstants.BY_ID_PATH)
    public void removeWeatherSensorById(@PathVariable(value = WeatherSensorProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
        weatherSensorDB.removeById(id);
    }
}