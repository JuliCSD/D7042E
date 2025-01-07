package ai.aitia.demo.sensor_measurements.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.sensor.measurements.common.EFCommonConstants;
import ai.aitia.demo.sensor.measurements.common.EFUtilities;
import ai.aitia.demo.sensor.measurements.common.dto.SensorMeasurementsDTO;
import ai.aitia.demo.sensor_measurements.provider.service.SensorMeasurementsService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
public class SensorServiceController {
	
	//=================================================================================================
	// members

	@Autowired
	private SensorMeasurementsService sensorMeasurementsService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = EFCommonConstants.SENSOR_MEASUREMENTS_SERVICE_URI)
	@ResponseBody public SensorMeasurementsDTO getSensorMeasurementsService(@RequestParam(name = EFCommonConstants.REQUEST_PARAM_BUILDING) final long building,
													  				@RequestParam(name = EFCommonConstants.REQUEST_PARAM_TIMESTAMP) final long timestamp) throws IOException, URISyntaxException {
		if (timestamp <= EFUtilities.nowUTCSeconds()) {
			throw new BadPayloadException("timestamp cannot be in the past");
		}
		return sensorMeasurementsService.measurements(building, timestamp);
	}
}