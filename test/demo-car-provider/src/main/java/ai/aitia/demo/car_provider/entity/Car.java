package ai.aitia.demo.car_provider.entity;

public class Car {

	//=================================================================================================
	// members

	private final int id;
	private String brand;
	private Integer status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Car(final int id, final String brand, final Integer status) {
		this.id = id;
		this.brand = brand;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getBrand() { return brand; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setBrand(final String brand) { this.brand = brand; }
	public void setStatus(final Integer status) { this.status= status; }	
}
