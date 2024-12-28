package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class CarRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private String status;
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public CarRequestDTO(final String status) {
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public String getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final String status) { this.status = status; }
}
