package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class CarResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private String status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public CarResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public CarResponseDTO(final int id, final String status) {
		this.id = id;
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setStatus(final String status) { this.status = status; }
}
