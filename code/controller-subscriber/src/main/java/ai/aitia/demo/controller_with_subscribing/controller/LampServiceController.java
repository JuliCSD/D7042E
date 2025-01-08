package ai.aitia.demo.controller_with_subscribing.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import ai.aitia.demo.smart_city_common.dto.LampRequestDTO;
import ai.aitia.demo.smart_city_common.dto.LampResponseDTO;
import ai.aitia.demo.controller_with_subscribing.LampProviderConstants;
import ai.aitia.demo.controller_with_subscribing.database.DTOConverter;
import ai.aitia.demo.controller_with_subscribing.database.InMemoryLampDB;
import ai.aitia.demo.controller_with_subscribing.entity.Lamp;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
@RequestMapping(LampProviderConstants.LAMP_URI)
public class LampServiceController {
	
	//=================================================================================================
	// members	
	
	@Autowired
	private InMemoryLampDB lampDB;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public List<LampResponseDTO> getLamps( @RequestParam(name = LampProviderConstants.REQUEST_PARAM_STATUS, required = false) final Integer status) {
		final List<LampResponseDTO> response = new ArrayList<>();
		for (final Lamp lamp : lampDB.getAll()) {

			int lastRequestStatus = lamp.getStatus();
			lamp.setlastRequestStatus(lastRequestStatus);

			boolean toAdd = true;
			
			if (status!=null && lamp.getStatus() != status) {
				toAdd = false;
			}
			if (toAdd) {
				response.add(DTOConverter.convertLampToLampResponseDTO(lamp));
			}
		}
		return response;
	}
	
	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = LampProviderConstants.BY_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public LampResponseDTO getLampById(@PathVariable(value = LampProviderConstants.PATH_VARIABLE_ID) final int id) {
		return DTOConverter.convertLampToLampResponseDTO(lampDB.getById(id));
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public LampResponseDTO createLamp(@RequestBody final LampRequestDTO dto) {
		
		if (dto.getStatus()>1 || dto.getStatus()<0) {
			throw new BadPayloadException("statusis null or blank");
		}
		final Lamp lamp = lampDB.create(dto.getStatus());
		return DTOConverter.convertLampToLampResponseDTO(lamp);
	}
	
	//-------------------------------------------------------------------------------------------------
	@PutMapping(path = LampProviderConstants.BY_ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public LampResponseDTO updateLamp(@PathVariable(name = LampProviderConstants.PATH_VARIABLE_ID) final int id, @RequestBody final LampRequestDTO dto) {
		
		if (dto.getStatus()>1 || dto.getStatus()<0) {
			throw new BadPayloadException("statusis null or blank");
		}
		final Lamp lamp = lampDB.updateById(id, dto.getStatus());
		return DTOConverter.convertLampToLampResponseDTO(lamp);
	}
	
	
	//-------------------------------------------------------------------------------------------------
	@DeleteMapping(path = LampProviderConstants.BY_ID_PATH)
	public void removeLampById(@PathVariable(value = LampProviderConstants.PATH_VARIABLE_ID) final int id) {
		lampDB.removeById(id);
	}
}
