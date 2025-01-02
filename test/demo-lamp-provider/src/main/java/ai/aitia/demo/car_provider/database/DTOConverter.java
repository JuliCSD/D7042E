package ai.aitia.demo.lamp_provider.database;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import ai.aitia.demo.lamp_common.dto.LampResponseDTO;
import ai.aitia.demo.lamp_provider.entity.Lamp;

public class DTOConverter {

	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static LampResponseDTO convertLampToLampResponseDTO(final Lamp lamp) {
		Assert.notNull(lamp, "lamp is null");
		return new LampResponseDTO(lamp.getId(), lamp.getGroup(), lamp.getStatus());
	}
	
	//-------------------------------------------------------------------------------------------------
	public static List<LampResponseDTO> convertLampListToLampResponseDTOList(final List<Lamp> lamps) {
		Assert.notNull(lamps, "lamp list is null");
		final List<LampResponseDTO> lampResponse = new ArrayList<>(lamps.size());
		for (final Lamp lamp : lamps) {
			lampResponse.add(convertLampToLampResponseDTO(lamp));
		}
		return lampResponse;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public DTOConverter() {
		throw new UnsupportedOperationException(); 
	}
}
