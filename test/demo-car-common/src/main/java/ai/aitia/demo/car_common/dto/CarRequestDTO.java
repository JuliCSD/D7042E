package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class CarRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private Integer status;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public CarRequestDTO( final Integer status) {
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final Integer status) { this.status= status; }	
}
