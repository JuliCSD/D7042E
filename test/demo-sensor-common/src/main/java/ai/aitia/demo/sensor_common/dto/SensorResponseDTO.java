package ai.aitia.demo.sensor_common.dto;

import java.io.Serializable;

public class SensorResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private String name;
	private String value;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public SensorResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public SensorResponseDTO(final int id, final String name, final String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getName() { return name; }
	public String getValue() { return value; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setName(final String name) { this.name = name; }
	public void setValue(final String value) { this.value = value; }	
}
