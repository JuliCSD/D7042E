package ai.aitia.demo.car_common.dto;

import java.io.Serializable;

public class CarResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private String brand;
	private Integer status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public CarResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public CarResponseDTO(final int id, final String brand, final Integer status) {
		this.id = id;
		this.brand = brand;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getBrand() { return brand; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setId(final int id) {this.id = id; }
	public void setBrand(final String brand) { this.brand = brand; }
	public void setStatus(final Integer status) { this.status= status; }	
}
