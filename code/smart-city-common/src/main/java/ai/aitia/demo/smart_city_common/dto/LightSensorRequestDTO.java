package ai.aitia.demo.smart_city_common.dto;

import java.io.Serializable;

public class LightSensorRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private String name;
	private String value;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public LightSensorRequestDTO(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public String getName() { return name; }
	public String getValue() { return value; }

	//-------------------------------------------------------------------------------------------------
	public void setName(final String name) { this.name = name; }
	public void setValue(final String value) { this.value = value; }	
}