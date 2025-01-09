package ai.aitia.demo.smart_city_common.dto;

import java.io.Serializable;

public class LightSensorResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private String value;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LightSensorResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public LightSensorResponseDTO(final int id, final String value) {
		this.id = id;
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getValue() { return value; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setValue(final String value) { this.value = value; }	
}
