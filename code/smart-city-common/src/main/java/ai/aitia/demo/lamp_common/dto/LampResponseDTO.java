package ai.aitia.demo.lamp_common.dto;

import java.io.Serializable;

public class LampResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private Integer status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LampResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public LampResponseDTO(final int id, final Integer status) {
		this.id = id;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setStatus(final Integer status) { this.status= status; }	
}
