package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class CarResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id; 
	private int status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LightsResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public LightsResponseDTO(final int id, final int status) {
		this.id = id;
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public int getStatus() { return status; } 


	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setStatus(final int status) { this.status = status; } 	
}
