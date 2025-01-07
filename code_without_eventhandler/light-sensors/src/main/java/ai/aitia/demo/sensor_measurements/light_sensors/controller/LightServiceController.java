package ai.aitia.demo.sensor_measurements.light_sensors.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.sensor.measurements.common.EFCommonConstants;
import ai.aitia.demo.sensor.measurements.common.EFUtilities;
import ai.aitia.demo.sensor.measurements.common.dto.SensorDetailsListDTO;
import ai.aitia.demo.sensor_measurements.light_sensors.service.LightService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
public class LightServiceController {
	
	//=================================================================================================
	// members
	
	@Autowired
	private LightService lightService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = EFCommonConstants.LIGHT_SENSOR_DETAILS_SERVICE_URI)
	@ResponseBody public SensorDetailsListDTO getLightSensorDetails(@RequestParam(name = EFCommonConstants.REQUEST_PARAM_BUILDING) final long building,
														  			 @RequestParam(name = EFCommonConstants.REQUEST_PARAM_FROM) final long fromTimestamp,
														  			 @RequestParam(name = EFCommonConstants.REQUEST_PARAM_TO, required = false) Long toTimestamp) {
		
		if (fromTimestamp < 0) {
			throw new BadPayloadException("fromTimestamp cannot be less than zero");
		}
		
		if (fromTimestamp > EFUtilities.nowUTCSeconds() || toTimestamp > EFUtilities.nowUTCSeconds()) {
			throw new BadPayloadException("fromTimestamp or toTimestamp cannot be in the future");
		}
		
		toTimestamp = toTimestamp == null || toTimestamp < fromTimestamp ? fromTimestamp : toTimestamp; 
		
		return lightService.getHourlySensorDetails(building, fromTimestamp, toTimestamp);
	}
}