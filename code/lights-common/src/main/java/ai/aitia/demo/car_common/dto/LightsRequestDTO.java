package ai.aitia.demo.lights_common.dto;

import java.io.Serializable;

public class LightsRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private int id;
	private int status;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public LightsRequestDTO(final int status, final int id) {
		this.id = id;
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getStatus() { return status; } 

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final int status) { this.status = status; } 
}
