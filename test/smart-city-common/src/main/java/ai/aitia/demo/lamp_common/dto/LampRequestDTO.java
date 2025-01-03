package ai.aitia.demo.lamp_common.dto;

import java.io.Serializable;

public class LampRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;
 
	private Integer status;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public LampRequestDTO(  final Integer status) { 
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
 
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
 
	public void setStatus(final Integer status) { this.status= status; }	
}
