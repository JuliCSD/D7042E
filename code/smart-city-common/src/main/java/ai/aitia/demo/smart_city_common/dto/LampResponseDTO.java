package ai.aitia.demo.smart_city_common.dto;

import java.io.Serializable;

public class LampResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private Integer status;
	private Integer lastRequestStatus;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LampResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public LampResponseDTO(final int id, final Integer status, final Integer lastRequestStatus) {
		this.id = id;
		this.status= status;
		this.lastRequestStatus = lastRequestStatus;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public Integer getStatus() { return status; }
	public Integer getlastRequestStatus() { return lastRequestStatus; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setStatus(final Integer status) { this.status= status; }	
	public void setlastRequestStatus(final Integer lastRequestStatus) { this.lastRequestStatus = lastRequestStatus; }
}
