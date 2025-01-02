package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class LampRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private int status;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public LampRequestDTO(final int status) {
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final int status) { this.status = status; }	
}
