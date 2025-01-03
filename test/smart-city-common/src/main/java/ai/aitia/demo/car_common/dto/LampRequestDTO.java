package ai.aitia.demo.lamp_common.dto;

import java.io.Serializable;

public class LampRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private String group;
	private Integer status;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public LampRequestDTO(final String group, final Integer status) {
		this.group = group;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public String getGroup() { return group; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setGroup(final String group) { this.group = group; }
	public void setStatus(final Integer status) { this.status= status; }	
}