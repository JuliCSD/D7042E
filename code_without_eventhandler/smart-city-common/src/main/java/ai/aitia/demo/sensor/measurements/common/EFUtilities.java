package ai.aitia.demo.sensor.measurements.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EFUtilities {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static long nowUTCSeconds() {
		return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public EFUtilities() {		
		throw new UnsupportedOperationException();
	}
}
