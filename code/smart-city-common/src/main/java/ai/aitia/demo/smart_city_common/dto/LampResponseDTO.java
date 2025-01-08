package ai.aitia.demo.smart_city_common.dto;

import java.io.Serializable;

public class LampResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private Integer status;
	private Integer lastRequestStatus;
	private boolean sendToLamp;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LampResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public LampResponseDTO(final int id, final Integer status, final Integer lastRequestStatus, final boolean sendToLamp) {
		this.id = id;
		this.status= status;
		this.lastRequestStatus = lastRequestStatus;
		this.sendToLamp = sendToLamp;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public Integer getStatus() { return status; }
	public Integer getlastRequestStatus() { return lastRequestStatus; }
	public boolean getSendToLamp() { return sendToLamp; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setStatus(final Integer status) { this.status= status; }	
	public void setlastRequestStatus(final Integer lastRequestStatus) { this.lastRequestStatus = lastRequestStatus; }
	public void setSendToLamp(final boolean sendToLamp) { this.sendToLamp = sendToLamp; }
}
