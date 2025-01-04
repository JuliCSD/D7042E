package ai.aitia.demo.sensor_common.dto;

import java.io.Serializable;

public class SensorRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private String brand;
	private String color;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public SensorRequestDTO(final String brand, final String color) {
		this.brand = brand;
		this.color = color;
	}

	//-------------------------------------------------------------------------------------------------
	public String getBrand() { return brand; }
	public String getColor() { return color; }

	//-------------------------------------------------------------------------------------------------
	public void setBrand(final String brand) { this.brand = brand; }
	public void setColor(final String color) { this.color = color; }	
}
