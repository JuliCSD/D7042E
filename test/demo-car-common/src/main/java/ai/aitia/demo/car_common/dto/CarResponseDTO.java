package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class CarResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private String group;
	private Integer status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public CarResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public CarResponseDTO(final int id, final String group, final Integer status) {
		this.id = id;
		this.group = group;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getGroup() { return group; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setGroup(final String group) { this.group = group; }
	public void setStatus(final Integer status) { this.status= status; }	
}
