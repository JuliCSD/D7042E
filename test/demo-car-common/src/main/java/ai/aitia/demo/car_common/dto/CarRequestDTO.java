package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class CarRequestDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private String brand;
	private Integer status;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public CarRequestDTO(final String brand, final Integer status) {
		this.brand = brand;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public String getBrand() { return brand; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setBrand(final String brand) { this.brand = brand; }
	public void setStatus(final Integer status) { this.status= status; }	
}
